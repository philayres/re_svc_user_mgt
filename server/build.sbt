organization := "com.consected"

name         := "re_svc_user_mgt"

version      := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

//------------------------------------------------------------------------------

libraryDependencies += "com.typesafe" % "config" % "1.0.2"

libraryDependencies += "com.twitter" %% "finagle-http" % "6.7.4"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.26"

libraryDependencies += "com.jolbox" % "bonecp" % "0.8.0.RELEASE"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.5"

// JSON4S uses scalap 2.10.0, which in turn uses scala-compiler 2.10.0, which in
// turn uses scala-reflect 2.10.0. We should force "scalaVersion" above.
libraryDependencies <+= scalaVersion { sv => "org.scala-lang" % "scalap" % sv }

// Put config directory in classpath for easier development --------------------

// For "sbt console"
unmanagedClasspath in Compile <+= (baseDirectory) map { bd => Attributed.blank(bd / "config") }

// For "sbt run"
unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "config") }

// Copy these to target/xitrum when "sbt xitrum-package" is run
XitrumPackage.copy("config", "db", "script")
