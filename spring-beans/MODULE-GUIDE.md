# spring-beans 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

实现 Bean 定义、注册、创建、属性填充和依赖注入，是 IoC 容器的核心。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [BeanFactory](src/main/java/org/springframework/beans/factory/BeanFactory.java) | 容器顶层访问契约 |
| [BeanDefinition](src/main/java/org/springframework/beans/factory/config/BeanDefinition.java) | 描述如何创建 Bean |
| [DefaultListableBeanFactory](src/main/java/org/springframework/beans/factory/support/DefaultListableBeanFactory.java) | 维护定义并解析依赖 |
| [AbstractBeanFactory](src/main/java/org/springframework/beans/factory/support/AbstractBeanFactory.java) | 实现 getBean 主流程 |
| [AbstractAutowireCapableBeanFactory](src/main/java/org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory.java) | 实例化、填充属性与初始化 |
| [DefaultSingletonBeanRegistry](src/main/java/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java) | 管理单例缓存和创建状态 |
| [BeanPostProcessor](src/main/java/org/springframework/beans/factory/config/BeanPostProcessor.java) | Bean 初始化前后的扩展点 |

## 建议阅读路径

沿 getBean → doGetBean → createBean → doCreateBean 阅读，重点观察单例缓存与 BeanPostProcessor 的调用位置。

## 模块依赖

依据本目录的 [spring-beans.gradle](spring-beans.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-core](../spring-core/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：[spring-core-test](../spring-core-test/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
