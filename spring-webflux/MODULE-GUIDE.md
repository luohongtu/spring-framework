# spring-webflux 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

实现基于 Reactive Streams 的响应式 Web 框架，提供注解控制器、函数式端点和 WebClient。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [DispatcherHandler](src/main/java/org/springframework/web/reactive/DispatcherHandler.java) | 响应式请求分发中心 |
| [RequestMappingHandlerMapping](src/main/java/org/springframework/web/reactive/result/method/annotation/RequestMappingHandlerMapping.java) | 响应式注解映射 |
| [RequestMappingHandlerAdapter](src/main/java/org/springframework/web/reactive/result/method/annotation/RequestMappingHandlerAdapter.java) | 响应式控制器适配 |
| [RouterFunctions](src/main/java/org/springframework/web/reactive/function/server/RouterFunctions.java) | 函数式路由工具 |
| [ServerResponse](src/main/java/org/springframework/web/reactive/function/server/ServerResponse.java) | 函数式响应契约 |
| [WebClient](src/main/java/org/springframework/web/reactive/function/client/WebClient.java) | 响应式 HTTP 客户端 |
| [WebFluxConfigurationSupport](src/main/java/org/springframework/web/reactive/config/WebFluxConfigurationSupport.java) | 组织 WebFlux 基础组件 |

## 建议阅读路径

从 DispatcherHandler.handle 阅读 Mono/Flux 链，与 MVC 对照；它不依赖 spring-webmvc。

## 模块依赖

依据本目录的 [spring-webflux.gradle](spring-webflux.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)、[spring-web](../spring-web/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-context-support](../spring-context-support/MODULE-GUIDE.md)。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
