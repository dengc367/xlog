#!/bin/bash

mvn clean package -Dmaven.test.skip -Dmaven.javadoc.skip

(cd /root/workspace/dap/xlog/src/java/dispatcher/target; tar -xvf xlog-dispatcher-1.0-SNAPSHOT-release.tar.gz; )

rsync -lazctv --exclude="conf" --exclude="bin" /root/workspace/dap/xlog/src/java/dispatcher/target/xlog-dispatcher-1.0-SNAPSHOT/* zhancheng.deng@10.4.19.87:~/xlog/dispatcher
