lazy val scalaV = "2.11.7"

name := "emishviz"
version := "1.0-SNAPSHOT"

val sharedDependencies = Def.setting(Seq(
  "com.lihaoyi" %%% "upickle" % "0.4.3"
))
val clientDependencies = Def.setting(sharedDependencies.value ++ Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "org.singlespaced" %%% "scalajs-d3" % "0.3.2"
))
val serverDependencies = Def.setting(sharedDependencies.value ++ Seq(

))

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  libraryDependencies ++= serverDependencies.value,
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  //  pipelineStages := Seq(digest, gzip),
  compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline
).enablePlugins(PlayScala, LauncherJarPlugin).dependsOn(crossJvm)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  libraryDependencies ++= clientDependencies.value
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).dependsOn(crossJS)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).settings(
  scalaVersion := scalaV,
  libraryDependencies ++= sharedDependencies.value
).jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val crossJvm = shared.jvm
lazy val crossJS = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value