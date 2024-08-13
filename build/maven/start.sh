#!/bin/sh

# Check if JAVA_OPTS is empty and set default values if it is
if [ -z "${JAVA_OPTS}" ]; then
    export JAVA_OPTS="-Xmx64m -Xms64m"
fi

# Check if JAVA_ENABLE_DEBUG is set and not false
if [ -n "${JAVA_ENABLE_DEBUG}" ] && [ "${JAVA_ENABLE_DEBUG}" != "false" ]; then
    java_debug_args="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=${JAVA_DEBUG_PORT:-5005}"
fi

# Execute the Java application with the provided arguments
exec java ${java_debug_args} ${JAVA_OPTS} ${JAVA_ARGS} -jar /opt/egov/*.jar
