#!/bin/bash
echo '===========$n============'
echo script name: $(basename $0 .sh)
echo script path: $(cd $(dirname $0); pwd) 
echo 1st paramater: $1
echo 2nd parameter: $2
echo '===========$#============'
echo parameter numbers: $#
echo '===========$*============'
echo $*
echo '===========$@============'
echo $@
