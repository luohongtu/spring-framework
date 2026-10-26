# framework-platform 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

集中管理本仓库构建和测试使用的第三方依赖版本与 BOM。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [framework-platform.gradle](framework-platform.gradle) | 构建、版本约束或文档聚合入口 |

## 建议阅读路径

本模块没有业务核心类；读 java-platform、api(platform(...)) 和 constraints，区分它与对外发布的 framework-bom。

## 模块依赖

依据本目录的 [framework-platform.gradle](framework-platform.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：无直接静态声明。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
