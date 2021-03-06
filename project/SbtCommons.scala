import com.typesafe.sbt.SbtScalariform.autoImport.scalariformPreferences
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.headerLicense
import de.heikoseeberger.sbtheader.License
import sbt.Keys._
import sbt._
import sbtprotoc.ProtocPlugin.autoImport.PB

import scalariform.formatter.preferences._

object SbtCommons {

  val scalariformPrefs = scalariformPreferences := scalariformPreferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(RewriteArrowSymbols, true)
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(SpaceBeforeContextColon, true)
    .setPreference(NewlineAtEndOfFile, true)

  val scalaV = scalaVersion := "2.12.4"

  val commons = Seq(
    scalaV,
    scalariformPrefs,
    version := "0.1",
    fork in test := true,
    parallelExecution in Test := false,
    organizationName := "Dmitry Sergeev",
    organizationHomepage := None,
    startYear := Some(2017),
    licenses += ("AGPL-3.0", new URL("http://www.gnu.org/licenses/agpl-3.0.en.html")),
    headerLicense := Some(License.AGPLv3("2017", organizationName.value))
  )

  val Cats1V = "1.0.1"
  val ScodecBitsV = "1.1.5"
  val ScodecCoreV = "1.10.3"
  val RocksDbV = "5.9.2"
  val TypeSafeConfV = "1.3.2"
  val FicusV = "1.4.3"
  val MockitoV = "2.13.0"
  val MonocleV = "1.5.0-cats"
  val CirceV = "0.9.1"
  val AirflameV = "0.38"
  val ScalatestV = "3.0.+"
  val SloggingV = "0.6.1"

  val slogging = "biz.enef" %% "slogging" % SloggingV
  val sloggingSlf4j = "biz.enef" %% "slogging-slf4j" % SloggingV

  val cats1 = "org.typelevel" %% "cats-core" % Cats1V
  val monix3 = "io.monix" %% "monix" % "3.0.0-M3"
  val shapeless = "com.chuusai" %% "shapeless" % "2.3.+"
  val monocle = "com.github.julien-truffaut" %% "monocle-core" % MonocleV
  val monocleMacro = "com.github.julien-truffaut" %% "monocle-macro" % MonocleV
  val scodecBits = "org.scodec" %% "scodec-bits" % ScodecBitsV
  val scodecCore = "org.scodec" %% "scodec-core" % ScodecCoreV
  val bouncyCastle = "org.bouncycastle" % "bcprov-jdk15on" % "1.59"

  val circeCore = "io.circe" %% "circe-core" % CirceV
  val circeParser = "io.circe" %% "circe-parser" % CirceV

  val rocksDb = "org.rocksdb" % "rocksdbjni" % RocksDbV
  val typeSafeConfig = "com.typesafe" % "config" % TypeSafeConfV
  val ficus = "com.iheart" %% "ficus" % FicusV

  val mockito = "org.mockito" % "mockito-core" % MockitoV % Test
  val scalatestKit = "org.scalatest" %% "scalatest" % ScalatestV
  val scalatest = scalatestKit % Test

  val nscala_time = "com.github.nscala-time" %% "nscala-time" % "2.18.0"

  val protobuf = Seq(
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    ),
    libraryDependencies ++= Seq(
      "com.trueaccord.scalapb" %% "scalapb-runtime" % com.trueaccord.scalapb.compiler.Version.scalapbVersion % "protobuf"
    )
  )

  val grpc = protobuf ++ Seq(
    libraryDependencies ++= Seq(
      "io.grpc" % "grpc-netty" % com.trueaccord.scalapb.compiler.Version.grpcJavaVersion,
      "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % com.trueaccord.scalapb.compiler.Version.scalapbVersion
    )
  )
  val chill = "com.twitter" %% "chill" % "0.9.2"
}
