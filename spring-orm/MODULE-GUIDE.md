# spring-orm 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

整合 JPA/Hibernate，管理持久化工厂、EntityManager 生命周期、事务与异常转换。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [LocalContainerEntityManagerFactoryBean](src/main/java/org/springframework/orm/jpa/LocalContainerEntityManagerFactoryBean.java) | 构建容器式 JPA 工厂 |
| [JpaTransactionManager](src/main/java/org/springframework/orm/jpa/JpaTransactionManager.java) | JPA 事务管理 |
| [SharedEntityManagerCreator](src/main/java/org/springframework/orm/jpa/SharedEntityManagerCreator.java) | 创建共享 EntityManager 代理 |
| [PersistenceAnnotationBeanPostProcessor](src/main/java/org/springframework/orm/jpa/support/PersistenceAnnotationBeanPostProcessor.java) | 处理 PersistenceContext 等注入 |
| [HibernateJpaDialect](src/main/java/org/springframework/orm/jpa/vendor/HibernateJpaDialect.java) | 适配 Hibernate 的 JPA 行为 |

## 建议阅读路径

沿工厂初始化和 JpaTransactionManager.doBegin 阅读，理解 EntityManager 如何加入事务。

## 模块依赖

依据本目录的 [spring-orm.gradle](spring-orm.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)、[spring-jdbc](../spring-jdbc/MODULE-GUIDE.md)、[spring-tx](../spring-tx/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-aop](../spring-aop/MODULE-GUIDE.md)、[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-web](../spring-web/MODULE-GUIDE.md)。
- **测试依赖**：[spring-core-test](../spring-core-test/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
