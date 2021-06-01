#!/usr/bin/env bash
set -e

# 脚本当前目录
cPath=$(cd $(dirname "$0") || exit; pwd)

# 启动集群数量
num="$1"
if [[ ! "$num" ]]; then
  num=6
fi

sPort=36378
for((i=1;i<=$num;i++)); do
    sh ${cPath}/run.sh  $(($sPort+$i))
done