# spring-aop 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

实现基于代理的面向切面编程，为事务、异步等提供方法拦截能力。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [ProxyFactory](src/main/java/org/springframework/aop/framework/ProxyFactory.java) | 编程式创建代理 |
| [AdvisedSupport](src/main/java/org/springframework/aop/framework/AdvisedSupport.java) | 保存代理配置和 Advisor |
| [DefaultAopProxyFactory](src/main/java/org/springframework/aop/framework/DefaultAopProxyFactory.java) | 选择代理实现 |
| [JdkDynamicAopProxy](src/main/java/org/springframework/aop/framework/JdkDynamicAopProxy.java) | JDK 动态代理调用入口 |
| [CglibAopProxy](src/main/java/org/springframework/aop/framework/CglibAopProxy.java) | 基于子类的代理实现 |
| [ReflectiveMethodInvocation](src/main/java/org/springframework/aop/framework/ReflectiveMethodInvocation.java) | 推进拦截器链 |
| [AbstractAutoProxyCreator](src/main/java/org/springframework/aop/framework/autoproxy/AbstractAutoProxyCreator.java) | 通过 BeanPostProcessor 自动包装 Bean |

## 建议阅读路径

跟踪 ProxyFactory.getProxy 到 invoke/proceed，再回看容器何时为 Bean 创建代理。

## 模块依赖

依据本目录的 [spring-aop.gradle](spring-aop.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：[spring-core-test](../spring-core-test/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
