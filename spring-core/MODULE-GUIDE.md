# spring-core 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

提供资源访问、类型解析、转换、注解合并与 AOT 基础设施，是容器等模块的底座。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [Resource](src/main/java/org/springframework/core/io/Resource.java) | 统一描述文件、类路径和 URL 资源 |
| [DefaultResourceLoader](src/main/java/org/springframework/core/io/DefaultResourceLoader.java) | 把资源位置转换为 Resource |
| [ResolvableType](src/main/java/org/springframework/core/ResolvableType.java) | 解析泛型类型 |
| [ConversionService](src/main/java/org/springframework/core/convert/ConversionService.java) | 统一类型转换入口 |
| [AnnotationUtils](src/main/java/org/springframework/core/annotation/AnnotationUtils.java) | 注解查找工具 |
| [MergedAnnotations](src/main/java/org/springframework/core/annotation/MergedAnnotations.java) | 处理组合注解与属性覆盖 |
| [RuntimeHints](src/main/java/org/springframework/aot/hint/RuntimeHints.java) | 描述运行时反射、资源等提示 |

## 建议阅读路径

从 Resource 的不同实现开始，再看泛型与组合注解如何为依赖注入提供支持。

## 模块依赖

依据本目录的 [spring-core.gradle](spring-core.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：无直接静态声明。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
