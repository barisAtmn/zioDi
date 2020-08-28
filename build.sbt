name := "ZioDi"

version := "0.1"

scalaVersion := "2.13.3"

//ZIO
libraryDependencies += "dev.zio" %% "zio" % "1.0.1"
libraryDependencies += "dev.zio" %% "zio-test" % "1.0.1"
libraryDependencies += "dev.zio" %% "zio-interop-cats" % "2.1.4.0"

// HTTP4S
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % "1.0.0-M4"
libraryDependencies += "org.http4s" %% "http4s-circe" % "1.0.0-M4"
libraryDependencies += "org.http4s" %% "http4s-dsl" % "1.0.0-M4"

// DOOBIE
libraryDependencies +=  "org.tpolecat" %% "doobie-core" % "0.9.0"
libraryDependencies +=  "org.tpolecat" %% "doobie-h2" % "0.9.0"

// Config
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.13.0"



