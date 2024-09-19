#!/bin/sh

echo "The application will start in ${SLEEP_TIME}s..." && sleep ${SLEEP_TIME}
# JAVA_OPTS e.g. -Dspring.profiles.active=demo
exec java ${JAVA_OPTS} -XX:+AlwaysPreTouch -Duser.home=/tmp -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "${START_CLASS}" "$@"