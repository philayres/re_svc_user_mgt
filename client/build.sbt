organization := "com.consected"

name         := "re_svc_user_mgt_client"

version      := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

//------------------------------------------------------------------------------

libraryDependencies += "com.twitter" %% "finagle-http" % "6.7.4"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.5"

// JSON4S uses scalap 2.10.0, which in turn uses scala-compiler 2.10.0, which in
// turn uses scala-reflect 2.10.0. We should force "scalaVersion" above.
libraryDependencies <+= scalaVersion { sv => "org.scala-lang" % "scalap" % sv }
