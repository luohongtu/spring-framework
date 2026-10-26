# spring-context 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

在 BeanFactory 上构建应用上下文，整合配置类、组件扫描、事件、国际化、生命周期、缓存、调度等能力。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [ApplicationContext](src/main/java/org/springframework/context/ApplicationContext.java) | 应用上下文契约 |
| [AbstractApplicationContext](src/main/java/org/springframework/context/support/AbstractApplicationContext.java) | refresh 和关闭的主流程 |
| [AnnotationConfigApplicationContext](src/main/java/org/springframework/context/annotation/AnnotationConfigApplicationContext.java) | 注解配置容器入口 |
| [ConfigurationClassPostProcessor](src/main/java/org/springframework/context/annotation/ConfigurationClassPostProcessor.java) | 触发配置类处理 |
| [ConfigurationClassParser](src/main/java/org/springframework/context/annotation/ConfigurationClassParser.java) | 解析配置类、导入和扫描 |
| [AnnotationConfigUtils](src/main/java/org/springframework/context/annotation/AnnotationConfigUtils.java) | 注册注解配置所需的基础设施 |
| [DefaultLifecycleProcessor](src/main/java/org/springframework/context/support/DefaultLifecycleProcessor.java) | 启动和停止生命周期组件 |
| [ApplicationEventMulticaster](src/main/java/org/springframework/context/event/ApplicationEventMulticaster.java) | 事件分发契约 |

自动注入的关键实现位于另一个模块：
[AutowiredAnnotationBeanPostProcessor](../spring-beans/src/main/java/org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor.java)。
它由上下文注册，在 Bean 创建期间完成注入。

## 建议阅读路径

优先读 refresh 的各阶段，再分别追踪配置解析、自动注入和 finishBeanFactoryInitialization。

## 模块依赖

依据本目录的 [spring-context.gradle](spring-context.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-aop](../spring-aop/MODULE-GUIDE.md)、[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)、[spring-expression](../spring-expression/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-instrument](../spring-instrument/MODULE-GUIDE.md)。
- **测试依赖**：[spring-core-test](../spring-core-test/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
