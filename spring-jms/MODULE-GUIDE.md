# spring-jms 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

整合 Jakarta JMS，简化消息收发、监听容器、注解端点与事务协作。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [JmsTemplate](src/main/java/org/springframework/jms/core/JmsTemplate.java) | 消息发送与接收模板 |
| [DefaultMessageListenerContainer](src/main/java/org/springframework/jms/listener/DefaultMessageListenerContainer.java) | 维护监听线程和消费循环 |
| [JmsListenerAnnotationBeanPostProcessor](src/main/java/org/springframework/jms/annotation/JmsListenerAnnotationBeanPostProcessor.java) | 发现 JmsListener 方法 |
| [JmsListenerEndpointRegistry](src/main/java/org/springframework/jms/config/JmsListenerEndpointRegistry.java) | 管理监听容器 |
| [MessagingMessageListenerAdapter](src/main/java/org/springframework/jms/listener/adapter/MessagingMessageListenerAdapter.java) | 消息到方法调用的适配 |

## 建议阅读路径

从 JmsListener 注解处理追踪监听容器创建，再看容器如何接收并分发消息。

## 模块依赖

依据本目录的 [spring-jms.gradle](spring-jms.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)、[spring-messaging](../spring-messaging/MODULE-GUIDE.md)、[spring-tx](../spring-tx/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-aop](../spring-aop/MODULE-GUIDE.md)、[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-oxm](../spring-oxm/MODULE-GUIDE.md)。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
