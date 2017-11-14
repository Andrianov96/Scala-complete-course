import _root_.sbt.Keys.{scalaVersion, _}

//name := "scala-course"
//
//version := "1.0"
//
//scalaVersion := "2.12.2"
//
//scalacOptions := Seq(
//  "-encoding", "utf8",
//  "-feature",
//  "-unchecked",
//  "-deprecation",
//  "-target:jvm-1.8",
//  "-Ymacro-debug-lite",
//  "-language:experimental.macros")
//
//resolvers ++= Seq(
//  Resolver.jcenterRepo,
//  Resolver.url("jCenter", url("http://jcenter.bintray.com/")),
//  Resolver.sonatypeRepo("releases"),
//  Resolver.sonatypeRepo("snapshots"))
//
//libraryDependencies += "org.scala-lang" % "scala-library" % "2.12.2"
//
//libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.12.2"
//
//libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.7.2"
//
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
//
//libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test"
//
//libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
//
//libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-actor" % "2.5.3",
//  "com.typesafe.akka" %% "akka-testkit" % "2.5.3" % Test,
//  "com.typesafe.akka" %% "akka-remote" % "2.5.3",
//  "com.typesafe.akka" %% "akka-slf4j" % "2.5.3"
//)


lazy val root = (project in file (".")).
  settings(
    libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-actor" % "2.5.3",
      "com.typesafe.akka" %% "akka-testkit" % "2.5.3" % Test,
      "com.typesafe.akka" %% "akka-remote" % "2.5.3",
      "com.typesafe.akka" %% "akka-slf4j" % "2.5.3",
      "org.scala-lang" % "scala-library" % "2.12.2",
      "org.scala-lang" % "scala-reflect" % "2.12.2",
      "org.xerial" % "sqlite-jdbc" % "3.7.2",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "org.mockito" % "mockito-core" % "1.9.5" % "test",
      "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
    ),
    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.url("jCenter", url("http://jcenter.bintray.com/")),
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")),
    scalacOptions := Seq(
      "-encoding", "utf8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-target:jvm-1.8",
      "-Ymacro-debug-lite",
      "-language:experimental.macros"),
    name := "scala-course",
    version := "1.0" ,
    scalaVersion := "2.12.2"
  )

