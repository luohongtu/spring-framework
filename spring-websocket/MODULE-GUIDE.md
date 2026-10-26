# spring-websocket 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

提供 WebSocket 会话、握手与处理器，并整合 SockJS 和 STOMP 消息代理。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [WebSocketHandler](src/main/java/org/springframework/web/socket/WebSocketHandler.java) | 连接和消息处理契约 |
| [WebSocketSession](src/main/java/org/springframework/web/socket/WebSocketSession.java) | 会话抽象 |
| [DefaultHandshakeHandler](src/main/java/org/springframework/web/socket/server/support/DefaultHandshakeHandler.java) | 握手实现入口 |
| [WebSocketHttpRequestHandler](src/main/java/org/springframework/web/socket/server/support/WebSocketHttpRequestHandler.java) | Servlet 请求到握手的适配 |
| [SubProtocolWebSocketHandler](src/main/java/org/springframework/web/socket/messaging/SubProtocolWebSocketHandler.java) | 分发子协议消息 |
| [StompSubProtocolHandler](src/main/java/org/springframework/web/socket/messaging/StompSubProtocolHandler.java) | 处理 STOMP 帧 |
| [WebSocketMessageBrokerConfigurer](src/main/java/org/springframework/web/socket/config/annotation/WebSocketMessageBrokerConfigurer.java) | 消息代理配置扩展点 |

## 建议阅读路径

先读握手与原生 WebSocketHandler，再追 STOMP 如何接入 spring-messaging。

## 模块依赖

依据本目录的 [spring-websocket.gradle](spring-websocket.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)、[spring-web](../spring-web/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-messaging](../spring-messaging/MODULE-GUIDE.md)、[spring-webmvc](../spring-webmvc/MODULE-GUIDE.md)。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
