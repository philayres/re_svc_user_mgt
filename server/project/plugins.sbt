// Run "sbt eclipse" to create Eclipse project file
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.4.0")

// Run "sbt gen-idea" to create IntelliJ project file
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.2")

// Run "sbt xitrum-package" to prepare for deploying to production environment
addSbtPlugin("tv.cntt" % "xitrum-package" % "1.6")
