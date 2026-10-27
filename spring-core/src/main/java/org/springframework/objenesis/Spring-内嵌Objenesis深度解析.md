# Spring 内嵌 Objenesis 深度解析

> 主题：`org.springframework.objenesis.Objenesis` 的作用、原理、设计动机，以及它与 Spring CGLIB AOP 的关系
> 适用：Spring 源码研读（`study/manual-annotation` 分支标注参考）、AOP 底层机制理解

---

## 1. 一句话总结

**`Objenesis` 的核心能力：绕过构造方法，直接分配内存创建 Java 对象实例，不执行任何构造器代码。**

> 注意：Spring 不是依赖外部 objenesis 包，而是**把 objenesis 源码内嵌打包到 spring-core 里**，包名重定位为 `org.springframework.objenesis`，避免第三方依赖冲突。

---

## 2. 作用与核心能力

普通反射 `Constructor.newInstance()` 必须调用构造函数，要求：要么有无参构造，要么传入匹配参数。而 Objenesis：

1. **不调用任何构造器**，直接在堆上分配对象内存；
2. 不在乎类有没有无参构造、构造器是不是 private、构造器会不会抛异常；
3. 底层靠 `sun.misc.Unsafe` / JVM 内部 API 实现；
4. 对象字段全部是默认零值（引用=null、数字=0），不会走构造器初始化逻辑。

### 2.1 Spring 里的唯一主战场：CGLIB AOP 代理（`ObjenesisCglibAopProxy`）

CGLIB 的原理：生成目标类的子类作为代理类。

痛点：**目标类可能只有有参构造，没有无参构造**。

- 老方案：反射调用代理子类构造器 → 触发父类（目标 Bean）构造器执行 → **目标构造器会被执行两次**，极易引发重复初始化问题；
- Objenesis 方案：直接 new 代理子类实例，**不跑构造器，目标类构造方法不会二次执行**。

源码逻辑（`ObjenesisCglibAopProxy#createProxyClassAndInstance` 简化）：

```java
// 1. ASM 生成代理 Class
Class<?> proxyClass = enhancer.createClass();
// 2. 优先 Objenesis 绕过构造器创建代理实例
// 3. 如果当前 JVM 不支持 objenesis（安全管理器、高版本 JDK 限制 Unsafe），fallback 到反射构造器
```

### 2.2 通用场景（objenesis 原生设计用途）

1. AOP、Mock 框架动态代理；
2. 序列化/反序列化（Hessian、Protostuff、Kryo）——对象状态从字节流恢复，不应走构造器初始化；
3. ORM 持久化框架——从数据库读数据组装对象；
4. 容器框架特殊实例化。

---

## 3. 原理：为什么能做到"跳过构造器"

### 3.1 底层根基：`sun.misc.Unsafe.allocateInstance(Class)`

```java
public native Object allocateInstance(Class<?> cls) throws InstantiationException;
```

行为：

1. 根据 Class 的对象布局，在堆上分配该对象所需内存；
2. 所有实例字段赋予 JVM 默认初始值（引用=null，boolean=false，数字=0）；
3. **完全不调用任何构造函数 `<init>`**。

限制：

- JDK 9+ 模块化之后，`sun.misc.Unsafe` 默认被模块壁垒保护；
- 高版本 JDK、GraalVM、安全管理器环境下，可能拿不到 Unsafe 或该方法被禁用。

> Objenesis 本质就是：封装 `Unsafe.allocateInstance`，同时做跨 JVM 兼容降级。

### 3.2 分层策略（自适应实例化工厂）

Objenesis 内置一套实例化器优先级链，启动时探测当前 JVM 支持哪种：

1. **UnsafeInstantiator（首选）**
   使用 `Unsafe.allocateInstance()`，能力最强，不调用构造器。
2. **ReflectionInstantiator（降级兜底）**
   Unsafe 拿不到/被禁止时，退回反射找无参构造 `cls.newInstance()` / `ctor.newInstance()`。
   ⚠️ 兜底方案**会执行构造器**——这就是 Spring AOP 里经典的「父类构造器执行两次」来源。
3. 旧虚拟机特殊适配（GCJ、Android、IBM J9 等），新版本基本废弃。

> Spring 内嵌版砍掉了大量老旧虚拟机适配，只保留 HotSpot 系核心实现。

### 3.3 简化伪代码

```java
public class Objenesis {
    private InstantiatorStrategy strategy;

    public <T> T newInstance(Class<T> clazz) {
        Instantiator<T> instantiator = strategy.getInstantiatorOf(clazz);
        return instantiator.newInstance(clazz);
    }
}

class UnsafeInstantiator<T> implements Instantiator<T> {
    private final Unsafe unsafe;

    @Override
    public T newInstance(Class<T> clazz) {
        // 核心！分配内存，跳过 <init>
        return (T) unsafe.allocateInstance(clazz);
    }
}
```

---

## 4. 为什么设计它？（设计动机）

**根本原因：Java 原生语法 + 标准反射，没有办法"只分配对象内存、不执行构造方法"；而 AOP 代理、序列化、ORM 这类框架场景，恰恰强烈需要这个能力。**

### 4.1 Java 原生层面的硬限制

Java 语言规范硬性规定：**创建对象实例，必须执行构造器 `<init>`**。

- `new X()`：一定调用构造器；
- `Constructor#newInstance()`：反射方式，同样一定会调用构造器；
- JDK 动态代理：基于接口，不需要继承，没有这个烦恼；
- CGLIB 代理：**继承目标类生成子类代理**，踩坑。

示例：

```java
class UserService {
    // 只有有参构造，没有无参构造
    public UserService(String name) {
        System.out.println("目标构造器执行！");
    }
}
```

CGLIB 生成子类 `UserService$$CGLIB$$xxx`。如果反射 new 这个代理子类：

```java
constructor.newInstance();
```

Java 继承语法规则：**子类构造器第一行隐式 super()，一定会调用父类构造器**。
后果：目标类 `UserService` 的构造器被执行一次——而它只是代理壳，真正的 target 是 Spring 已经实例化好的 Bean。于是**目标构造器跑两次**，极易触发重复初始化、数据库连接、日志重复、状态错乱等诡异问题。这就是当年老 `CglibAopProxy` 最大的 bug。

### 4.2 框架场景的通用诉求

| 场景 | 诉求 |
|---|---|
| CGLIB 子类代理（Spring 主场景） | 代理壳不需要初始化逻辑，只想要空壳实例，不要触发父类构造 |
| 序列化框架（Hessian、Kryo 等） | 对象状态从字节流恢复，构造器的校验/初始化逻辑不应执行 |
| ORM / Mock 框架 | 类只有私有构造或构造器带业务校验，正常反射无法实例化 |

共同特点：
- ✅ 对象字段的值不是由构造器赋予，而是后续通过反射直接 set 字段赋值；
- ❌ 不希望执行构造器里的业务初始化、校验逻辑。

Java 标准 API 没有这个能力，所以需要 Objenesis 封装底层 JVM 私有的 Unsafe 能力。

### 4.3 工程化封装（不只是简单包一层 Unsafe）

原生 `Unsafe.allocateInstance()` 坑非常多：JDK 版本差异、模块化壁垒、安全管理器限制、不同虚拟机实现差异、某些环境直接禁用。如果框架直接裸写 Unsafe，到处都是兼容性 if 判断和异常捕获。

所以 Objenesis 被设计成**自适应实例化工厂**：

1. 优先尝试 Unsafe 方案（理想：不执行构造器）；
2. 探测失败自动降级到反射无参构造兜底（牺牲特性、保证不崩溃，此时构造器会执行）；
3. 屏蔽不同 JVM、不同安全策略的底层差异，对外暴露统一简单接口 `newInstance(clazz)`。

> 对外接口稳定，底层策略自动适配环境，上层业务（Spring AOP 代码）不需要关心底层是 Unsafe 还是反射兜底。

### 4.4 Spring 为什么不自己写 Unsafe，而是内嵌 Objenesis？

1. Unsafe 是内部私有 API，兼容逻辑、异常分支繁琐；
2. Objenesis 沉淀了多年跨 JVM 适配逻辑，是成熟轮子；
3. Spring 早期直接依赖 objenesis 第三方包，版本冲突频发；后来直接把核心源码 copy 进 spring-core，重命名包名 `org.springframework.objenesis` 内嵌，**消除第三方依赖，自己掌控版本**。

---

## 5. 为什么 AOP 要先分配对象内存？（关键追问）

### 5.1 先分清两个对象

- **target**：Spring 正常实例化的原始业务 Bean（`UserService`），走正常构造、依赖注入、初始化流程。**这个对象是好的，不需要 Objenesis**。
- **proxy**：CGLIB ASM 动态生成的子类 `UserService$$SpringCGLIB$$0`，即**代理壳**。
    - proxy 本身没有业务逻辑；
    - 它内部持有 target 引用；所有方法进入 proxy → 回调 MethodInterceptor 拦截器 → 转发调用到 target；
    - **拦截器是绑定在这个 proxy 实例上的**。

👉 CGLIB 机制的硬性前提：**必须存在一个代理子类的实例**，方法调用才能路由到拦截器。这就是为什么要分配内存、创建 proxy 实例。

### 5.2 如果不用 Objenesis，普通反射创建 proxy 会发生什么？

Java 继承语法铁则：任何子类构造器执行，第一行默认隐式 `super()`，必须调用父类构造器。

```java
// 动态生成的代理子类伪代码
public class UserService$$SpringCGLIB$$0 extends UserService {
    // 编译器自动插入
    public UserService$$SpringCGLIB$$0() {
        super(); // 这里会调用 UserService 的构造方法！
    }
}
```

Spring 容器已提前创建好 `UserService(target)`。为了造代理壳再调用一次代理子类构造 → `super()` 跑一遍 → **目标 UserService 构造器第二次执行**。

构造器里如果有数据库初始化、发消息、计数器+1，就会执行两遍，业务错乱。

> 我们仅仅只想要一个壳子挂上拦截器，完全不希望触发父类（目标业务类）的任何构造逻辑。

### 5.3 Objenesis 在这里做了什么

```
Unsafe.allocateInstance(proxyClass)
```

1. 给代理子类分配堆内存，生成 proxy 实例；
2. **不执行代理子类的 `<init>` 构造方法**，所以不会执行里面隐式的 `super()`；
3. 自然就**不会触发父类 UserService 的构造**；
4. 拿到空 proxy 壳后，手动设置回调拦截器（MethodInterceptor）；
5. 后续外部拿到这个 proxy 壳，调用方法就走拦截逻辑。

### 5.4 时序对比

**✅ Objenesis 方案（`ObjenesisCglibAopProxy`）**

```
1. Spring 正常 new target（UserService，执行一次构造）
2. ASM 生成代理子类 Class
3. Objenesis 分配内存创建 proxy 壳，跳过所有构造
4. proxy 壳绑定拦截器，拦截器内部持有 target
5. 对外返回 proxy
```

**❌ 老版本普通 CglibAopProxy（反射构造）**

```
1. Spring 正常 new target（UserService，执行一次构造）
2. ASM 生成代理子类 Class
3. 反射调用代理子类构造器
4. 代理子类构造触发 super() → UserService 构造第二次执行
5. 绑定拦截器，返回 proxy
```

### 5.5 为什么 JDK 动态代理没有这个烦恼？

JDK 代理是**实现接口**，不是继承类：

```java
Proxy.newProxyInstance(...)
```

生成的代理类 `$Proxyxx` 实现接口，**不会继承业务类**，不存在 super() 调用业务类构造器的问题。所以 JDK 代理根本不需要 Objenesis，只有 CGLIB 继承式代理才踩这个坑。

---

## 6. 关键边界与坑（源码标注重点）

1. **只跳过实例构造器 `<init>`，静态代码块 `<clinit>` 仍然会执行**——Class 加载时静态块就跑了，不受 allocateInstance 控制；
2. **对象字段全部是默认零值**——构造器里的赋值、校验逻辑全部丢失；代理对象本身只是空壳，业务逻辑走 target；
3. **JDK 版本壁垒**——JDK 17+ 强模块化默认不能访问 `sun.misc.Unsafe`，需要 `--add-opens java.base/sun.misc=ALL-UNNAMED`，否则降级反射；
4. **GraalVM Native Image 直接禁用 Unsafe.allocateInstance**——AOT 编译时规避 Objenesis 路径，走别的代理方案；
5. **不绕过安全管理器**——安全策略禁止 Unsafe 时直接降级。

---

## 7. 三种实例化方式对比

| 方式 | 是否执行构造器 | 依赖 |
|---|---|---|
| `new X()` / `Constructor.newInstance()` | ✅ 执行 `<init>` | 反射 |
| `Unsafe.allocateInstance()` | ❌ 不执行 `<init>` | sun.misc.Unsafe |
| `Objenesis newInstance()` | 优先不执行，失败降级执行 | 封装 Unsafe + 自动探测 |

---

## 8. 设计模式解析

Objenesis 不是一个"用了一个设计模式"的库，而是一套**多个经典设计模式的组合**。逐层拆解如下。

### 8.1 策略模式（Strategy）——核心模式

**意图**：定义一族算法，各自封装、可互相替换，客户端与具体算法解耦。

- 接口：`InstantiatorStrategy`（实例化策略）
- 实现：
    - `StandardInstantiatorStrategy`：标准策略，内部再按 Unsafe → 反射 的优先级链自动降级
    - `SerializingInstantiatorStrategy`：序列化场景专用策略，行为贴近 `ObjectInputStream` 反序列化语义（保留序列化兼容性）
- 客户端：`ObjenesisStd` / `ObjenesisSerializer` 在构造时注入策略，运行时通过策略获取实例化器

```java
public class ObjenesisStd implements Objenesis {
    // 构造时注入策略，运行时只面向策略接口编程
    private final InstantiatorStrategy strategy;

    public ObjenesisStd() {
        this(new StandardInstantiatorStrategy());
    }

    public ObjenesisStd(InstantiatorStrategy strategy) {
        this.strategy = strategy;
    }
}
```

> 降级链（Unsafe → 反射）也是策略内部的自适应逻辑：策略封装"怎么实例化"的完整算法，调用方完全不感知底层差异。这正是"封装变化"的策略思想。

### 8.2 抽象工厂 + 工厂方法（Abstract Factory / Factory Method）

**意图**：定义一个创建对象的接口，让子类/实现决定实例化哪个具体类。

- `Objenesis` 接口本身就是实例化器工厂：`getInstantiatorOf(Class)` 返回 `Instantiator<T>`；
- `newInstance(Class)` 是工厂方法：统一入口，内部委托给策略工厂生产的实例化器；
- `ObjenesisStd`（普通）与 `ObjenesisSerializer`（序列化版）是两个工厂实现，产出行为不同的实例化器。

```java
public interface Objenesis {
    <T> T newInstance(Class<T> clazz);          // 工厂方法：创建实例
    <T> Instantiator<T> getInstantiatorOf(Class<T> clazz); // 工厂：产出实例化器
}
```

### 8.3 享元模式（Flyweight）/ 实例化器缓存

**意图**：共享细粒度对象，避免重复创建，节省内存与构建开销。

- 每个 Class 对应的 `Instantiator` 被缓存（`InstantiatorCache`，内部 `ConcurrentHashMap<Class, Instantiator>`）；
- 同一 Class 反复 `newInstance` 时，直接复用已缓存的实例化器，不再重新探测策略、重试 Unsafe 可用性；
- 缓存 key = Class（内部状态），value = Instantiator（可共享，无状态差异）。

```java
public class InstantiatorCache {
    private final ConcurrentHashMap<Class<?>, Instantiator<?>> cache = new ConcurrentHashMap<>();

    public <T> Instantiator<T> getInstantiatorOf(Class<T> clazz) {
        // computeIfAbsent：没有才创建，有则复用（享元）
        return (Instantiator<T>) cache.computeIfAbsent(clazz, this::createInstantiator);
    }
}
```

### 8.4 单例模式（Singleton）——使用层面

Objenesis 本身无状态（实例化器缓存内部管理状态），因此：
- `ObjenesisStd` / `ObjenesisSerializer` 在实际使用中**以单例持有**，全局共享一个工厂实例即可；
- Spring 内嵌版 `SpringObjenesis` 在 `ObjenesisCglibAopProxy` 中也是**单例字段**，整个容器生命周期复用，不做重复创建。

### 8.5 适配器模式（Adapter）——对 Unsafe 的封装

**意图**：将不兼容的接口（`sun.misc.Unsafe` 私有 API）转换为客户端可用的统一接口（`Instantiator`）。

- `UnsafeInstantiator` 内部持有 Unsafe，把 `unsafe.allocateInstance(clazz)` 包装成标准的 `Instantiator.newInstance(clazz)`；
- 同时对 Unsafe 不可用的环境做降级适配（反射实现 `ReflectionInstantiator` 是同一接口的另一个适配器）；
- 上层（Spring AOP）只依赖 `Instantiator` 接口，与底层 JVM 私有 API 完全解耦。

### 8.6 门面模式（Facade）——对外统一入口

`Objenesis` 接口 + `ObjenesisHelper` 静态工具类，对上层屏蔽了：策略选择、缓存、Unsafe 探测、异常降级等全部内部复杂度，只暴露 `newInstance(clazz)` 一个简单门面。

### 8.7 模式组合总结表

| 设计模式 | 在 Objenesis 中的落点 | 解决的问题 |
|---|---|---|
| 策略模式 | `InstantiatorStrategy` 接口 + Standard/Serializing 实现 | 不同场景（普通/序列化）可替换的实例化算法 |
| 抽象工厂/工厂方法 | `Objenesis` 接口、`getInstantiatorOf`、`newInstance` | 统一入口创建实例化器，隐藏创建细节 |
| 享元模式 | `InstantiatorCache` 按 Class 缓存实例化器 | 避免重复探测与创建，提升性能 |
| 单例模式 | `ObjenesisStd`/`SpringObjenesis` 全局单例持有 | 无状态工厂复用，容器内共享 |
| 适配器模式 | `UnsafeInstantiator`/`ReflectionInstantiator` 封装 Unsafe/反射 | 统一私有 API 与反射的差异接口 |
| 门面模式 | `Objenesis` 接口/`ObjenesisHelper` | 屏蔽内部复杂度，上层一行调用 |

> 一句话：**策略定算法、工厂出实例、缓存省开销、适配抹差异、门面稳接口**——五个模式各司其职，组合出这个"极小但健壮"的库。

---

## 9. 可直接写入标注分支的注释模板

```java
/**
 * Objenesis 设计目的：
 * Java 标准反射 / new 机制必然执行构造器 <init>。
 * 在 CGLIB 子类代理、序列化等场景，仅需要分配对象内存，不希望触发构造器（避免父类构造重复执行）。
 * 封装 Unsafe.allocateInstance 实现跳过构造器实例化，并内置自适应降级策略，屏蔽不同 JVM 环境差异。
 * Spring 使用它解决 CglibAopProxy 代理子类导致目标 Bean 构造方法执行两次的问题。
 */

/**
 * AOP 需要创建 CGLIB 代理子类实例（代理壳）用于挂载方法拦截器；
 * 如果通过构造器创建代理子类，JVM 继承规则会隐式执行 super()，触发目标业务类构造器二次执行。
 * 因此需要 Objenesis 直接为代理子类分配内存生成实例、跳过构造方法，规避父类构造重复执行问题。
 * 注意：该代理壳只是分发外壳，真实业务调用转发给已经正常初始化完成的 target 对象。
 */
```

---

## 10. 延伸阅读线索（关联知识点）

1. `ObjenesisCglibAopProxy` 与老版 `CglibAopProxy` 的源码差异；
2. Spring AOP 代理选择逻辑：JDK 动态代理 vs CGLIB（`DefaultAopProxyFactory`）；
3. `Unsafe` 在 JDK 高版本下的模块化访问限制与 `--add-opens` 参数；
4. GraalVM Native Image 对反射与 Unsafe 的限制，以及 Spring AOT 的应对。
