# log4j2异步日志

> 日志框架中log4j2性能较好，故使用此框架

log4j2优越的性能其原因在于log4j2使用了LMAX,一个无锁的线程间通信库代替了,logback和log4j之前的队列. 并发性能大大提升。下面是网上搜索到的一些性能测试图

![](http://blog-imgs.nos-eastchina1.126.net/1617354831.png)

![](http://blog-imgs.nos-eastchina1.126.net/1617354865.png)



## 项目集成

因`springboot`使用logback框架，为避免依赖冲突，首先需要排除掉logback日志框架

+ 文件`pox.xml`

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <!-- 去掉自带输出框架logback-->
      <exclusions>
          <exclusion>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-logging</artifactId>
          </exclusion>
      </exclusions>
  </dependency>
  
  <!-- 日志 Log4j2 -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-log4j2</artifactId>
  </dependency>
  
  <!-- Log4j2 异步支持 -->
  <dependency>
      <groupId>com.lmax</groupId>
      <artifactId>disruptor</artifactId>
      <version>3.3.6</version>
  </dependency>
  
  <!-- 使用Lombok可简化log操作和pojo代码量 -->
  <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
  </dependency>
  
  ```

## 编写导入log4j2配置

> log4j2已放弃使用.properties作为配置文件，所以这里使用xml文件来编写配置

**输出格式**

```xml
%d{yyyy-MM-dd HH:mm:ss, SSS} : 日志生产时间,输出到毫秒的时间
%-5level : 输出日志级别，-5表示左对齐并且固定输出5个字符，如果不足在右边补0
%c : logger的名称(%logger)
%t : 输出当前线程名称
%p : 日志输出格式
%m : 日志内容，即 logger.info("message")
%n : 换行符
%C : Java类名(%F)
%L : 行号
%M : 方法名
%l : 输出语句所在的行数, 包括类名、方法名、文件名、行数
hostName : 本地机器名
hostAddress : 本地ip地址
```

编写的配置文件在application中引用即可

+ `application.yml`

  ```yaml
  logging:
  	# 本人其他配置都放在config文件夹中
    config: classpath:config/log4j2-spring.xml
  ```

+ `log4j2-spring.xml`

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!-- Configuration后面的status，这个用于设置log4j2自身内部的信息输出，可以不设置，当设置成trace时，
       你会看到log4j2内部各种详细输出。可以设置成OFF(关闭) 或 Error(只输出错误信息)。
       30s 刷新此配置
  -->
  <configuration status="WARN" monitorInterval="30">
  
      <!-- 日志文件目录、压缩文件目录、日志格式配置 -->
      <properties>
          <Property name="fileName">自行填写日志路径</Property>
          <Property name="fileGz">自行填写日志压缩包路径</Property>
          <Property name="PID">????</Property>
          <Property name="LOG_PATTERN">%d{yyyy-MM-dd HH:mm:ss.SSS} %clr{%5p} %clr{${sys:PID}}{magenta} --- [%15.15C] %clr{%-40.40c{1.}}{cyan} : %m%n</Property>
          <Property name="FILE_PATTERN">%d{yyyy-MM-dd HH:mm:ss.SSS} [%C %M]: %m%n</Property>
      </properties>
  
      <Appenders>
          <!-- 输出控制台日志的配置 -->
          <Console name="console" target="SYSTEM_OUT">
              <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
              <ThresholdFilter level="debug" onMatch="ACCEPT" onMismatch="DENY"/>
              <!-- 输出日志的格式 -->
              <PatternLayout pattern="${LOG_PATTERN}" charset="UTF-8"/>
          </Console>
  
          <!-- 打印出所有的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档 -->
          <RollingRandomAccessFile name="infoFile" fileName="${fileName}/web-info.log" immediateFlush="false"
                                      filePattern="${fileGz}/$${date:yyyy-MM}/%d{yyyy-MM-dd}-%i.web-info.gz">
              <PatternLayout pattern="${FILE_PATTERN}"/>
  
              <Policies>
                  <SizeBasedTriggeringPolicy size="20 MB"/>
              </Policies>
  
              <Filters>
                  <!-- 只记录info和warn级别信息 -->
                  <ThresholdFilter level="warn" onMatch="DENY" onMismatch="NEUTRAL"/>
                  <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
              </Filters>
  
              <!-- 指定每天的最大压缩包个数，默认7个，超过了会覆盖之前的 -->
              <DefaultRolloverStrategy max="50"/>
          </RollingRandomAccessFile>
  
          <!-- 存储所有error信息 -->
          <RollingRandomAccessFile name="errorFile" fileName="${fileName}/web-error.log" immediateFlush="false"
                                      filePattern="${fileGz}/$${date:yyyy-MM}/%d{yyyy-MM-dd}-%i.web-error.gz">
              <PatternLayout pattern="${FILE_PATTERN}"/>
  
              <Policies>
                  <SizeBasedTriggeringPolicy size="50 MB"/>
              </Policies>
  
              <Filters>
                  <!-- 只记录error级别信息 -->
                  <ThresholdFilter level="error" onMatch="ACCEPT" onMismatch="DENY"/>
              </Filters>
  
              <!-- 指定每天的最大压缩包个数，默认7个，超过了会覆盖之前的 -->
              <DefaultRolloverStrategy max="50"/>
          </RollingRandomAccessFile>
      </Appenders>
  
      <!-- Mixed sync/async 同时只能使用一个 -->
      <Loggers>
  <!--        <Root level="debug" includeLocation="true">-->
  <!--            <AppenderRef ref="console"/>-->
  <!--            <AppenderRef ref="infoFile"/>-->
  <!--            <AppenderRef ref="errorFile"/>-->
  <!--        </Root>-->
  
          <AsyncRoot level="debug" includeLocation="true">
              <AppenderRef ref="console"/>
              <AppenderRef ref="infoFile"/>
              <AppenderRef ref="errorFile"/>
          </AsyncRoot>
      </Loggers>
  
  </configuration>
  ```

## log4j2使用

> 因项目使用了lombok框架使用@Slf4j注解即相当于为该类生成一个静态的log属性

下面为示例

```java
package com.mrbai.services.impls;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mrbai.mapper.ProductMapper;
import com.mrbai.pojos.Product;
import com.mrbai.services.ProductService;
import com.mrbai.untils.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: MrBai
 * @date: 2021-03-31 15:34
 **/
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Override
    public ApiResponse createProduct(Product product) {
        log.info("开始添加商品");
        ApiResponse res = new ApiResponse();

        if (productMapper.insert(product) > 0) {
            res.setMsg("添加商品成功");
        } else {
            res.setCode(211);
            res.setMsg("添加商品失败");
        }
        log.info(res.getMsg());
        return res;
    }

    @Override
    public ApiResponse updateProduct(Product product) {
        log.info("开始更新商品");
        ApiResponse res = new ApiResponse();
        if (productMapper.update(product) > 0) {
            res.setMsg("更新商品成功");
        } else {
            res.setCode(211);
            res.setMsg("更新商品失败");
        }
        log.info(res.getMsg());
        return res;
    }

    @Override
    public ApiResponse shelfProduct(String productNo, int on) {
        log.info("开始商品上下架操作");
        ApiResponse res = new ApiResponse();
        if (productMapper.shelf(productNo, on) > 0) {
            res.setMsg(on == 1 ? "上架商品成功" : "下架商品成功");
        } else {
            res.setCode(211);
            res.setMsg("操作商品失败");
        }
        log.info(res.getMsg());
        return res;
    }

    @Override
    public ApiResponse selectProductList(int pageNum, int pageSize) {
        log.info("查询商品列表");
        PageHelper.startPage(pageNum,pageSize);
        ApiResponse res = new ApiResponse();
        List<Product> products = productMapper.selectList();
        PageInfo pageInfo = new PageInfo(products);
        res.setData(pageInfo);
        return res;
    }
}

```

