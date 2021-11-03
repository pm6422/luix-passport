# Passport
## 项目地址(URL)
https://github.com/pm6422/passport

## 概述(Overview)
本项目是以SpringBoot2为基础的标准OAuth2服务端程序，使用本项目可以快速创建企业内部的OAuth2服务器。其他Web或手机客户端可以使用本服务端做统一安全认证。
该服务端提供authorization_code，password，client_credentials，implicit等授权模式，适用于各种不同的使用场景。

系统前端架构UI部分为Bootstrap定制主题，JavaScript部分采用了基于前后端分离思想的AngularJS的前端MVC架构。
系统后端架构主要是以Spring相关组件为基础搭建的。

本系统支持分布式多节点部署，分布式环境下session共享采用了MongoDB存储。
另外本项目提供用户管理，用户权限管理，单点登录客户端信息管理，系统菜单管理，以及应用健康状态监控，接口调用时间监控，工程配置信息查看等高级功能。

另外本项目采用国外最新的后端技术栈，也可以用作后端学习资料。

This project is a standard OAuth2 server program based on SpringBoot 2. Using this project, you can quickly create an internal OAuth2 server in the enterprise. Other web or mobile clients can use this server for unified security authentication.
The server provides authorization_code, password, client_credentials, implicit and other authorization modes, which are suitable for various usage scenarios.

The UI part of the system front-end architecture is a customized theme for Bootstrap, and the JavaScript part adopts the front-end MVC architecture of AngularJS based on the idea of front-end and back-end separation.
The back-end architecture of the system is mainly based on Spring related components.

The system supports distribution deployment, and MongoDB storage is used for session sharing in a distributed environment.
In addition, this project provides advanced functions such as user management, user roles management, single sign-on client information management, system menu management, application health status monitoring, HTTP call time monitoring, and project configuration information viewing.

In addition, this project uses the latest foreign back-end technology stack, which can also be used as back-end learning materials.

## 应用配置(Configuration)
如果不想搭建mongo数据库，可以使用名为test的profile。如需使用mongo数据库，可以使用名为prod的profile。

If you don't want to connect to a mongo database, you can use a profile named test. If you need to use the mongo database, you can use a profile called prod.

## 应用运行(Run)
本项目包含服务器端(passport-server)和客户端(passport-client)两个子模块。
应用在第一次启动过程中会自动下载嵌入式数据库并自动完成安装。
运行命令很简单就是mvn，自动进行maven clean install操作，并完成应用启动。

This project contains two sub-modules, the server-side (passport-server) and the client-side (passport-client).
The application will automatically download the embedded database and automatically complete the installation during the first startup.
The simple command to run is mvn, which automatically performs the maven clean install operation and completes the application startup.

### 运行服务端(Run Passport Server)
```
cd passport-server
mvn
```

### 运行客户端(Run Passport Client)
```
cd passport-client
mvn
```

## 环境要求(Requirements)

- Java 8+
- Maven 3.0+

## 主要依赖(Major dependencies)
- Spring Boot 2.2.5
- Swagger and Springfox
- Spring boot starter actuator
- Spring boot starter data mongodb
- Spring boot starter security
- Spring security oauth2

## 系统截图(Screenshots)
![image](https://raw.githubusercontent.com/pm6422/passport/master/passport-server/images/00.png)
![image](https://raw.githubusercontent.com/pm6422/passport/master/passport-server/images/01.png)
![image](https://raw.githubusercontent.com/pm6422/passport/master/passport-server/images/02.png)
![image](https://raw.githubusercontent.com/pm6422/passport/master/passport-server/images/03.png)
![image](https://raw.githubusercontent.com/pm6422/passport/master/passport-server/images/04.png)
![image](https://raw.githubusercontent.com/pm6422/passport/master/passport-server/images/05.png)
![image](https://raw.githubusercontent.com/pm6422/passport/master/passport-server/images/06.png)
![image](https://raw.githubusercontent.com/pm6422/passport/master/passport-server/images/07.png)
![image](https://raw.githubusercontent.com/pm6422/passport/master/passport-server/images/08.png)

## 联系方式(Contact)
- WeChat ID: pm6422
- Email: pm6422@126.com

