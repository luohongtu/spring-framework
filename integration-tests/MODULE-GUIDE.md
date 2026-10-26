# integration-tests 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

验证多个模块组合后的行为，包括容器、代理、事务、持久化等跨模块场景。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [integration-tests.gradle](integration-tests.gradle) | 构建、版本约束或文档聚合入口 |

## 建议阅读路径

本模块以测试为入口，不生产业务 API；从 src/test 中选择熟悉的场景，再反向定位被测试模块。

## 模块依赖

依据本目录的 [integration-tests.gradle](integration-tests.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：无直接静态声明。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：[spring-aop](../spring-aop/MODULE-GUIDE.md)、[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)、[spring-core-test](../spring-core-test/MODULE-GUIDE.md)、[spring-expression](../spring-expression/MODULE-GUIDE.md)、[spring-jdbc](../spring-jdbc/MODULE-GUIDE.md)、[spring-orm](../spring-orm/MODULE-GUIDE.md)、[spring-test](../spring-test/MODULE-GUIDE.md)、[spring-tx](../spring-tx/MODULE-GUIDE.md)、[spring-web](../spring-web/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。

## 测试样例入口

- [AopNamespaceHandlerAdviceOrderIntegrationTests](src/test/java/org/springframework/aop/config/AopNamespaceHandlerAdviceOrderIntegrationTests.java)
- [AopNamespaceHandlerScopeIntegrationTests](src/test/java/org/springframework/aop/config/AopNamespaceHandlerScopeIntegrationTests.java)
- [AdvisorAutoProxyCreatorIntegrationTests](src/test/java/org/springframework/aop/framework/autoproxy/AdvisorAutoProxyCreatorIntegrationTests.java)
- [AspectJAutoProxyAdviceOrderIntegrationTests](src/test/java/org/springframework/aop/framework/autoproxy/AspectJAutoProxyAdviceOrderIntegrationTests.java)
- [RuntimeHintsAgentTests](src/test/java/org/springframework/aot/RuntimeHintsAgentTests.java)
