# spring-r2dbc 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

基于 R2DBC 提供响应式数据库访问和事务集成。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [DatabaseClient](src/main/java/org/springframework/r2dbc/core/DatabaseClient.java) | 响应式数据库客户端 |
| [DefaultDatabaseClient](src/main/java/org/springframework/r2dbc/core/DefaultDatabaseClient.java) | SQL 执行和结果消费实现 |
| [ConnectionFactoryUtils](src/main/java/org/springframework/r2dbc/connection/ConnectionFactoryUtils.java) | 管理事务关联连接 |
| [R2dbcTransactionManager](src/main/java/org/springframework/r2dbc/connection/R2dbcTransactionManager.java) | 响应式数据库事务 |

## 建议阅读路径

沿 DatabaseClient.sql 的执行链阅读，再观察 Reactor Context 如何关联事务连接，与 JDBC 的线程绑定方式比较。

## 模块依赖

依据本目录的 [spring-r2dbc.gradle](spring-r2dbc.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)、[spring-tx](../spring-tx/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
