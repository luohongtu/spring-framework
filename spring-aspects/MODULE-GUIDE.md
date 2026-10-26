# spring-aspects 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

提供 AspectJ 切面，支持事务、异步、缓存和 Configurable 等织入场景。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [AnnotationTransactionAspect](src/main/java/org/springframework/transaction/aspectj/AnnotationTransactionAspect.aj) | AspectJ 注解事务切面 |
| [AnnotationAsyncExecutionAspect](src/main/java/org/springframework/scheduling/aspectj/AnnotationAsyncExecutionAspect.aj) | 异步方法切面 |
| [AnnotationBeanConfigurerAspect](src/main/java/org/springframework/beans/factory/aspectj/AnnotationBeanConfigurerAspect.aj) | 为容器外创建的对象接入依赖配置 |
| [AnnotationCacheAspect](src/main/java/org/springframework/cache/aspectj/AnnotationCacheAspect.aj) | 注解缓存切面 |

## 建议阅读路径

对比 AspectJ 织入与 spring-aop 代理的工作方式；本模块还包含 .aj 文件。

## 模块依赖

依据本目录的 [spring-aspects.gradle](spring-aspects.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：无直接静态声明。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-aop](../spring-aop/MODULE-GUIDE.md)、[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-context-support](../spring-context-support/MODULE-GUIDE.md)、[spring-orm](../spring-orm/MODULE-GUIDE.md)、[spring-tx](../spring-tx/MODULE-GUIDE.md)。
- **测试依赖**：[spring-core](../spring-core/MODULE-GUIDE.md)、[spring-test](../spring-test/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
