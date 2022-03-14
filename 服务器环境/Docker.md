# Docker

> 一个开源的应用容器引擎，让开发者可以打包他们的应用以及依赖包到一个可移植的[镜像](https://baike.baidu.com/item/镜像/1574)中，然后发布到任何流行的 [Linux](https://baike.baidu.com/item/Linux)或[Windows](https://baike.baidu.com/item/Windows/165458) 机器上，也可以实现[虚拟化](https://baike.baidu.com/item/虚拟化/547949)。容器是完全使用[沙箱](https://baike.baidu.com/item/沙箱/393318)机制，相互之间不会有任何接口。

## 作用

> 将程序和环境作为一个整体发布到系统中
>
> docker中将程序和环境封装到一起做为镜像
>
> 任何系统使用镜像即可使用程序

+ 传统：开发人员提供(jar)  =>  环境 => 系统
+ docker: 开发人员提供(jar + 环境)   =>  系统

## 安装

> 基于阿里云服务器安装

```shell
# 查看系统内核
uname -r
# 查看系统信息
cat /etc/os-release 
# 如有安装docker需要先卸载
sudo yum remove docker \
                docker-client \
                docker-client-latest \
                docker-common \
                docker-latest \
                docker-latest-logrotate \
                docker-logrotate \
                docker-engine 
# 相关安装包
sudo yum install -y yum-utils
# 安装docker仓库
# yum-config-manager --add-repo http://download.docker.com/linux/centos/docker-ce.repo（国外仓库源）
sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
# 安装docker ce社区版 ee企业版
sudo yum install docker-ce docker-ce-cli containerd.io
# 检查是否安装成功
sudo docker version
# 启动docker
sudo systemctl start docker
# 运行helleo-world检查运行是否正常
sudo docker run hello-world

# 卸载相关
sudo yum remove docker-ce docker-ce-cli containerd.io
sudo rm -rf /var/lib/docker
sudo rm -rf /var/lib/containerd
```

**阿里云镜像服务器加速**

> 推荐安装1.10.0以上版本的Docker客户端，参考文档[docker-ce](https://yq.aliyun.com/articles/110806)

```shell
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://na46zfav.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

## 常用命令

> [官网命令文档](https://docs.docker.com/engine/reference/run/)

### 帮助

```shell
# 服务器版本
docker version
# docker 系统信息
docker info
# 帮助命令
docker help
```

### 镜像命令

```shell
# 查看所有镜像
docker images
# 搜索镜像
docker search ****
# 下载镜像,不写版本默认为最后一个版本
# 指定版本 docker pull ****:版本号
docker pull ****
# 删除镜像,可以为id或(名称:版本号)
docker rmi ****
```

### 容器命令

> 有了镜像才能创建容器

+ 新建容器

  ```shell
  # 新建容器并启动镜像
  --name="name" 容器名称
  -d 						后台方式运行
  -it 					使用交互方式运行
  -p						指定主机端口:容器端口 8080:8080
  docker run [可选] image
  # 示例 启动并进入容器  exit: 退出容器 ctrl+p+q: 后台运行退出
  # docker run -it centos /bin/bash
  ```

+ 运行容器

  ```shell
  # 列出当前正在运行的容器
  -a 			#包含历史运行的
  -n=? 		#最近创建的几个容器
  -q 			#只显示容器编号
  docker ps [可选]
  ```

+ 删除容器

  ```shell
  # 不能删除正在运行的 可加入-f强制删除
  docker rm 容器id
  ```

+ 启动和停止

  ```shell
  docker start 容器id
  docker restart 容器id
  docker stop 容器id	
  docker kill 容器id 		# 强制退出
  ```

+ 查看日志

  ```shell
  -f   				# 跟随输出 
  -t   				# 带时间戳
  --tail ? 	  # 最近的几条
  docer logs [参数] 容器id
  ```

+ 查看镜像元数据

  ```shell
  docker inspect 容器id
  ```

+ 进入正在运行的容器

  ```shell
  # 交互方式一般为: /bin/bash
  docker exec -it 容器id 交互方式
  
  # 直接进入某容器
  docker attach 容器id 
  ```

+ 其他

  ```shell
  # 从容器内拷贝文件到主机
  docker cp 容器id:路径 主机路径
  ```

## 数据卷

> 将docker中的数据同步到主机的技术，可让多个容器之间共享数据

```shell
# 将容器路径内容同步到主机路径下，使数据持久化
# 可以同时挂在多个 -v 主机路径:容器路径 -v 主机路径:容器路径

# 指定路径挂在
docker run -v 主机路径:容器路径

# 匿名挂载
docker run -v 容器路径

# 具名挂载 卷名不带路径`/`
docker run -v 卷名:容器路径

# 同步挂载其他容器的挂载项,可实现容器间数据共享
# --volumes-from 容器id
docker run --name abc xxxx
docker run --volumes-from abc xxxx
docker run --volumes-from abc xxxx
```

**具名匿名挂载**

> 这是的卷都在/var/lib/docker/volumes下，可使用`docker volume ls`查看

## DockerFile

> 基础镜像 `FROM stratch`所有的镜像基本都是从开始

<img src="http://blog-imgs.nos-eastchina1.126.net/1618474106.png"  />

编写带java8的镜像

```dockerfile
FROM java:8
MAINTAINER mrbai<568604944@qq.com>

ENV MYPATH /usr/local
WORKDIR $MYPATH

CMD echo $MYPATH
CMD echo "------end------"

CMD ["/bin/bash"]
```

## 自定义网络

> 为docker自定义网络,这样可以直接使用容器名来连通

```shell
# 自定义网络
# --driver bridge
# --subnet 192.168.0.0/16 代表可以设置255* 255 - 2个网络(.1和.255) 192.168.0.2~192.168.255.254
# --gateway 192.168.0.1 网关
docker network create --driver bridge --subnet 192.168.0.0/16 --gateway 192.168.0.1 mynet
```

使用自定义网络

```shell
docker run -net mynet
```

