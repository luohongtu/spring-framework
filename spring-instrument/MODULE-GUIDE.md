# spring-instrument 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

提供 Java Agent 入口，保存 JVM Instrumentation，供加载期织入等能力使用。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [InstrumentationSavingAgent](src/main/java/org/springframework/instrument/InstrumentationSavingAgent.java) | 通过 premain/agentmain 保存 Instrumentation |

## 建议阅读路径

先读这个小类，再到 spring-context 的 InstrumentationLoadTimeWeaver 查看消费者；它不是完整 AOP 实现。

## 模块依赖

依据本目录的 [spring-instrument.gradle](spring-instrument.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：无直接静态声明。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
