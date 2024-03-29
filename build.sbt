name := "KanbanBoard"

version := "0.1"

scalaVersion := "2.13.8"


libraryDependencies += "org.scalafx" %% "scalafx" % "16.0.0-R24"
libraryDependencies ++= {
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
}

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.30.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test