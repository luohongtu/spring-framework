# spring-jdbc 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

封装 JDBC 资源管理、SQL 执行、结果映射和异常转换，并实现 JDBC 事务。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [JdbcTemplate](src/main/java/org/springframework/jdbc/core/JdbcTemplate.java) | JDBC 操作模板 |
| [NamedParameterJdbcTemplate](src/main/java/org/springframework/jdbc/core/namedparam/NamedParameterJdbcTemplate.java) | 命名参数支持 |
| [DataSourceUtils](src/main/java/org/springframework/jdbc/datasource/DataSourceUtils.java) | 获取和释放与事务关联的连接 |
| [DataSourceTransactionManager](src/main/java/org/springframework/jdbc/datasource/DataSourceTransactionManager.java) | JDBC 事务管理 |
| [JdbcTransactionManager](src/main/java/org/springframework/jdbc/support/JdbcTransactionManager.java) | 增强提交回滚异常转换 |
| [SQLExceptionTranslator](src/main/java/org/springframework/jdbc/support/SQLExceptionTranslator.java) | SQL 异常转换契约 |
| [RowMapper](src/main/java/org/springframework/jdbc/core/RowMapper.java) | 逐行结果映射 |

## 建议阅读路径

从 JdbcTemplate.query 跟到连接获取、回调执行和资源释放，再联读 DataSourceTransactionManager。

## 模块依赖

依据本目录的 [spring-jdbc.gradle](spring-jdbc.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)、[spring-tx](../spring-tx/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-context](../spring-context/MODULE-GUIDE.md)。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
