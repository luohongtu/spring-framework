# spring-web 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

提供 HTTP、Web 应用上下文、Servlet 基础设施、客户端、消息转换与绑定支持，是 MVC 和 WebFlux 的公共基础。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [HttpMessageConverter](src/main/java/org/springframework/http/converter/HttpMessageConverter.java) | HTTP 消息转换契约 |
| [RestClient](src/main/java/org/springframework/web/client/RestClient.java) | 同步 HTTP 客户端 |
| [RestTemplate](src/main/java/org/springframework/web/client/RestTemplate.java) | 经典同步客户端模板 |
| [WebDataBinder](src/main/java/org/springframework/web/bind/WebDataBinder.java) | Web 参数绑定 |
| [ContextLoader](src/main/java/org/springframework/web/context/ContextLoader.java) | 启动根 Web 应用上下文 |
| [OncePerRequestFilter](src/main/java/org/springframework/web/filter/OncePerRequestFilter.java) | 控制请求过滤执行 |
| [HttpHandler](src/main/java/org/springframework/http/server/reactive/HttpHandler.java) | 响应式 HTTP 处理契约 |

## 建议阅读路径

先读 HTTP 消息转换与 WebDataBinder；Servlet 方向看 ContextLoader，响应式方向看 HttpHandler。

## 模块依赖

依据本目录的 [spring-web.gradle](spring-web.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-aop](../spring-aop/MODULE-GUIDE.md)、[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-oxm](../spring-oxm/MODULE-GUIDE.md)。
- **测试依赖**：[spring-core-test](../spring-core-test/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
