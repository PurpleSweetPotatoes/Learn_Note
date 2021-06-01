# 题目

>  oc对象是基于c/c++的结构体

**内存对齐**: 结构体大小必须是最大成员大小的倍数

利用clang转化文件为cpp
`xcrun -sdk iphoneos clang -arch arm64 -rewrite-objc xxx.m -o xxx.cpp`

```cpp
struct NSObject_IMPL {
	class isa; // 8个字节
}
```

## 一个 OC对象占用多少内存空间

64bit的系统，通过`class_getInstanceSize`和`malloc_size`函数查看

```objective-c
NSObject * obj = [[NSObject alloc] init];
// 此函数返回的是实例对象成员变量所占用的内存空间大小
NSLog("%zd",class_getInstanceSize([obj class]));
// 获得objc所指向的内存空间大小 >> 16 
// corefountion内部规定最小16字节
NSLog("%zd",malloc_size((__bridge void *)obj));
```

## isa指针指向哪里

+ **instance** (isa., 成员变量....) 指向class
+ **class** (isa, superclass, 属性、对象方法、协议、成员变量...) 指向 meta-class
+ **meta**-class (isa, superclass, 类方法...) 指向基类meta-class

![](http://blog-imgs.nos-eastchina1.126.net/1621503151.png)

## OC的类型集存放在哪里

对象方法、属性、成员变量、协议信息存放在class对象中

类方法，存放在元类中

## KVO的本质、如何手动触发KVO

使用addObserver后，runtime会动态创建一个kvo的子类对象，并让对象的isa指针指向该kvo类对象。kvo类对象会重写新对象的set方法。set方法中会增加willChangeValueForKey和didChangeValueForKey的方法调用,手动触发执行set中新加方法即可

## Category

> 编译之后底层结构`struct _category_t`，里面包含对象方法、类方法、协议、属性信息
>
> 在程序运行时runtime会将category信息合并到类信息中

```cpp
// 分类结构体
struct _category_t {
	const char *name; 
	struct _class_t *cls;
	const struct _method_list_t *instance_methods;
	const struct _method_list_t *class_methods;
	const struct _protocol_list_t *protocols;
	const struct _prop_list_t *properties;
}	
```

+ **分类和延展区别**

  分类是在程序运行时合并到类信息中，只能利用runtime动态绑定属性

  延展是在编译时就会将信息合并到类信息中，添加属性，生成get、set方法

+ **load和initialize**

  load首先执行父类的load方法，再执行子类的load方法，最后调用分类的load方法
  调用顺序和编译顺序一样。
  方法只会调用一次，通过类地址直接调用，未走消息转发机制所以并不会被覆盖

  initialize会在类第一次接收到消息时候调用
  会先调用父类再是子类，消息转发机制调用，会存在方法覆盖(父类的initialize可能调用多次)

+ 对象关联

  > AssociationsManager => AssociationsHashMap => ObjectAssociationMap => ObjectAssociation

  实现`manger`中包含 `associationsHashMap`=>{对象地址:对象字典}
  对象字典 => {key: 值对象信息(策略和值)}

## Block

> block本质也是一个OC对象，包含`__block_impl`, `__main_block_desc_0和所捕获的外部参数

```cpp
struct __main_block_impl_0 {
  struct __block_impl impl;
  struct __main_block_desc_0* Desc;
  //ivars 所捕获的外部参数
  ...
  //构建函数
  __main_block_impl_0(...) {
    ....
  }
}

struct __block_impl {
  void *isa;
  int Flags;
  int Reserved;
  void *FuncPtr; //指向具体执行的匿名函数
}

static struct __main_block_desc_0 {
  size_t reserved;
  size_t Block_size;
  
  //堆上block才有
  void (*copy)(struct __main_block_impl_0*, struct __main_block_impl_0*); 
  void (*dispose)(struct __main_block_impl_0*);
}
```

block捕捉环境变量(全局变量不需要捕获)

+ **对象** 直接引用
+ **基础数据** 值传递
+ **__block修饰的值** 会将变量包装为一个对象引用(MRC下使用__block来表示弱引用)
+ **static修饰的值** 直接引用地址

block的类型

+ **栈** 在栈里使用的block，对栈里的block进行copy操作会放入堆中
+ **堆** arc下block被赋值给强指针、作为为返回值或作为参数都会copy放入堆中
+ **全局** 没有捕获变量的block

## RunTime

> 运行时，Objective-c的动态性由RunTime API 支持

### isa

arm64以前`isa`只是一个普通指针，指向Class和Mete-Class内存地址
arm64开始优化`isa`，其变成了一个共用体(union)，需要 & 一个mask地址值来查找对应地址值

```c
union isa_t {
    isa_t() { }
    isa_t(uintptr_t value) : bits(value) { }

    uintptr_t bits;

private:
    Class cls;

public:
    struct {
        uintptr_t nonpointer        : 1;                              
        uintptr_t has_assoc         : 1;                                       
        uintptr_t has_cxx_dtor      : 1;                                       
        uintptr_t shiftcls          : 33; /*MACH_VM_MAX_ADDRESS 0x1000000000*/ 
        uintptr_t magic             : 6;                                       
        uintptr_t weakly_referenced : 1;                                       
        uintptr_t unused            : 1;                                       
        uintptr_t has_sidetable_rc  : 1;                                       
        uintptr_t extra_rc          : 19
    };

    bool isDeallocating() {
        return extra_rc == 0 && has_sidetable_rc == 0;
    }
    void setDeallocating() {
        extra_rc = 0;
        has_sidetable_rc = 0;
    }

    void setClass(Class cls, objc_object *obj);
    Class getClass(bool authenticated);
    Class getDecodedClass(bool authenticated);
};
```

其中使用c语言`位域`技术，比使用位运算更方便

```c
// 此结构体只占一个字节 :1 代表占1位
// 字节中按其中先后顺序 挨个占位排列
struct {
	char tall :1;
	char rich :1;
	char handsome :1
} _testAAA
```

**union**共用体

> 公用体内所有数据使用同一块内存空间

```c
// 其中struct主要用于说明保持可读性
// 实际使用的是bits
union {
	char bits;
  struct {
    char tall :1;
    char rich :1;
    char handsome :1
  }
}_testAAA
```

### 消息发送流程

![](http://blog-imgs.nos-eastchina1.126.net/1622385606.png)



## RunLoop

> 应用：Timer、PerformSelect、GCD、网络请求、事件响应、手势识别、界面刷新等等
>
> **Runloop**保持程序活性

![](http://blog-imgs.nos-eastchina1.126.net/1621998484.jpeg)

**主要类**

+ CFRunLoopRef (NSRunLoop是对CFRunLoopRef的包装)

+ CFRunLoopModeRef 代表当前runloop运行模式

  常见两种mode 
  KCFRrunLoopDefaultMode(NSDefaultRunloopMode) 默认model
  UITrackingRunLoopMode 界面跟踪mode,保证界面滑动不受其他mode影响 

+ CFRunLoopSourceRef

  包含source0和source1

  0: 处理触摸事件和preformselect:onthred:线程调用代码

  1: 基于线程port通信，系统事件捕捉

+ CFRunLoopTimerRef 用于preformselect:after: 延后调用代码

+ CFRunLoopObserverRef 用于监听Runloop状态、Runloop休眠前会刷新UI、清理autorelasepool

**状态**

```cpp
typedef CF_OPTIONS(CFOptionFlags, CFRunLoopActivity) {
  	kCFRunLoopEntry = (1UL << 0), // 进入runloop的时候
    kCFRunLoopBeforeTimers = (1UL << 1),// 执行timer前
    kCFRunLoopBeforeSources = (1UL << 2), // 执行事件源前
    kCFRunLoopBeforeWaiting = (1UL << 5),//休眠前
    kCFRunLoopAfterWaiting = (1UL << 6),//休眠后
    kCFRunLoopExit = (1UL << 7),// 退出
    kCFRunLoopAllActivities = 0x0FFFFFFFU
};
```

**运行逻辑**

<img src="http://blog-imgs.nos-eastchina1.126.net/1622003142.png" style="zoom:25%;" />

**常驻线程**

> 需要给线程创建一个Runloop，才能保活
> RunLoop中如果没有source会直接退出
> 开启RunLoop后需要对应停止CFRunLoopStop();

```objective-c
[[NSRunLoop currentRunLoop] addPort:[[NSPort alloc] init] forMode:NSDefaultRunLoopMode];
if (标志位) {
		[[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate distantFuture]];
}
// 直接run会导致无法退出该循环,具体可查run说明
// [[NSRunLoop currentRunLoop] run];
```

## 多线程

### 专业术语

> 在当前串行队列dispatch_sync执行任务会造成死锁

+ **同步** 在当前线程执行任务
+ **异步** 在新开子线程执行任务`主队列不会开子线程,会在当前线程执行`
+ **串行** 同时只能执行一个任务
+ **并发** 同时执行多个任务 

### 多线程方案

|    方案     |                     简介                     | 语言 | 生命周期管理 | 使用频率 |
| :---------: | :------------------------------------------: | :--: | :----------: | :------: |
|   pthread   | 跨平台通用多线程API<br />可移植、使用难度大  |  c   |  程序员管理  | 几乎不用 |
|  NSThread   | 面向对象使用<br />简单易用，直接操作线程对象 |  OC  |  程序员管理  | 偶尔使用 |
|     GCD     |     NSThread替代品<br />充分利用设备多核     |  C   |   自动管理   | 经常使用 |
| NSOperation |       基于GCD的封装<br />面向对象使用        |  OC  |   自动管理   | 经常使用 |

### 线程同步方案

> **自旋锁** 锁住时会一直占用cpu等待,目前已不建议使用。会出现优先级反转问题线程A优先级高 线程B优先级低
> B上锁执行代码，此时A进行阻塞等待。
> 因为A优先级高，cpu分配更多时间给A执行，造成无资源给B执行任务，即A一直执行阻塞、B无法执行而形成死锁
>
> **互斥锁** 发现被上锁后会进入休眠状态，等待解锁后进入活跃状态重新执行任务

#### OSSpinLock

> 导入libkern/OSAtomic.h
> 
>

```objective-c
#import <libkern/OSAtomic.h>
OSSpinLock lock = OS_SPINLOCK_INIT;
OSSpinLockLock(&lock); // 返现已经被上锁，后面线程会在这里阻塞，直到解锁
OSSpinLockUnlock(&lock);
```

#### os_unfair_lock

> 自旋锁 作为OSSpinLock替代品，目前性能最高
> 导入 os/lock.h

```objective-c
os_unfair_lock lock = OS_UNFAIR_LOCK_INIT;
os_unfair_lock_lock(&lock);
os_unfair_lock_unlock(&lock);
```

#### pthread_mutex

> 互斥锁 等待锁的线程会处于休眠状态
> 递归锁 允许同一线程对一把锁重复加锁
> 导入 pthread/pthread.h

```objective-c
// 递归锁 PTHREAD_RWLOCK_INITIALIZER
// 互斥锁 PTHREAD_MUTEX_INITIALIZER
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_lock(&mutex);
pthread_mutex_unlock(&mutex);
// 需要手动销毁
pthread_mutex_destroy(&mutex);
```

### 其他

+ dispatch_barrier_async 栅栏，队列必须为自己创建的队列
+ dispatch_semaphore 信号量
+ atomic 基本在mac上才使用，原子性
+ NSLock 基于pthread_mutex封装
+ NSCondition 基于pthread_mutex封装 
+ NSConditionLock 基于pthread_mutex封装
+ @synchronized

## 内存管理

> 深拷贝: 会有新的内存地址
> 浅拷贝: 指向原来的内存地址

### 内存布局

+ 代码段: 编译之后的代码
+ 数据段: 常量、全局变量、静态变量等
+ 栈: 程序控制调用区域
+ 堆: 程序员控制管理的对象存放区域