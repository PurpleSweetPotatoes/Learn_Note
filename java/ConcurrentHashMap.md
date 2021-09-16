# ConcurrentHashMap

> 保证线程安全，其可能会造成阻塞，取决于是否多条线程落到同一个segment下
> segment个数在初始化时确定，扩容只是扩展segment中的hashTable

初始化时先初始化一个segment作为标准，其余segment做懒加载

## Segment

> concurrentHashMap含有一个segment的集合，segment中包含有HashTable(线程安全)

## HashTable

当多线程同时在一个table中进行put操作逻辑

1.获取锁，获取到锁的线程进行put操作，使用头插法

2.未获取到锁

+ 缓存当前头结点并自旋
+ 获取到锁后判断头结点是否改变，改变则重新修改头结点
+ 头插法插入
+ 自旋最大次数为64次

lock锁不自带自旋，需要自己手写
synchronized锁 系统控制