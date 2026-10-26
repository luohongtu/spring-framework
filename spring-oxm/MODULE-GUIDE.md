# spring-oxm 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

统一对象与 XML 之间的编组、解组接口，并适配 JAXB 等实现。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [Marshaller](src/main/java/org/springframework/oxm/Marshaller.java) | 对象转 XML 契约 |
| [Unmarshaller](src/main/java/org/springframework/oxm/Unmarshaller.java) | XML 转对象契约 |
| [Jaxb2Marshaller](src/main/java/org/springframework/oxm/jaxb/Jaxb2Marshaller.java) | JAXB 适配实现 |
| [AbstractMarshaller](src/main/java/org/springframework/oxm/support/AbstractMarshaller.java) | 处理不同 XML Source/Result 的公共逻辑 |

## 建议阅读路径

从 Jaxb2Marshaller.marshal/unmarshal 入手，观察统一接口如何屏蔽 XML 表示差异。

## 模块依赖

依据本目录的 [spring-oxm.gradle](spring-oxm.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：[spring-context](../spring-context/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
