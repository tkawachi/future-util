val commonSettings = Seq(
  organization := "com.github.tkawachi",
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/tkawachi/future-util/"),
    "scm:git:github.com:tkawachi/future-util.git"
  )),
  scalaVersion := "2.11.4",
  crossScalaVersions := Seq("2.10.4", "2.11.4"),
  doctestTestFramework := DoctestTestFramework.ScalaTest
) ++ scalariformSettings ++ doctestSettings


lazy val root = project.in(file("."))
  .settings(commonSettings :_*)
  .settings(
    name := "future-util"
  )
