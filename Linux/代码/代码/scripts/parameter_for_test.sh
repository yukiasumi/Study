#!/bin/bash

echo '=============$*================='
for para in "$*"
do
	echo $para
done

echo '=============$@================='
for para in "$@"
do
	echo $para
done
