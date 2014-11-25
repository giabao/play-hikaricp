xerial.sbt.Sonatype.sonatypeSettings

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := <url>https://github.com/giabao/play-hikaricp</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/giabao/play-hikaricp</url>
    <connection>scm:git:git@github.com:giabao/play-hikaricp.git</connection>
    <developerConnection>scm:git:https://github.com/giabao/play-hikaricp.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>megazord</id>
      <name>Megazord</name>
      <email>contact [at] edulify.com</email>
      <url>https://github.com/megazord</url>
    </developer>
    <developer>
      <id>giabao</id>
      <name>Gia Bảo</name>
      <email>giabao@sandinh.net</email>
      <organization>Sân Đình</organization>
      <organizationUrl>http://sandinh.com</organizationUrl>
    </developer>
  </developers>
