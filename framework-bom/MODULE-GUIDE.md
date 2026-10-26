# framework-bom 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

发布 spring-framework-bom，通过依赖约束统一 Spring 模块版本。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [framework-bom.gradle](framework-bom.gradle) | 构建、版本约束或文档聚合入口 |

## 建议阅读路径

本模块没有业务核心类；阅读 constraints 与 MavenPublication，理解 BOM 管版本但不会自动引入所有模块。

## 模块依赖

依据本目录的 [framework-bom.gradle](framework-bom.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：无直接静态声明。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。

该模块通过循环为全部 `spring-*` 模块添加版本约束；约束关系不等于运行时依赖。
