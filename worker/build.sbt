name := "piezo-worker"

organization := "com.lucidchart"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.1"

exportJars := true

exportJars in Test := false

autoScalaLibrary := true

retrieveManaged := true

mainClass := Some("com.lucidchart.piezo.Worker")

libraryDependencies ++= Seq(
	"org.specs2" %% "specs2" % "1.14" % "test",
	"ch.qos.logback" % "logback-classic" % "1.0.1",
	"org.quartz-scheduler" % "quartz" % "2.1.7",
	"mysql" % "mysql-connector-java" % "5.1.25",
	"javax.transaction" % "jta" % "1.1",
	"net.databinder" %% "dispatch-http" % "0.8.8",
	"org.skife.com.typesafe.config" % "typesafe-config" % "0.3.0",
	"net.liftweb" %% "lift-json" % "2.5.1",
	"com.typesafe.akka" %% "akka-actor" % "2.1.0"
)

resolvers ++= List(
	DefaultMavenRepository,
	"Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
	"Staging Sonatype repository" at "https://oss.sonatype.org/content/groups/staging/"
)

TaskKey[Set[File]]("stage") <<= (fullClasspath in Runtime, target) map { (cp, out) =>
  val entries: Seq[File] = cp.files
  val toDirectory: File = out / "staged"
  IO.copy( entries x flat(toDirectory) )
}

mappings in (Compile, packageBin) ~= { (ms: Seq[(File, String)]) =>
  ms filter {
    case (file, toPath) =>
      !excludeFileRegex.pattern.matcher(file.getName).matches
  }
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo <<= (version) {
	version: String =>
		val lucidRepo = "http://repo.lucidchart.com:8081/artifactory/"
		if (version.trim.endsWith("SNAPSHOT")) {
			Some("snapshots" at lucidRepo + "libs-snapshot-local/")
		}
		else {
			Some("releases" at lucidRepo + "libs-release-local/")
		}
}