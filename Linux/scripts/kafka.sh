#!/bin/bash
host="18.7.41.209"
#参数1是操作，参数2是topic
case $1 in
#查看有哪些topic
1)
kafka-topics --bootstrap-server "$host":9092 --list|grep "$2"
;;
#查看kafka消息
2)
kafka-console-consumer --bootstrap-server "$host":9e92 --topic "$2" --from-beginning
;;
#删除topic
3)
kafka-topics --delete --bootstrap-server "$host":9092 --topic "$2"
;;
#查看topic数据条数
4)
kafka-run-class kafka.tools.GetoffsetShell --broker-list "$host":9092 --topic $2 --time -1
;;
#向topic插入数据
5)
kafka-console-producer --broker-list "$host" --topic "$2"
;;
esac
