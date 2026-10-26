# framework-docs 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

维护参考文档、可编译的 Java/Kotlin 示例以及文档站点构建。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [framework-docs.gradle](framework-docs.gradle) | 构建、版本约束或文档聚合入口 |

## 建议阅读路径

先看 modules/ROOT/nav.adoc、modules/ROOT/pages 与 src/main/java、src/main/kotlin；按 IoC、AOP、事务、Web 专题对照源码。

## 模块依赖

依据本目录的 [framework-docs.gradle](framework-docs.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：无直接静态声明。
- **实现依赖**：[spring-aspects](../spring-aspects/MODULE-GUIDE.md)、[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-context-support](../spring-context-support/MODULE-GUIDE.md)、[spring-core-test](../spring-core-test/MODULE-GUIDE.md)、[spring-jdbc](../spring-jdbc/MODULE-GUIDE.md)、[spring-jms](../spring-jms/MODULE-GUIDE.md)、[spring-test](../spring-test/MODULE-GUIDE.md)、[spring-web](../spring-web/MODULE-GUIDE.md)、[spring-webflux](../spring-webflux/MODULE-GUIDE.md)、[spring-webmvc](../spring-webmvc/MODULE-GUIDE.md)、[spring-websocket](../spring-websocket/MODULE-GUIDE.md)。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
