
[TOC]

# Redis入门

> Remote Dictionary Server，键值对方式存储

+ 能做什么

  - 内存储存，持久化

  - 效率高可以高速缓存
  - 地图信息分析
  - 计时器、计数器...

+ 特性

  + 多样的数据类型
  + 持久化
  + 集群
  + 事务

## 安装

[官网]([Redis](https://redis.io/))

> 服务器安装需要安装gcc-c++

推荐使用linux环境下使用

1、进去服务器，键入以下命令,[安装参考]([Redis](https://redis.io/download))

```shell
wget https://download.redis.io/releases/redis-6.2.1.tar.gz
tar xzf redis-6.2.1.tar.gz
cd redis-6.2.1
//yum install gcc-c++
make
make install
```

2、指定文件启动redis

安装完成后查看路径`/usr/local/bin/`中包含redis即安装成功

编辑`redis.conf`将daemonize改为yes

```shell
redis-server redis.conf 
```

3、’查看redis进程信息

```shell
ps -ef|grep redis
```

4、打开客户端

执行`redis-cli` 输入`ping`可查看redis是否启动成功

5、关闭服务端

关闭服务器:`shutdown` 

退出:`exit`

## 测试性能

>  redis-benchmark

| 选项  |                    描述                    |  默认值   |
| :---: | :----------------------------------------: | :-------: |
|  -h   |              指定服务器主机名              | 127.0.0.1 |
|  -p   |               指定服务器端口               |   6379    |
|  -s   |             指定服务器 socket              |           |
|  -c   |               指定并发连接数               |    50     |
|  -n   |                 指定请求数                 |   10000   |
|  -d   |   以字节的形式指定 SET/GET 值的数据大小    |     2     |
|  -k   |          1=keep alive 0=reconnect          |           |
|  -r   | SET/GET/INCR 使用随机 key, SADD 使用随机值 |           |
|  -P   |         通过管道传输 <numreq> 请求         |     1     |
|  -q   |    强制退出 redis。仅显示 query/sec 值     |           |
| --csv |              以 CSV 格式输出               |           |
|  -l   |           生成循环，永久执行测试           |           |
|  -t   |       仅运行以逗号分隔的测试命令列表       |           |
|  -I   |   Idle 模式。仅打开 N 个 idle 连接并等待   |           |

## 基础知识

默认数据库16个,进入使用第0个数据

> [redis 命令手册](https://www.redis.com.cn/commands.html)

|          命令           |        代码         |
| :---------------------: | :-----------------: |
|     切换数据库(num)     |    select `num`     |
| 查看当前数据库的所有key |       keys *        |
|   清空当前数据库键值    |       flushdb       |
|     清空全部数据库      |      FLUSHALL       |
|         设置值          |  set `key` `value`  |
|         获取值          |      get `key`      |
|       是否存在key       |    EXISITS `key`    |
|     设置key过期时间     | FXPIRF `key` `time` |
|   查看key还有多久过期   |      ttl `key`      |
|      查看key的类型      |     type `key`      |

> 单线程

redis是基于内存操作，cpu不是性能瓶颈所以使用单线程

**单线程为什么这么快**

1、高性能服务器不一定是多线程

2、多线程(CPU上下文切换)不一定比单线程效率高

3、数据读取速度CPU>内存>硬盘, redis直接操作内存所以效率高

## 数据类型

> Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作**数据库**、**缓存**和消息中间件. 它支持多种类型的数据结构，如 **字符串**（strings）， **散列**（hashes）， **列表**（lists）， **集合**（sets）， **有序集合**（sorted sets） 与**范围查询**， bitmaps， hyperloglogs 和 地理空间（geospatial） 索引半径查询. Redis 内置了 复制（replication）， LUA脚本（Lua scripting）， LRU驱动事件（LRU eviction）， **事务**（transactions） 和不同级别的 **磁盘持久化**（persistence）， 并通过 **Redis哨兵**（Sentinel） 和自动 分区（Cluster）提供高可用性（high availability）

### String

+ append 追加
+ incr 自增 
+ incrby: 自增长度
+ decr 自减
+ decrby 自减长度
+ getrange 截取指定范围的字符串(start end)
+ setrange 替换指定位置开始的字符串(start)
+ setex 设置过期时间
+ setnx 不存在的时候设置

### List

> 相当于一个链表，可以从两头操作

+ [L|R]PUSH 头部或尾部插入值
+ [LP]POP 头部或尾部弹出
+ lindex 取出第几个元素
+ llen 列表长度
+ lrem 移除指定个数的value
+ ltrim 保留指定位置的元素，其余移除
+ lset 指定位置改变值，该位置必须有值

### Set

> 命令以s开头，元素不重复

+ sadd 添加元素
+ srem 删除元素
+ scard 获取长度
+ sinter 交集
+ sunion 并集
+ sdiff 差集

### Hash

> 命令以h开头，值相当于Map集合(k-map集合)

+ hset 存值
+ hget 取值
+ hmset 多值设置
+ hmget 多指获取
+ hgetall 获取所有的key和值
+ hdel 删除指定的key和对应value
+ hlen 获取key数量
+ hexists 是否存在指定key

### Zset

> 以z开头的map，key为数字类型，自动排序

+ zadd 添加值
+ zrem 

### 特殊数据类型

#### [GEO](https://www.redis.com.cn/redis-geo.html) 地理位置信息 

> 基于Zset的封装，可以使用ZSet命令

+ geoadd：添加地理位置的坐标。
+ geopos：获取地理位置的坐标。
+ geodist：计算两个位置之间的距离。
+ georadius：根据用户给定的经纬度坐标来获取指定范围内的地理位置集合。
+ georadiusbymember：根据储存在位置集合里面的某个地点获取指定范围内的地理位置集合。
+ geohash：返回一个或多个位置对象的 geohash 值。

#### [HyperLogLog](https://www.redis.com.cn/redis-hyperloglog.html)

> 一般统计使用

HyperLogLog 是用来做基数统计的算法，HyperLogLog 的优点是，在输入元素的数量或者体积非常非常大时，计算基数所需的空间总是固定 的、并且是很小的。

> 比如数据集 {1, 3, 5, 7, 5, 7, 8}， 那么这个数据集的基数集为 {1, 3, 5 ,7, 8}, 基数(不重复元素)为5。 基数估计就是在误差可接受的范围内，快速计算基数。

+ [PFADD](https://www.redis.com.cn/commands/pfadd.html)添加指定元素到 HyperLogLog 中。
+ [PFCOUNT](https://www.redis.com.cn/commands/pfcount.html)返回给定 HyperLogLog 的基数估算值。
+ [PFMERGE](https://www.redis.com.cn/commands/pfmerge.html)将多个 HyperLogLog 合并为一个 HyperLogLog

#### Bitmap位图

> 利用二进制位来代表一个每个状态 ,命令融合在String当中

示例

```bash
# 使用bitmap判断一周打卡情况
setbit sign 1 0
setbit sign 2 1
setbit sign 3 1
setbit sign 4 0
setbit sign 5 0
setbit sign 6 0
setbit sign 7 0

getbit sign 2 # 得出周二是否打卡
bitcount sign # 查询打卡次数

```

+ setbit 设置某位状态
+ getbit 获取某位状态
+ bitcount 状态为1的次数

## 事务

> Redis 事务可以理解为一个打包的批量执行脚本，但批量指令并非原子化的操作，中间某条指令的失败不会导致前面已做指令的回滚，也不会造成后续的指令不做

所有命令存入队列中，只有进行执行操作后再一次性操作

1. 开启事务multi
2. 操作命令
3. exec执行之前所有的命令

放弃事务`dicard`，之前队列中的命令都会取消

> 监控

**悲观锁**: 无论做什么都会加锁，担心任何时候都会出问题
**乐观锁**: 更新数据的时候才去加锁，其他时候数据不会出问题

事务提交件使用`watch`相当于开启监听，如果在此过程中修改的key发生了改变，则此事务会提交失败
事务提交后需要使用`unwatch`取消监听

## SpringDataRedis

> springboot2.x以后使用lettuce替代jedis进行redis链接

+ Jedis是直连模式，在多个线程间共享一个Jedis实例时是线程不安全的，可以通过创建多个Jedis实例来解决，但当连接数量增多时，物理连接成本就较高同时会影响性能，因此较好的解决方法是使用JedisPool。
+ Lettuce的连接是基于Netty的，连接实例可以在多个线程间共享，Netty可以使多线程的应用使用同一个连接实例，而不用担心并发线程的数量。通过异步的方式可以让我们更好地利用系统资源。

由于lettuce不像jedis使用类似redis命令操作，所以一般会封装一个工具类进行操作

## Redis.conf

```

# 快照
save 900 1 # 900s以内至少1个key进行修改，会进行一次持久化
save 300 10 # 300s内至少10个key进行.....
save 60 10000 # 60s内.....
 
rdbcompression yes # 是否压缩rdb文件，需要消耗cpu
dir ./ # rdb保存目录

```
 
## Reids持久化

### RDB (Redis DataBase)

> 需要一定时间间隔操作，适合大规模数据恢复对数据完整性要求不高fork进程时会占用一定内存空间

**触发方式**

+ SAVE：阻塞redis的服务器进程，直到RDB文件被创建完毕。
+ BGSAVE：派生(fork)一个子进程来创建新的RDB文件，记录接收到BGSAVE当时的数据库状态，父进程继续处理接收到的命令，子进程完成文件的创建之后，会发送信号给父进程，而与此同时，父进程处理命令的同时，通过轮询来接收子进程的信号
每次所有键值对全部备份(类似快照)，
+ `flushall` 或 `shutdown` 在配置文件设置了save的情况下会生成rdb

**恢复**

需要rdb文件放在redis-server同级目录下，redis启动时会自动读取

### AOF (Append-Only-File)

>以日志的方式记录写操作记录

恢复时aof需在redis-server同级目录下，redis启动时会自动读取

恢复时逐条重新写入，数据量大时效率低

## Redis发布订阅

> 场景 网络聊天室
>
+ PSUBSCRIBE    订阅一个或多个符合给定模式的频道。
+ PUBSUB  查看订阅与发布系统状态。
+ PUBLISH 将信息发送到指定的频道。
+ PUNSUBSCRIBE    退订所有给定模式的频道。
+ SUBSCRIBE   订阅给定的一个或多个频道的信息。
+ UNSUBSCRIBE 指退订给定的频道。

## Redis主从复制

> 鉴于高可用至少需要1主2从，默认情况下redis都是主节点

主从复制，读写分离。主机负责写，从机负责读

`info replication`可查看当前数据库信息

### 配置

只需要配置从服务器

配置redis信息
 
 + 端口
 + 输出文件
 + 进程文件
 + rdb文件 

#### 命令配置

> 重启后不会有主机信息，因为这时默认自己为主机，需要重新设置为从机

启动好redis后使用`slaveof`来设置那个服务器接口当主机

```
slaveof host port 
# slaveof no one 重新变为主机
```

#### 配置文件配置

> 正常使用此方式来进行配置

replicaof <masterip> <masterport>

### 复制原理

+ **全量复制**: 第一次设置为从机时候会从主机接收全部的数据
+ **增量复制**: 主机每写入一次数据，都会让从机也写入对应数据


### 哨兵模式(redis-sentinel)

> master节点挂掉后，哨兵进程会主动选举新的master，可用性高

新开一个进程作为哨兵，**哨兵每秒给所有服务器和哨兵发送命令，查看是否响应判断是否需要重新设置master**

#### 配置哨兵文件`sentinel.conf`

```

# 禁止保护模式
protected-mode no
# 配置监听的主服务器，这里sentinel monitor代表监控，mymaster代表服务器的名称，可以自定义，
#192.168.1.10代表监控的主服务器，6379代表端口，2代表只有两个或两个以上的哨兵认为主服务器不可用的时候，才会进行failover(故障转移)操作。
sentinel monitor mymaster 192.168.1.10 6379 2
# sentinel author-pass定义服务的密码，mymaster是服务名称，123456是Redis服务器密码
sentinel auth-pass mymaster 123456
```

#### 启动哨兵

`redis-sentinel sentinel.conf`

## Redis缓存穿透和雪崩

> 高可用问题

### 缓存穿透

查询一个数据，缓存没有命中，于是向持久层进行请求。秒杀请求下同时多请求会导致持久层容易崩溃

**解决**

+ 布隆过滤器： 过滤不正常请求
+ 设置空缓存: 设置一个命中的空值

### 缓存击穿

一个key非常热频，扛着高并发，在此key失效的瞬间，请求会同时向持久层发起请求

**解决**

+ 不过期
+ 加锁: 保证只有一个线程访问持久层

### 缓存雪崩

缓存集中过期或者缓存服务器宕机或断网

**解决**

+ 多服务器
+ 降频限流(加锁)
+ 暂停边缘业务，提升服务器可用性能
+ 数据预热(提前加入缓存)



