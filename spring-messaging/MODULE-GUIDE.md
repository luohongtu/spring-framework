# spring-messaging 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

提供消息、通道、处理器和方法调用抽象，并支持 STOMP、RSocket 等消息模型。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [Message](src/main/java/org/springframework/messaging/Message.java) | 消息头和消息体 |
| [MessageChannel](src/main/java/org/springframework/messaging/MessageChannel.java) | 发送通道契约 |
| [MessageHandler](src/main/java/org/springframework/messaging/MessageHandler.java) | 消息处理契约 |
| [ExecutorSubscribableChannel](src/main/java/org/springframework/messaging/support/ExecutorSubscribableChannel.java) | 通过执行器分发消息 |
| [SimpAnnotationMethodMessageHandler](src/main/java/org/springframework/messaging/simp/annotation/support/SimpAnnotationMethodMessageHandler.java) | 处理消息映射方法 |
| [SimpMessagingTemplate](src/main/java/org/springframework/messaging/simp/SimpMessagingTemplate.java) | 发送应用消息 |
| [RSocketRequester](src/main/java/org/springframework/messaging/rsocket/RSocketRequester.java) | RSocket 请求客户端 |

## 建议阅读路径

跟踪一条消息从通道到处理器，再看消息映射方法如何解析参数并处理返回值。

## 模块依赖

依据本目录的 [spring-messaging.gradle](spring-messaging.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-oxm](../spring-oxm/MODULE-GUIDE.md)。
- **测试依赖**：[spring-core-test](../spring-core-test/MODULE-GUIDE.md)。
- **测试运行依赖**：[spring-context](../spring-context/MODULE-GUIDE.md)。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
