> homecd ..阿里云服务器docker安装nacos并启动
>
> 由于nacos默认占的内存比较大，服务器内存小。所以配置一下启动所占内存的参数

+ 挂在目录

  ```shell
  mkdir -p /home/nacos/logs/                      #新建logs目录
  mkdir -p /home/nacos/init.d/          
  vim /home/nacos/init.d/custom.properties        #修改配置文件
  ```

+ 配置文件

  ```properties
  server.contextPath=/nacos
  server.servlet.contextPath=/nacos
  server.port=8848
  
  #spring.datasource.platform=mysql
  
  #db.num=1
  #db.url.0=jdbc:mysql://xx.xx.xx.x:3306/nacos_devtest_prod?#characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
  #db.user=user
  #db.password=password
  
  
  nacos.cmdb.dumpTaskInterval=3600
  nacos.cmdb.eventTaskInterval=10
  nacos.cmdb.labelTaskInterval=300
  nacos.cmdb.loadDataAtStart=false
  
  management.metrics.export.elastic.enabled=false
  
  management.metrics.export.influx.enabled=false
  
  
  server.tomcat.accesslog.enabled=true
  server.tomcat.accesslog.pattern=%h %l %u %t "%r" %s %b %D %{User-Agent}i
  
  
  nacos.security.ignore.urls=/,/**/*.css,/**/*.js,/**/*.html,/**/*.map,/**/*.svg,/**/*.png,/**/*.ico,/console-fe/public/**,/v1/auth/login,/v1/console/health/**,/v1/cs/**,/v1/ns/**,/v1/cmdb/**,/actuator/**,/v1/console/server/**
  nacos.naming.distro.taskDispatchThreadCount=1
  nacos.naming.distro.taskDispatchPeriod=200
  nacos.naming.distro.batchSyncKeyCount=1000
  nacos.naming.distro.initDataRatio=0.9
  nacos.naming.distro.syncRetryDelay=5000
  nacos.naming.data.warmup=true
  nacos.naming.expireInstance=true
  ```

+ 启动容器

  ```shell
  docker run --name nacos -d \
  -p 38848:8848 \
  --network mynet \
  --privileged=true \
  --restart=always \
  -e JVM_XMS=256m \
  -e JVM_XMX=256m \
  -e MODE=standalone \
  -e PREFER_HOST_MODE=hostname \
  -v /home/nacos/logs:/root/nacos/logs \
  -v /home/nacos/init.d/custom.properties:/root/nacos/init.d/custom.properties \
  nacos/nacos-server
  ```


+ nginx配置

  > 可以直接使用`ip/nacos`访问控制面板

  ```shell
  location /nacos {
       proxy_set_header Host $host;
       proxy_set_header X-Real-IP $remote_addr;
       proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
       proxy_pass http://localhost:8848;
  }
  ```

  

