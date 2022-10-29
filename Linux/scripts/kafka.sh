#!/bin/bash
topic=$2
case $1 in
	1)kafka-topics.sh --bootstrap-server 192.168.10.130:9092 --list;;
	2)kafka-console-consumer.sh --bootstrap-server 192.168.10.130:9092 --topic $topic --from-beginning;;
	3)kafka-topics.sh --bootstrap-server 192.168.10.130:9092 --delete --topic $topic;;
	4)kafka-run-class.sh  kafka.tools.GetOffsetShell --broker-list 192.168.10.130:9092 --topic $topic --time -1;;
	5)kafka-console-producer.sh --broker-list 192.168.10.130:9092 --topic $topic;;
	6)kafka-console-consumer.sh --bootstrap-server 192.168.10.130:9092 --topic $topic --from-beginning --max-messages 1 --property print.timestamp=true;;
	7)result=$(kafka-run-class.sh  kafka.tools.GetOffsetShell --broker-list 192.168.10.130:9092 --topic $topic --time -1)
	  count=${result:$((${#topic}+3))}
	  kafka-console-consumer.sh --bootstrap-server 192.168.10.130:9092 --topic $topic --offset $(($count-1)) --max-messages 1 --property print.timestamp=true --partition 0;;
	8)kafka-topics.sh --bootstrap-server 192.168.10.130:9092 --create --topic $topic --partitions 1 --replication-factor 1;;
esac
