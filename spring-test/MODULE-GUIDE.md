# spring-test 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

提供面向使用者的 Spring 集成测试工具，包含测试上下文、事务测试、MockMvc 和 WebTestClient。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [TestContextManager](src/main/java/org/springframework/test/context/TestContextManager.java) | 组织测试生命周期回调 |
| [TestContext](src/main/java/org/springframework/test/context/TestContext.java) | 测试上下文契约 |
| [SpringExtension](src/main/java/org/springframework/test/context/junit/jupiter/SpringExtension.java) | JUnit Jupiter 集成入口 |
| [DefaultCacheAwareContextLoaderDelegate](src/main/java/org/springframework/test/context/cache/DefaultCacheAwareContextLoaderDelegate.java) | 缓存与加载应用上下文 |
| [MockMvc](src/main/java/org/springframework/test/web/servlet/MockMvc.java) | 模拟 Servlet 请求 |
| [WebTestClient](src/main/java/org/springframework/test/web/reactive/server/WebTestClient.java) | 测试响应式或绑定服务器的 HTTP 行为 |
| [TestTransaction](src/main/java/org/springframework/test/context/transaction/TestTransaction.java) | 测试事务控制 |

## 建议阅读路径

先从 SpringExtension 跟到 TestContextManager，再读上下文缓存；Web 测试分别看 MockMvc 和 WebTestClient。

## 模块依赖

依据本目录的 [spring-test.gradle](spring-test.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-core](../spring-core/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-aop](../spring-aop/MODULE-GUIDE.md)、[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-jdbc](../spring-jdbc/MODULE-GUIDE.md)、[spring-orm](../spring-orm/MODULE-GUIDE.md)、[spring-tx](../spring-tx/MODULE-GUIDE.md)、[spring-web](../spring-web/MODULE-GUIDE.md)、[spring-webflux](../spring-webflux/MODULE-GUIDE.md)、[spring-webmvc](../spring-webmvc/MODULE-GUIDE.md)、[spring-websocket](../spring-websocket/MODULE-GUIDE.md)。
- **测试依赖**：[spring-context-support](../spring-context-support/MODULE-GUIDE.md)、[spring-core-test](../spring-core-test/MODULE-GUIDE.md)、[spring-oxm](../spring-oxm/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
