#!/usr/bin/env bash
set -e

# 脚本当前目录
cPath=$(cd $(dirname "$0") || exit; pwd)

# 根目录
dirPath=$(dirname "$cPath")

# 获取端口
port="$1"
if [[ ! "$port" ]]; then
  port=6379
fi

# 创建数据目录
mkdir -p "$dirPath"/redis/data_"$port"

# 删除已启动服务
containerId=$(docker ps -a | grep "redis_$port" | awk -F' ' '{print $1}')
if [[ "$containerId" ]]; then
    docker rm -f ${containerId} > /dev/null
fi

# 启动服务
containerName=redis_"$port"
docker run -itd --privileged=true -p "$port":6379 --name ${containerName} \
-v="$dirPath"/conf/redis.conf:/etc/redis/redis.conf \
-v="$dirPath"/redis/data_"$port":/data \
redis \
redis-server /etc/redis/redis.conf > /dev/null

# 获取容器IP地址
dockerIp=$(docker inspect -f "{{.NetworkSettings.IPAddress}}" "$containerName")

# 获取容器启动状态
isRunning=$(docker inspect -f "{{.State.Running}}" "$containerName")
if [[ "$isRunning" == "true" ]]; then
    echo "容器：$containerName - IP：$dockerIp - 启动成功"
fi