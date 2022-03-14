# docker+Jenkins+SpringBoot部署

## 下载jenkins镜像

> jenkins镜像已停用，需使用jenkins/jenkins镜像

`docker pull jenkins/jenkins`

## docker中运行jenkins

> 第一次时可能会因权限问题无法启动
>
> 需要增加卷的权限 `chown -R 1000:1000 主机路径`

```shell
docker run -d --name jenkins -p 30001:8080 -v /baiqiang/jenkins_home:/var/jenkins_home -e JENKINS_OPTS="--prefix=/jenkins" jenkins/jenkins
```

| 参数  |                   作用                   |
| :---: | :--------------------------------------: |
|  -d   |                 后台运行                 |
| -name |                 容器名称                 |
|  -p   |      端口映射 `主机端口:docker端口`      |
|  -v   | 卷挂载数据映射 `主机路径:docker内部路径` |
|  -e   |     环境变量 添加一级目录`/jenkins`      |

启动成功后 浏览器输入`http://ip:30001/jenkins`即可进入jenkins

初次进入需要输入密码，可通过下面方式查看密码

```shell
# 进入jenkins容器
docker exec -it jenkins bash
# 输出初次登陆密码
cat /var/jenkins_home/secrets/initialAdminPassword
```

