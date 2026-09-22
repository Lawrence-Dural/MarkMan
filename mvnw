#!/bin/sh
# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup script, version 3.3.4
#
# Downloads and runs the Maven version declared in
# .mvn/wrapper/maven-wrapper.properties, so contributors don't need Maven
# pre-installed. Requires only a JDK and internet access on first run.
# ----------------------------------------------------------------------------

set -e

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

if [ -z "$JAVA_HOME" ]; then
  JAVA_CMD="java"
else
  JAVA_CMD="$JAVA_HOME/bin/java"
fi

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper..."
  WRAPPER_URL=$(grep '^wrapperUrl=' "$WRAPPER_PROPERTIES" | cut -d'=' -f2-)
  mkdir -p "$BASE_DIR/.mvn/wrapper"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$WRAPPER_JAR" "$WRAPPER_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$WRAPPER_JAR" "$WRAPPER_URL"
  else
    echo "Error: need curl or wget to download the Maven Wrapper jar." >&2
    exit 1
  fi
fi

exec "$JAVA_CMD" \
  -classpath "$WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=$BASE_DIR" \
  "$WRAPPER_LAUNCHER" "$@"
