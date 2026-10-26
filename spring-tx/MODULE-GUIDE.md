# spring-tx 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

定义统一事务抽象，实现命令式和响应式事务流程、事务属性解析与声明式事务拦截。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [PlatformTransactionManager](src/main/java/org/springframework/transaction/PlatformTransactionManager.java) | 命令式事务管理契约 |
| [AbstractPlatformTransactionManager](src/main/java/org/springframework/transaction/support/AbstractPlatformTransactionManager.java) | 事务传播、提交和回滚模板 |
| [TransactionTemplate](src/main/java/org/springframework/transaction/support/TransactionTemplate.java) | 编程式事务模板 |
| [TransactionInterceptor](src/main/java/org/springframework/transaction/interceptor/TransactionInterceptor.java) | 声明式事务拦截入口 |
| [TransactionAspectSupport](src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java) | 事务拦截通用流程 |
| [TransactionSynchronizationManager](src/main/java/org/springframework/transaction/support/TransactionSynchronizationManager.java) | 线程绑定的事务资源 |
| [ReactiveTransactionManager](src/main/java/org/springframework/transaction/ReactiveTransactionManager.java) | 响应式事务管理契约 |
| [TransactionalOperator](src/main/java/org/springframework/transaction/reactive/TransactionalOperator.java) | 响应式事务操作入口 |

## 建议阅读路径

沿 TransactionInterceptor → invokeWithinTransaction → getTransaction/commit/rollback 阅读，再看 REQUIRED 和 REQUIRES_NEW。

## 模块依赖

依据本目录的 [spring-tx.gradle](spring-tx.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-aop](../spring-aop/MODULE-GUIDE.md)、[spring-context](../spring-context/MODULE-GUIDE.md)。
- **测试依赖**：[spring-core-test](../spring-core-test/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
