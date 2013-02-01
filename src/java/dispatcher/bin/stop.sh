#!/bin/bash

PID=`ps xu | grep com.renren.dp.xlog.dispatcher.Bootstrap | grep -v grep | awk '{print$2}'`
pid=${1:-$PID}
if kill -0 $pid > /dev/null 2>&1; then
  echo -n stopping $command
  kill $pid > /dev/null 2>&1
  while kill -0 $pid > /dev/null 2>&1; do
    echo -n "."
    sleep 1;
  done
  echo
else
  retval=$?
  echo no $command to stop because kill -0 of pid $pid failed with status $retval
fi

