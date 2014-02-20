#!/bin/bash



if [ -z "$ANDROID_HOME" ]; then
  export ANDROID_HOME="$HOME/opt/android-sdk"
fi

export MAVEN_OPTS="-Xmx1512m -XX:MaxPermSize=128m"

mvn clean install -DskipTests

