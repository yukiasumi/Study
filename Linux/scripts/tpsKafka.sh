#!/bin/bash
topic="$1"
id=$(./kafka.sh 4 "$topic")startTime=$(date +%s)
startK=${id:46}
while true;
    do
    id=$( ./ kafka.sh 4 "$topic")final=${id:46}
    sleep 2.5;
    id=$( . / kafka.sh 4 "$topic")check=${id:46}
    if ((final==check)); then 
        endTime=$(date +%s)
        diff=$(($final-$startK))
        interval=$(($endTime-$startTime))
        echo 初始"$startK"条
        echo 结束"$final"条
        echo 新增"$diff"条
        echo 用时"$interval"秒
        tps=$(echo "scale=1;$diff/$interval"|bc)
        echo '平均每秒新增(tps)' : $tps
        break;
    fi
done;
