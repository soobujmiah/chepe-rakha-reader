#!/bin/bash
##############################################################################
# Chepe Rakha Reader — Gradle Wrapper Script
# Downloads and executes Gradle wrapper
##############################################################################

APP_NAME="Chepe Rakha Reader"
WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"
WRAPPER_PROPERTIES="gradle/wrapper/gradle-wrapper.properties"

# Default JVM args
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# OS specific support
CYGWIN=false
MSYS=false
DERIVE_OS_TYPE=""
OS-specific
case "`uname`" in
    CYGWIN*)
        CYGWIN=true
        ;;
    MINGW*)
        MSYS=true
        ;;
esac

# Get the directory of this script
PRG="$0"
while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
DIR=$(cd -P "$(dirname "$PRG")" && pwd)

# Use Java home from environment or system default
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Check if jar exists
if [ ! -e "$WRAPPER_JAR" ]; then
    echo "ERROR: $WRAPPER_JAR not found"
    echo "Download from: https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
    exit 1
fi

# Execute Gradle
exec "$JAVACMD" $DEFAULT_JVM_OPTS -classpath "\"$DIR/$WRAPPER_JAR\"" \
    org.gradle.wrapper.GradleWrapperMain "$@"
