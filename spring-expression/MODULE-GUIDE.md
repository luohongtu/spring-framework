# spring-expression 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

实现 Spring Expression Language（SpEL），支持属性访问、方法调用、模板与表达式求值。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [ExpressionParser](src/main/java/org/springframework/expression/ExpressionParser.java) | 解析入口契约 |
| [SpelExpressionParser](src/main/java/org/springframework/expression/spel/standard/SpelExpressionParser.java) | SpEL 解析器 |
| [SpelExpression](src/main/java/org/springframework/expression/spel/standard/SpelExpression.java) | 表达式求值入口 |
| [StandardEvaluationContext](src/main/java/org/springframework/expression/spel/support/StandardEvaluationContext.java) | 保存解析器和求值配置 |
| [SpelNodeImpl](src/main/java/org/springframework/expression/spel/ast/SpelNodeImpl.java) | 表达式语法树节点基础实现 |
| [SpelCompiler](src/main/java/org/springframework/expression/spel/standard/SpelCompiler.java) | 表达式编译优化 |

## 建议阅读路径

用一个属性访问表达式观察 parseExpression 与 getValue，区分解析阶段和求值阶段。

## 模块依赖

依据本目录的 [spring-expression.gradle](spring-expression.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-core](../spring-core/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：无直接静态声明。
- **测试依赖**：无直接静态声明。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
