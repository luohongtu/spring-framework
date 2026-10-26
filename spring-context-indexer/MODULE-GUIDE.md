# spring-context-indexer 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

通过编译期注解处理生成候选组件索引 META-INF/spring.components，用于理解组件扫描的索引机制。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [CandidateComponentsIndexer](src/main/java/org/springframework/context/index/processor/CandidateComponentsIndexer.java) | 注解处理器入口 |
| [MetadataCollector](src/main/java/org/springframework/context/index/processor/MetadataCollector.java) | 收集候选组件元数据 |
| [MetadataStore](src/main/java/org/springframework/context/index/processor/MetadataStore.java) | 读写组件索引 |
| [IndexedStereotypesProvider](src/main/java/org/springframework/context/index/processor/IndexedStereotypesProvider.java) | 分析带索引标记的类型 |

## 建议阅读路径

从注解处理器 process 开始，跟踪类型信息如何变成索引文件；是否使用索引还需看上下文侧扫描实现。

## 模块依赖

依据本目录的 [spring-context-indexer.gradle](spring-context-indexer.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：无直接静态声明。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：[spring-context](../spring-context/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。

这是编译期工具。`CandidateComponentsIndexer` 自 6.1 起已弃用并标记待移除，源码建议转向 AOT 引擎；学习主线可暂时跳过本模块。
