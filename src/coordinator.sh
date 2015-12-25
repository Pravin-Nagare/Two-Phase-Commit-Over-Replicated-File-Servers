#!/bin/bash +vx
LIB_PATH=$".:sqlite-jdbc-3.8.6.jar:/home/yaoliu/src_code/local/libthrift-1.0.0.jar:/home/yaoliu/src_code/local/slf4j-log4j12-1.5.8.jar:/home/yaoliu/src_code/local/slf4j-api-1.5.8.jar:/home/yaoliu/src_code/local/log4j-1.2.14.jar"
#<ip> <port> —-operation <operation> —-filename <fileName> —-user <ownerName>
java -classpath $LIB_PATH coordinator $1 $2
