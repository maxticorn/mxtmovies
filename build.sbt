ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "2.13.8"

lazy val mxtmovies = (project in file("."))
  .settings(name := "mxtmovies")

val zioVersion       = "2.0.0"
val zioConfigVersion = "3.0.1"
val zioCacheVersion  = "0.2.0"
val zioCatsVersion   = "3.3.0"
val tapirVersion     = "1.0.3"
val http4sVersion    = "0.23.12"
val doobieVersion    = "1.0.0-RC1"
val slf4jVersion     = "2.0.0-alpha7"
val chimneyV         = "0.6.2"

libraryDependencies ++= List(
  "dev.zio"                     %% "zio"                     % zioVersion,
  "dev.zio"                     %% "zio-test"                % zioVersion % "test",
  "dev.zio"                     %% "zio-test-sbt"            % zioVersion % "test",
  "dev.zio"                     %% "zio-config"              % zioConfigVersion,
  "dev.zio"                     %% "zio-config-typesafe"     % zioConfigVersion,
  "dev.zio"                     %% "zio-config-magnolia"     % zioConfigVersion,
  "dev.zio"                     %% "zio-cache"               % zioCacheVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-core"              % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
  "org.http4s"                  %% "http4s-core"             % http4sVersion,
  "org.http4s"                  %% "http4s-blaze-server"     % http4sVersion,
  "org.tpolecat"                %% "doobie-core"             % doobieVersion,
  "org.tpolecat"                %% "doobie-postgres"         % doobieVersion,
  "org.tpolecat"                %% "doobie-hikari"           % doobieVersion,
  "dev.zio"                     %% "zio-interop-cats"        % zioCatsVersion,
  "org.slf4j"                    % "slf4j-api"               % slf4jVersion,
  "org.slf4j"                    % "slf4j-simple"            % slf4jVersion,
  "io.scalaland"                %% "chimney"                 % chimneyV
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
