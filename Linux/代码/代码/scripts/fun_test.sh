#!/bin/bash

function add(){
	s=$[$1 + $2]
	echo $s
}

read -p "请输入第一个整数：" a
read -p "请输入第二个整数：" b

sum=$(add $a $b)
echo "和："$sum
echo "和的平方："$[$sum * $sum]
