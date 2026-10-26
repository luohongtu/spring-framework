# spring-context-support 模块学习指南

[返回项目总览](../SPRING-SOURCE-GUIDE.md)

## 模块作用

整合缓存、邮件、Quartz、FreeMarker 等可选基础设施。

## 核心类型与源码入口

| 类型 | 阅读重点 |
| --- | --- |
| [JavaMailSenderImpl](src/main/java/org/springframework/mail/javamail/JavaMailSenderImpl.java) | 邮件发送实现 |
| [SchedulerFactoryBean](src/main/java/org/springframework/scheduling/quartz/SchedulerFactoryBean.java) | 把 Quartz 调度器纳入容器生命周期 |
| [SpringBeanJobFactory](src/main/java/org/springframework/scheduling/quartz/SpringBeanJobFactory.java) | 为 Quartz Job 填充属性 |
| [FreeMarkerConfigurationFactory](src/main/java/org/springframework/ui/freemarker/FreeMarkerConfigurationFactory.java) | 创建 FreeMarker 配置 |
| [CaffeineCacheManager](src/main/java/org/springframework/cache/caffeine/CaffeineCacheManager.java) | 管理 Caffeine 缓存 |
| [JCacheCacheManager](src/main/java/org/springframework/cache/jcache/JCacheCacheManager.java) | 适配 JCache 缓存 |

## 建议阅读路径

选择一个已熟悉的外部库，观察 Spring 如何通过工厂和生命周期接口完成集成。

## 模块依赖

依据本目录的 [spring-context-support.gradle](spring-context-support.gradle)；仅列仓库内模块，不展开第三方库和传递依赖。

- **API 依赖**：[spring-beans](../spring-beans/MODULE-GUIDE.md)、[spring-context](../spring-context/MODULE-GUIDE.md)、[spring-core](../spring-core/MODULE-GUIDE.md)。
- **实现依赖**：无直接静态声明。
- **可选集成依赖**：[spring-jdbc](../spring-jdbc/MODULE-GUIDE.md)、[spring-tx](../spring-tx/MODULE-GUIDE.md)。
- **测试依赖**：[spring-context](../spring-context/MODULE-GUIDE.md)。
- **测试运行依赖**：无直接静态声明。

测试夹具依赖未在上面展开；根构建还会统一应用框架平台约束，详见项目总览。
