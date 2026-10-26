# spring-webmvc 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

实现 Servlet 技术栈的 Spring MVC，包括请求分发、控制器调用、参数绑定与视图渲染。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [DispatcherServlet](src/main/java/org/springframework/web/servlet/DispatcherServlet.java) | 请求分发中心 |
| [HandlerMapping](src/main/java/org/springframework/web/servlet/HandlerMapping.java) | 定位处理器 |
| [HandlerAdapter](src/main/java/org/springframework/web/servlet/HandlerAdapter.java) | 调用处理器 |
| [RequestMappingHandlerMapping](src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.java) | 注册和匹配请求映射 |
| [RequestMappingHandlerAdapter](src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter.java) | 调用注解控制器 |
| [ServletInvocableHandlerMethod](src/main/java/org/springframework/web/servlet/mvc/method/annotation/ServletInvocableHandlerMethod.java) | 处理方法返回值 |
| [ViewResolver](src/main/java/org/springframework/web/servlet/ViewResolver.java) | 解析视图 |

## 建议阅读路径

沿 DispatcherServlet.doDispatch → getHandler → HandlerAdapter.handle 阅读，再追参数解析和返回值处理。

## 模块依赖

依据本目录的 [spring-webmvc.gradle](spring-webmvc.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-aop](../spring-aop/MODULE-GUIDE.md)、[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)、[spring-expression](../spring-expression/MODULE-GUIDE.md)、[spring-web](../spring-web/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-context-support](../spring-context-support/MODULE-GUIDE.md)、[spring-oxm](../spring-oxm/MODULE-GUIDE.md)。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
