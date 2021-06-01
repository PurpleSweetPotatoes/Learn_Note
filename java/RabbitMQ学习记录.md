# RabbitMQ学习记录

> 实现了高级消息队列协议（AMQP）的开源消息代理软件（亦称面向消息的中间件）。

RabbitMQ使用Erlang编写，因此需要安装erlang。因版本之间有对应要求需安装要求安装对应版本才可使用 [要求](https://www.rabbitmq.com/which-erlang.html)

## 安装

+ 安装`erlang`

  直接使用`yum install - y erlang`即可

+ 安装`rabbitmq`

  因yum中rabbitmq版本过低，无法使用，需直接下载网上对应版本的安装包才行

  ```shell
  # 进入安装包路径 出现complete即安装成功
  yum install -y 安装包名
  ```

## 配置命令

> 安装完成后需要进行相关配置才可使用
>
> 可视化界面端口号为15672，阿里云服务器注意放行端口  

```shell
# 开启可视化界面
rabbitmq-plugins enable rabbitmq_management

# 开启服务
systemctl start rabbitmq-server

# 关闭服务
systemctl stop rabbitmq-server

# 重启服务
systemctl restart rabbitmq-server

# 查看状态
systemctl status rabbitmq-server

# 配置最大内存使用率 fraction为0~1小数(一般设置0.4-0.7之间) absolute为实际占用大小
rabbitmqctl set_vm_memory_high_watermark [<fraction>] | [absolute 2GB]

# 配置剩余磁盘容量
rabbitmqctl set_disk_free_limit 30GB
```

## 用户权限

> 每个用户权限不同，可更具需要分配

**权限等级**

|     名称      |                权限                |
| :-----------: | :--------------------------------: |
| administrator | 查看所有信息，可对rabbitmq进行管理 |
|  monitoring   |            查看所有信息            |
|  policymaker  |        策略制定者、指定策略        |
|  management   |             普通管理员             |
|     none      |         可以理解为逻辑删除         |

**主要相关命令**

```shell
# 添加用户 后面为用户名 密码
rabbitmqctl add_user admin 100100

# 删除用户
rabbitmqctl delete_user admin

# 添加用户等级，具体等级见下表
rabbitmqctl set_user_tags admin administrator

# 添加所有权限
rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"

# 修改用户的密码
rabbitmqctl change_password admin newpwd
```

## 相关概念

> 通过Connection链接到RabbitMQ并创建Channel
> Connection通过Exchange发送消息到相关Queue

![](http://blog-imgs.nos-eastchina1.126.net/1618209066.png)

交换机模式

+ **生产者** 提供消息插入队列
+ **消费者** 消费队列中消息
+ **Connecttion** 链接
+ **Channel** 通道
+ **Exchange** 交换机
+ **Queue** 队列

## 项目配置

> springboot可以很方便的配置使用rabbitmq
>
> 使用场景适用于一些辅助消息的处理，不建议核心业务和对延时要求低的任务使用

**POM配置**

```xml
<!--  RabbitMQ 消息中间件-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
    <version>2.4.4</version>
</dependency>
```

**交换机和队列配置**

```java
@Configuration
public class RabbitMQConfiguration {

    public static final String FANOUT_ORDER_EXCHANGE = "fanout_order_exchange";

    public static final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";
    public static final String DEAD_WECHAT_QUEUE = "dead_wechat_queue";

    // 声明交换机
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_ORDER_EXCHANGE,true,false);
    }

    // 死信交换机
    @Bean
    public DirectExchange deadDirectExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    // 声明队列
    @Bean
    public Queue smsQueue() {
        return new Queue("sms.fanout.queue",true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue("email.fanout.queue",true);
    }

    @Bean
    public Queue wechatQueue() {
        // 配置该队列的死信队列
        // 消息被否定确认，使用 channel.basicNack 或 channel.basicReject ，并且此时requeue 属性被设置为false。
        // 消息在队列的存活时间超过设置的TTL时间。
        // 消息队列的消息数量已经超过最大队列长度。
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        map.put("x-dead-letter-routing-key", DEAD_LETTER_EXCHANGE);
        return new Queue("wechat.fanout.queue",true);
    }

    // 死信队列
    @Bean
    public Queue deadQueue() {
        return new Queue(DEAD_WECHAT_QUEUE,true);
    }

    // 绑定交换机和队列关系
    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue()).to(fanoutExchange());
    }
    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(fanoutExchange());
    }
    @Bean
    public Binding wechatBinding() {
        return BindingBuilder.bind(wechatQueue()).to(fanoutExchange());
    }

    //绑定死信交换机和死信队列
    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(deadQueue()).to(deadDirectExchange()).with(DEAD_WECHAT_QUEUE);
    }
}
```

### 生产者

> 在相关业务逻辑代码中使用

以下为使用示例

```java
// 交换机
String exchangeName = RabbitMQConfiguration.FANOUT_ORDER_EXCHANGE;
// 队列或路由key
String routeKey = "";
// String.valueOf(orderId) 具体要传送的信息
rabbitTemplate.convertAndSend(exchangeName, routeKey, String.valueOf(orderId));
```

某些场景为确保消息可靠送达需使用消息确认回调来保证逻辑闭环

+ **setConfirmCallback** 消息发送到交换机的回调

  ```java
  rabbitTemplate.setConfirmCallback((data, ack, cause) -> {
    String msgId = data.getId();
    if (ack) {
      log.info(msgId + ": 消息发送成功");
    } else {
      log.info(msgId + ": 消息发送失败");
    }
  });
  ```

+ **returnCallback** 消息发送到队列失败的回调

  ```java
  rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
  	log.info(MessageFormat.format("消息发送失败，ReturnCallback:{0},{1},{2},{3},{4},{5}", message, replyCode,replyText, exchange, routingKey));
    // TODO 做消息发送失败时的处理逻辑
  });
  ```

### 消费者

> 为了保证可靠消费，一般使用手动应答
>
> 配置 `spring.rabbitmq.listener.simple.acknowledge-mode=manual`

直接在方法上使用@RabbitListener注解即可

```java
@Component
public class ConsumerController {

  @RabbitListener(queues = {队列名称或routeKey})
  public void handler(Message message, Channel channel) throws IOException {
    System.out.println("收到消息：" + message.toString());
    Long tag = message.getMessageProperties().getDeliveryTag();
    try {
    	// 手动确认消息已消费
      channel.basicAck(tag,false);
    } catch (IOException e) {
      // 把消费失败的消息放入到死信队列
      channel.basicNack(tag, false, false);
    }
  }
}
```



