# Spring bean 生命周期

## 生命周期

> 实例化 => 属性赋值 =>  初始化 =>销毁
>
> createBeanInstance=>populateBean=>initializeBean=>destoryBean

+ **实例化**：创建对象，在内存中分配空间 

+ **属性赋值**：配置与对象相关的属性bean
+ **初始化**：为对象属性赋值
+ **销毁**：移除bean的相关属性，释放其内存空间

## 扩展点

> 用于在spring bean生命周期中进行切面操作

###  BeanPostProcessor和InstantiationAwareBeanPostProcessor

> 继承自BeanPostProcessor
> 实例化拦截接口，针对多个bean有效

拦截切入点

![](http://blog-imgs.nos-eastchina1.126.net/1620354918.png)

```java
public class BeanInstanceTest implements InstantiationAwareBeanPostProcessor {
  	// 实例化某个bean之前会调用此方法
  	@Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }
  	// 实例化某个bean后会调用此方法
  	@Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return false;
    }
  
  	// 以下两个方法针对初始化前后调用
  	
		// 初始化某bean之前会调用此方法
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
		// 初始化某bean后会调用此方法
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
```

## Aware相关接口

> 此接口的特点是功能丰富，常用于用户自定义扩展
>
> 所有的Aware方法都是在初始化阶段之前调用的！

基本做到见名知意，实际开发中较少使用

+ BeanNameAware
+ BeanClassLoaderAware
+ BeanFactoryAware

## 属性注入

### @Autowired 

1. spring容器中通过`byType`寻找到所有类型匹配的bean

2. 在bean集合中排除autowireCandidate=false的bean(默认值为true)

3. bean集合判断

   + 集合超过1个: 根据条件一次判断，直到筛选出唯一一个bean，未筛选出则报错

     + 根据主bean注解@Primarybean进行筛选

     + 根据优先级@Priority进行筛选
     + 通过`byName`进行筛选

   + 集合只有1个: 使用这个bean

   + 集合为空: 报错

### @Resource

1. 如果使用了`name`则直接在spring容器中找bean
2. 未使用`name`判断spring容器中是否有对象名称的bean
   + 有：直接使用
   + 没有: 根据类型找，对应bean集合只能有一个否则报错

## 初始化

> 初始化时可以配置InitializingBean接口,用于在属性赋值完成后调用afterPropertiesSet方法
>
> 通常做法直接使用@PostConstruct注解来标注方法，个人理解为对象生成后的一些具体业务操作