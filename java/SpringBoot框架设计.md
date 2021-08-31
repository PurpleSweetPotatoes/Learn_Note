# 框架结构

主体设计包括不限于以下内容

```shell
Project
|__ subProject // 子工程模块
|    |__ project1 (微服务1,例如:用户中心)
|		 |__ priject2 (微服务2,例如:漫画爬虫)
|__ codeGenerator // 代码生成器
|__ frameWork // 第三方依赖
			|__ mybatisPlus_config (mybatisPlus_config配置)
			|__ redis_config (redis配置)
			|__ feign_api (fen接口配置)
			|__ pojo (模型-(响应、请求、mybatis相关等等))
```

已使用框架

```

```



## 自动上传构建

> 利用shell脚本中ssh功能操作

```shell
# 传输jar文件
scp -r 本地文件 {name}@{ip}:服务器路径
# ssh 远程执行命令 source /etc/profile用于加载远程环境配置
ssh {name}@{ip} """
cd 服务器路径
rm -f nohup.out
source /etc/profile
lsof -t -i :8080 -s TCP:LISTEN | xargs kill
nohup java -jar comics_demo-1.0-SNAPSHOT.jar >nohup.out 2>&1 &
"""
```

## 过滤器

> 存放于pojo工程中

用于布隆过滤和黑名单过滤,因使用注解配置

```java
@WebFilter(urlPatterns = "/*", filterName = "testFilter")
```

需在主入口配置注解

```java
@ServletComponentScan("com.mrbai.filters")
```

## 拦截器

> 存放于pojo工程中

用于接口统计，基于`WebMvcConfigurer`接口中重新`addInterceptors`方法实现

```java
/*
* 增加方法拦截器
* */
@Override
public void addInterceptors(InterceptorRegistry registry) {
registry.addInterceptor(new BQTimeInterCeptor()).addPathPatterns("/**");
}
```

## 响应体封装

> 存放于pojo工程中

基于ResponseBodyAdvice接口实现

```java
/*
* 指定扫描路径，对路径下的响应进行包装
* */
@ControllerAdvice(basePackages = "com.mrbai.controller")
public class BQResponseHandler implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        List<Boolean> booleans = Arrays.asList(
                o instanceof BQResponse,
                methodParameter.getMethodAnnotation(NoPackResponse.class) != null
        );
        if (booleans.contains(true))
            return o;
        return BQResponse.success(o);
    }
}
```

为解决响应体封装中封装string字符串异常问题 需要调整序列化方式

```java
/*
* 解决 ResponseBodyAdvice 返回string字符串
* StringHttpMessageConverter 转化异常问题 cannot be cast to java.lang.String
* 调整MappingJackson2HttpMessageConverter优先级或移除StringHttpMessageConverter
* */
@Override
public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
  ArrayList<HttpMessageConverter<?>> objects = new ArrayList<>();
  for (HttpMessageConverter<?> converter : converters) {
    if (converter.getClass().isAssignableFrom(StringHttpMessageConverter.class)) {
      objects.add(converter);
    }
  }
  converters.removeAll(objects);
}
```

## 分布式session共享

> 基于spring-session-data-redis和redis实现

spring-session重写了request.getsession方法，使用sessionId为key存放于redis中
保证不同客户端请求时通过统一标示符(自定义header中的“token”)来确定用户唯一身份信息

1. 自定义获取session的key

```java
/*
* 自定义session获取的key值
* */
@Bean
public HeaderHttpSessionIdResolver httpSessionStrategy() {
return new HeaderHttpSessionIdResolver("token");
}
```

2. 入口类配置session共享启用

```java
// redisNamespace => 域名空间
// maxInactiveIntervalInSeconds => 过期时间
@EnableRedisHttpSession(redisNamespace = "comic", maxInactiveIntervalInSeconds = 3600 * 24 * 7)
```

