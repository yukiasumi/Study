#!/bin/bash

# 用for进行实现
for (( i=1; i <= $1; i++  ))
do
	sum=$[ $sum + $i ]
done
echo $sum

# 用while做一个实现
a=1
while [ $a -le $1 ]
do
#	sum2=$[ $sum2 + $a ]
#	a=$[$a + 1]
	let sum2+=a
	let a++
done
echo $sum2
