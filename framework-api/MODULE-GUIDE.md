# framework-api 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

聚合各 Spring 模块的 Java/Kotlin API 文档，用于生成统一 API 站点。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [framework-api.gradle](framework-api.gradle) | 构建、版本约束或文档聚合入口 |

## 建议阅读路径

本模块没有业务核心类；先读 framework-api.gradle 的 javadoc 依赖收集和 JDK 工具链配置。

## 模块依赖

依据本目录的 [framework-api.gradle](framework-api.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：无直接静态声明。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。

该模块通过循环收集全部 `spring-*` 模块的 `javadoc` 依赖，属于文档聚合关系。
