package com.sergeev.raft.node

import com.sergeev.raft.node.environment.{ RaftNetworkEndpoint, RaftScheduler }
import com.sergeev.raft.node.message._
import com.sergeev.raft.node.role.{ RaftFollower, RaftRole }
import com.sergeev.raft.node.state.{ RaftState, RaftVolatileState }

trait RaftRouter {
  def processClientCommand(command: RaftCommand)

  def processNodeMessage(sender: NodeId, message: RaftMessage)
}

class RaftRouterImpl(context: RaftContext, network: RaftNetworkEndpoint, scheduler: RaftScheduler) extends RaftRouter {
  private var stateHolder: StateHolder[_ <: RaftState[_ <: RaftVolatileState[_], _]] = StateHolder(RaftFollower, RaftFollower.initializeState())

  override def processClientCommand(command: RaftCommand): Unit = processMessage(None, ClientCommand(command))

  override def processNodeMessage(sender: NodeId, message: RaftMessage): Unit = processMessage(Some(sender), message)

  private def processMessage(sender: Option[NodeId], inMessage: RaftMessage): Unit = {
    if (sender.nonEmpty)
      context.senderId = sender.get // TODO:

    val processingResult = stateHolder.process(inMessage)(context)
    val nextRole = processingResult._1
    val nextStateBeforeConvert = processingResult._2
    val outMessagesInfo = processingResult._3

    stateHolder = nextRole.convertState(nextStateBeforeConvert)

    for ((target, outMessage) ← outMessagesInfo)
      outMessage match {
        case RetryProcessingMessage() ⇒ processMessage(sender, inMessage)
        case _: SelfImmediateMessage  ⇒ processMessage(Some(context.selfId), outMessage)
        case _: SelfDeferredMessage ⇒ scheduler.schedule(outMessage.asInstanceOf[SelfDeferredMessage].interval, () ⇒ {
          processMessage(Some(context.selfId), outMessage)
        })
        case _: ExternalTargetMessage ⇒ network.sendMessage(target, outMessage)
      }
  }
}

case class StateHolder[S <: RaftState[_ <: RaftVolatileState[_], S]](role: RaftRole[S], state: S) {
  def process(message: RaftMessage)(context: RaftContext): ProcessingResult[S] = role.processIncoming(message, state)(context)
}

