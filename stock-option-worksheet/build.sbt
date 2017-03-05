name := "stockOptionWorksheet"

version := "1.0"

lazy val `stockoptionworksheet` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc ,
  cache ,
  ws   ,
  specs2 % Test,
  "com.typesafe.play" %% "anorm" % "2.5.2",
  "mysql" % "mysql-connector-java" % "5.1.41"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  