# spring-core-test 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

为框架自身提供动态编译、生成代码测试、运行时提示记录与断言工具。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [TestCompiler](src/main/java/org/springframework/core/test/tools/TestCompiler.java) | 动态编译测试代码 |
| [RuntimeHintsAgent](src/main/java/org/springframework/aot/agent/RuntimeHintsAgent.java) | 记录需要运行时提示的调用 |
| [RuntimeHintsRecorder](src/main/java/org/springframework/aot/test/agent/RuntimeHintsRecorder.java) | 控制提示记录范围 |
| [RuntimeHintsInvocations](src/main/java/org/springframework/aot/test/agent/RuntimeHintsInvocations.java) | 组织记录的调用以进行提示断言 |
| [MockSpringFactoriesLoader](src/main/java/org/springframework/core/test/io/support/MockSpringFactoriesLoader.java) | 便于测试 factories 加载 |

## 建议阅读路径

结合使用 TestCompiler 的框架测试阅读；区分内部测试工具与面向应用集成测试的 spring-test。

## 模块依赖

依据本目录的 [spring-core-test.gradle](spring-core-test.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-core](../spring-core/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
