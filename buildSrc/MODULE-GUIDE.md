# buildSrc 学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

本目录是 Gradle 构建逻辑，不是 Spring 运行时模块。

## 核心入口

- [JavaConventions](src/main/java/org/springframework/build/JavaConventions.java)：配置 Java 工具链和编译约定。
- [ConventionsPlugin](src/main/java/org/springframework/build/ConventionsPlugin.java)：组合项目构建约定。
- [RepositoriesPlugin](src/main/java/org/springframework/build/RepositoriesPlugin.java)：配置依赖仓库。
- [OptionalDependenciesPlugin](src/main/java/org/springframework/build/optional/OptionalDependenciesPlugin.java)：处理可选依赖。

[build.gradle](build.gradle) 声明构建工具依赖；这些依赖与应用运行依赖是不同层次。
