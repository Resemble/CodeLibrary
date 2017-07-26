# CodeLibrary
代码与业务解耦部分，可重用代码

### 邮件服务器处理流程
- 在 docker 中启动容器，`--privileged` 和 `/sbin/init` 是为了提权限，因为直接部属镜像，centOs 没有 `systemctl` 权限
```
sudo docker run --privileged -d -p 25:25 -p 8143:143 -p 8110:110 --name=emailServer2 resemble/email:3 /sbin/init 
```
- 使用 telnet 检查服务是否开启，如果开启则正确
```
telnet ip 25
telnet ip 8143
telnet ip 8110
```
- 如果没有开启，则进入容器
```
docker exec -it bash emailServer2
systemctl  restart  dovecot
systemctl  restart  postfix
systemctl  restart  saslauthd
```
### 邮箱帐号说明
一个系统帐号密码对应一个邮箱帐号密码
