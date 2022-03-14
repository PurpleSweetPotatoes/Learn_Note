# docker+redis集群配置

> 环境: docker + redis镜像
>
> 集群模式至少需要三主三从，所以最少需要6个节点

## redis配置文件

对普通redis配置文件做如下调整

```sh
# 注释掉ip绑定
# bind 127.0.0.1

# 关闭保护模式
protected-mode no

# 开启cluster集群
cluster-enabled yes

# 集群配置文件
cluster-config-file nodes-6379.conf

# 集群节点超时
cluster-node-timeout 15000
```

## 准备文件夹

```shell
├── conf
│   └── redis.conf
└── scripts
    ├── cluster.sh
    └── run.sh
```

## run.sh 脚本文件

```sh
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
```

## cluster.sh 脚本文件

```shell
#!/usr/bin/env bash
set -e

# 脚本当前目录
cPath=$(cd $(dirname "$0") || exit; pwd)

# 启动集群数量
num="$1"
if [[ ! "$num" ]]; then
  num=6
fi

sPort=6378
for((i=1;i<=$num;i++)); do
    sh ${cPath}/run.sh  $(($sPort+$i))
done
```

## 启动服务

```shell
# 启动脚本后会返回对应创建结果
sh scripts/cluster.sh

# 容器：redis_6379 - IP：172.17.0.2 - 启动成功
# 容器：redis_6380 - IP：172.17.0.3 - 启动成功
# 容器：redis_6381 - IP：172.17.0.4 - 启动成功
# 容器：redis_6382 - IP：172.17.0.5 - 启动成功
# 容器：redis_6383 - IP：172.17.0.6 - 启动成功
# 容器：redis_6384 - IP：172.17.0.7 - 启动成功
```

## 创建集群

**获取对应容器节点ip**

```sh
docker inspect -f "{{.NetworkSettings.IPAddress}}:6379" `docker ps | grep redis_ | awk -F' ' '{print $1}'` | sort |xargs |  sed 's/ /, /g'

# 返回结果
# 172.17.0.2:6379, 172.17.0.3:6379, 172.17.0.4:6379, 172.17.0.5:6379, 172.17.0.6:6379, 172.17.0.7:6379
```

**进入任意节点**

```sh
# redis_6379为容器名称
docker exec -it redis_6379 /bin/bash

# 创建集群 以下ip均为所获得的容器ip
redis-cli --cluster create 172.17.0.2:6379, 172.17.0.3:6379, 172.17.0.4:6379, 172.17.0.5:6379, 172.17.0.6:6379, 172.17.0.7:6379 --cluster-replicas 1
```

过程中输入`yes`让redis自动创建集群即可

## 集群测试

> 以下操作均为节点中操作

```sh
# 进入redis客户端
redis-cli -c
# 查看集群信息
cluster info
# 查看集群节点
cluster nodes
```

​		