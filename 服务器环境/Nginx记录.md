# Nginx学习记录

## 什么是Nginx

> 一款轻量级的Web服务器、反向代理服务器，由于它的内存占用少，启动极快，高并发能力强，在互联网项目中广泛应用

## Nginx作用

1. 反向代理

> 作为其他服务器的代理服务器，接收请求并转到被代理的服务器上

+ **正向代理** 为客户端做代理，访问服务器
+ **反向代理** 为服务器做代理，让客户端请求

2. 负载均衡 

> 充当着网络流中“交通指挥官”的角色，“站在”服务器前处理所有服务器端和客户端之间的请求，从而最大程度地提高响应速率和容量利用率，同时确保任何服务器都没有超负荷工作

用于分配请求到具体服务器的数量(权重)

3. 动静分离

> 为了提高网站的响应速度，减轻程序服务器（Tomcat，Jboss等）的负载，对于静态资源，如图片、js、css等文件，可以在反向代理服务器中进行缓存，这样浏览器在请求一个静态资源时，代理服务器就可以直接处理，而不用将请求转发给后端服务器

## 安装Nginx

根据你的系统，选择对应的命令来安装：

```shell
$ sudo yum install epel-release && yum install nginx   [On CentOS/RHEL]
$ sudo dnf install nginx                               [On Debian/Ubuntu]
$ sudo apt install nginx                               [On Fedora]
```

## 常用命令

+ 查看版本 `nginx -v`
+ 检查配置正确性 `nginx -t`
+ 启动nginx `nginx`
+ 快速停止 `nginx -s stop`
+ 完整有序的停止 `nginx -s quit`(同停止相同，安全性高、一步一步停止)
+ 平滑的重启 `nginx -s reload`
+ 查看进程 `ps aux|grep nignx`

## 配置文件

> 默认配置文件`nginx.conf`在nginx/conf内

1. 负载均衡权重配置 (默认权重为1)

   如下所示权重，来3次请求 2次会转到19号服务器上,1次转发到1号服务器上

```shell
# 权重为 weight / 总和(weight),权重越高调用频率更高,不填写weight则默认为1
upstream weightConfig {
	server 192.168.9.1 weight=1;
	server 192.168.9.19 weight=2;
}
```

2. 使用负载均衡

```shell
 # 在server/locaiton/ 中添加负载均衡使用
 server {
 	...
 	location / {
 		...
 		proxy_pass http://weightConfig/;
 		...
 	}
 	...
 }
```

## 文件路径

> 开启文件目录 `autoindex on`

+ **root** root路径＋location路径
+ **alias** 用alias路径替换location路径, alias必须以'/'结尾

```shell
请求uri: /t/a.html

location ^~ /t/ {
     root /www/root/html/;
}
==> /www/root/html/t/a.html

location ^~ /t/ {
alias /www/root/html/new_t/;
}
==> /www/root/html/new_t/a.html
```

