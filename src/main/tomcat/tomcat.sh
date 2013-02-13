#!/bin/sh
#
# /etc/init.d/tomcat
#
# This is the init script for starting up the
#  Jakarta Tomcat server
#
# description: Starts and stops the Tomcat daemon.
#

tomcat=/opt/tomcat-7.0.35
startup=$tomcat/bin/startup.sh
shutdown=$tomcat/bin/shutdown.sh

start() {
  echo -n $"Starting Tomcat service: "
  sh $startup
  echo $?
}

stop() {
  echo -n $"Stopping Tomcat service: "
  sh $shutdown
  echo $?
}

restart() {
  stop
  start
}

status() {
  ps -aef | grep tomcat
}

# Handle the different input options
case "$1" in
start)
  start
  ;;
stop)
  stop
  ;;
status)
  status
  ;;
restart)
  restart
  ;;
*)
  echo $"Usage: $0 {start|stop|restart|status}"
  exit 1
esac

exit 0
