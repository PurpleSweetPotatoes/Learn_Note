# HashMap

> jdk8   数组+链表/红黑树: key-value, Entry(节点)

```java
HashMap<String, String > map = new HashMap<>();
// "123" --> hashCode --> 数组长度取余 --> 确定数组下标 --> 下标重复 --> 放入下标节点next指向之前节点
map.put("123","asd");
```

具体流程图
<img src="http://blog-imgs.nos-eastchina1.126.net/1626083051.png" style="zoom:40%;" />

## 红黑树

> 根节点是黑色
> 叶节点为黑色(叶节点为null节点，即尾节点的next的节点)
> 一个节点是红的，其子节点都为黑色(父子节点不能同为红色节点)
> 某节点到其子孙节点上的黑色节点个数相同

新插入节点默认红色，根据定义判断修改节点颜色

