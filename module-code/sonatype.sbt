xerial.sbt.Sonatype.sonatypeSettings

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := <url>https://github.com/giabao/play-hikaricp.edulify.com</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/giabao/play-hikaricp.edulify.com</url>
    <connection>scm:git:git@github.com:giabao/play-hikaricp.edulify.com.git</connection>
    <developerConnection>scm:git:https://github.com/giabao/play-hikaricp.edulify.com.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>megazord</id>
      <name>Megazord</name>
      <email>contact [at] edulify.com</email>
      <url>https://github.com/megazord</url>
    </developer>
  </developers>
