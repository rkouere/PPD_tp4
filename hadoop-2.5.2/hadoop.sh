#!/bin/bash

hdfs namenode -format
sbin/start-dfs.sh
sbin/start-yarn.sh


hdfs dfs -mkdir /user
hdfs dfs -mkdir /user/echallier


hdfs dfs -mkdir /user/echallier/ana
hdfs dfs -put words /user/echallier/ana