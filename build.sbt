lazy val phoneCompany = (project in file(".")).settings(
  Seq(
    name := "phone-company",
    version := "0.1.0",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq (
      "com.typesafe.akka" %% "akka-stream" % "2.5.22",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )
)
