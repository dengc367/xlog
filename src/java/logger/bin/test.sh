#!/bin/bash

#BASEDIR=`dirname $0`
source ~/.bash_profile

cd ..

if [ "$CLASSPATH" == "" ]
then
  CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
fi

LIBPATH="lib"

export LIBPATH

for f in `find $LIBPATH -name '*.jar' | sort -d`
do
  CLASSPATH=$CLASSPATH:$f
done
# ******************************************************************
# ** Set java runtime options                                     **
# ** Change 256m to higher values in case you run out of memory.  **
# ******************************************************************

#export DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=3005,server=y,suspend=n"
OPT="-Xmx2000m -Xms1000m -Xmn800m -Xss256k -XX:+UseParallelGC -XX:ParallelGCThreads=20 -cp $CLASSPATH"

# ***************
# ** Run...    **
# ***************

java $OPT com.renren.dp.xlog.logger.TestXLogLogger "${1+$@}"
