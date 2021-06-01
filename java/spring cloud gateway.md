# Gateway+Nacos动态路由

> Spring Cloud Gateway是Spring Cloud官方推出的第二代网关框架，取代Zuul网关。网关作为流量的，在微服务系统中有着非常作用，网关常见的功能有路由转发、权限校验、限流控制等作用

**特性**

- 基于Spring 5，Reactor(模式) 和 SpringBoot 2.0
- 能够在任何请求属性上匹配路由
- 断言和过滤器是特定于路由的
- Hystrix断路器集成
- SpringCloud DiscoveryClient集成
- 易于编写断言和过滤器
- 请求速率限制
- 路径重写

```xml
spring:
  cloud:
    gateway:
      routes:
        - id: clientA # 标识名唯一
          uri: http://localhost:8080 # 转发到哪里
          predicates:
            # 匹配规则: 请求路径为 /clientA/.....才会转发
            - Path=/clientA/**
          filters:
            # StripPrefix的意思是将路径切掉一级,转化后为 http://localhost:8081/**
            - StripPrefix=1

        - id: clientB
          uri: http://localhost:8081
          predicates:
            - Path=/clientB/**
            # 两个相同path配置权重，这一步可由注册中心来操作
						- Weight=service1, 95
          filters:
            - StripPrefix=1
```

