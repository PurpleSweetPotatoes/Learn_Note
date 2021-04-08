# 1 初识MySql

## 1.1 数据库分类

**关系型数据库** (SQL)

- MySQL, ORacle, Sql Server, DB2, SQLlite
- 通过表和表之间行和列之间的关系进行数据的存储

非关系型数据库 (NoSQL)

- Readis, MongDB
- 非关系型数据库,对象存储，通过对象的自身属性决定

## 1.2 连接数据库

命令行操作

```sql
mysql -uroot -p --连接数据库

update mysql.user set authentication_string=password("123456") where user='root' and host='localhost' -- 修改用户密码

show databases; --查看所有数据库

use school; -- 切换使用数据库

show tables; -- 查看所有表

describe student; -- 查看student表信息

create database westos; -- 创建一个数据库
```

**数据库xxx语言** CRUD增删改查

- DDL 定义
- DML 操作
- DQL 查询
- DCL 控制

# 2 数据库操作

> 代码中`[]`代表可选

## 2.1 数据库 (了解)

1. 创建数据库

```sql
CREATE DATABASE [IF NOT EXISTS] westons
```

2. 删除数据库

```sql
DROP DATABASE [IF EXISTS] westons
```

3. 使用数据库

```sql
USE `school`
```

4. 创建数据库

```sql
CREATE DATABASE [IF NOT EXISTS] westons
```

## 2.2 数据库的列类型

> 数值

|   名称    |      说明      |   字节大小   |
| :-------: | :------------: | :----------: |
|  tinyint  |    很小数据    |      1       |
| smallint  |    较小数据    |      2       |
| mediumint |    中等数据    |      3       |
|    int    | 标准整数(常用) |      4       |
|  bigint   |    较大数据    |      8       |
|   float   |     浮点数     |      4       |
|  double   |     浮点数     |      8       |
|  decimal  | 字符串式浮点数 | 金融计算使用 |

> 字符串

|   名称   |       说明       |   大小   |
| :------: | :--------------: | :------: |
|   char   |  字符串固定大小  |  0~255   |
| varchar  | 可变字符串(常用) | 0~65535  |
| tinytext |     微型文本     | 2^8 - 1  |
|   text   |      文本串      | 2^16 - 1 |

> 时间日期

|   名称    |             格式             |
| :-------: | :--------------------------: |
|   date    |          YYYY-MM-DD          |
|   time    |           HH:mm:ss           |
| datetime  |  YYYY-MM-DD HH:mm:ss(常用)   |
| timestamp | 1970.1.1到现在的毫秒数(常用) |
|   year    |             年份             |

## 2.3 数据库的字段属性(重点)



|      名称      |                 格式                 |
| :------------: | :----------------------------------: |
|    unsigned    |        无符号整数、不能为负数        |
|    NOT NULL    |                 非空                 |
| AUTO_INCREMENT | 自增、在上一条记录基础上+1，值为数字 |
|    zerofill    | 不足数位0填充: int(3),  5  -->  005  |
|    DEFAULT     |         默认值: DEFAULT '3'          |
|    COMMENT     |                 注释                 |
|  PRIMARY KEY   |        主键: PRIMARY KEY 'id'        |

## 2.4 创建表

```sql
CREATE TABLE IF NOT EXISTS `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
```

> 格式

```
CREATE TABLE IF NOT EXISTS `表名` (
  `字段` 类型 [属性] [索引] [注释],
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
```

## 2.5 表的类型

> 关于数据库引擎

|    特性    |  MYISAM(早年使用)  |       INNODB（现在使用）       |
| :--------: | :----------------: | :----------------------------: |
|  事务支持  |         n          |               y                |
| 数据行锁定 |         n          |               y                |
|  外检约束  |         n          |               y                |
|  全文索引  |         y          |               n                |
| 表空间大小 |        较小        |            较大,2倍            |
|    特点    | 节约空间、速度较快 | 安全性、事务性、多用户多表操作 |

> MySQL引擎在物理文件上的区别

MySQL本质上还是文件，一个文件夹对应一个数据库

+ innoDB 在数据库表中只有一个*.frm，以及上级目录下的idbata1文件
+ MYISAM对应文件 
  + *.frm 表结构定义文件
  + *.MYD 数据文件(data)
  + *.MYI 索引文件(index)

## 2.6 表的操作

> 修改

```sql
-- 修改表
ALTER TABLE student RENAME AS students

-- 增加表的字段
ALTER	TABLE students ADD age INT(4)

-- 修改字段的字段(重命名、修改约束)
ALTER	TABLE students MODIFY age VARCHAR(4)  --修改约束
ALTER	TABLE students CHANGE age age1 VARCHAR(2)  --修改约束

-- 删除表的字段
ALTER	TABLE students DROP age1
```

> 删除

```sql
-- 删除表
DROP TABLE IF EXISTS students
```

# 3 MySQL数据管理

## 3.1 外键(了解)

删除有外键关系的表时，必须先删除引用了外键的表

1. 在创建表的时候添加约束

```
CREATE TABLE IF NOT EXISTS `abc` (
  `id`int(4),
  ...
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `test` (
  `id`int(4),
  `testId` int(4)
  ...
  KEY `FK_testId` (`testId`),
  CONSTRAINT `FK_testId` FOREIGN KEY (`testId`) REFERENCES `abc` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
```

2. 修改表的时候添加外键

```
ALTER TABLE `test` ADD CONSTRAINT `FK_testId` FOREIGN KEY (`testId`) REFERENCES `abc` (`id`)
```



## 3.2 DML语言(记住)

DML语言: 数据操作语言

+ Insert
+ update
+ delete

3.3 添加

```sql
-- insert 主键自增可以直接省略,字段可省略,字段和值一一对应
-- insert into 表名 (colnum1,colnum2...) values (value1,value2...)
insert into `student` (`name`,`age`) values ('老王',18)
-- 同时插入多条
insert into 表名 (`字段1`,`字段2`...) values ('值1','值2')...
```

3.4 修改

```sql
-- update 表名 set colnum=value,[colnum=value...] where [条件] [AND] [条件]
update student set `name`='老王' where age=28
```

where操作符

|       操作符        | 含义                                 | 范围       |
| :-----------------: | ------------------------------------ | ---------- |
|          =          | 等于                                 | 5=6(false) |
|       <>或!=        | 不等于                               | 5!=6(true) |
|    <、<=、>、>=     | 小于、小于等于、大于、大于等于       |            |
| BETWEEN ... and ... | 闭合区间，在范围类 [2...5]，包含2和5 |            |

3.5 删除

> delete

```sql
-- delete from 表名 [where 条件]

delete from `student` -- 删除表里所有数据

```

> truncate(常用),会重新设置自增列，计数器归0。不会影响事务

```
-- truncate 完全清空表
truncate 表名
```

# 4 DQL查询数据(重点)

## 4.1 DQL:数据查询语言

查询操作字段`select`，最核心语言

> 通用模板语法

```sql
select [all | distinct]
field1 [as filed1_aslias], field2...
from table_name [as table_aslias]
[left | right | inner join table_name] -- 联合查询
[where ...] -- 条件查询
[group by ...] -- 指定分组字段
[having ] -- 过滤分组次要条件
[order by ...] -- 设置配方式
[limit {offset, row_count}] -- 分页查询 offset:起始位置 row_count:获取数量
```

> 简单查询

语法: select 字段,... from 表 [where 条件]

```sql
-- 查询所有
SELECT * FROM student
-- 查询指定字段
SELECT name from student
-- 条件查询指定字段
SELECT name from student WHERE age=21
-- 别名,使用as给字段或表取别名
select name as 名称 from student
-- 函数 concat(a,b):拼接a和b
select CONCAT('姓名:', name) as 名称 from student
-- 去重，去除重复的数据
select distinct name from student
```

## 4.2 where 条件子句

作用:用于检索符合条件的值

> 运算符条件

```sql
-- 分数大于60
select studentNo, score from student where score >=60
-- 学号1000学生的分数
select studentNo, score from student where studentNo=1000
-- 区间 分数在60-80之间的
select studentNo, score from student where score between 60 and 80
```

> 模糊查询

|   运算法    |           语法           |        描述         |
| :---------: | :----------------------: | :-----------------: |
|   is null   |        a is null         |     a为null为真     |
| is not null |      a is not null       |    a不为null为真    |
|   between   |    a between b and c     |   a在b和c之间为真   |
|    like     |     a like '字符串'      | a符合字符串要求为真 |
|     in      | a in (b,c,d) bcd为具体值 | a等于其中一个值为真 |

```sql
-- `%`代表多个字符，`_`代表任意一个字符
select * from student where name like '老_'
-- 查询指定id学生
select * from student where id in (1,2,4)
```

## 4.3 连表查询

> join 链接表 on 条件

```sql
-- inner join 学生表中的学号 = 成绩表中的学号 两个表的交集
select s.studentNO, s.studentName, studentResult from student as a inner join result as r
on s.studentNO = r.studentNO

-- right join
select s.studentNO, s.studentName, studentResult from student as a 
right join result as r
on s.studentNO = r.studentNO

-- left join
select s.studentNO, s.studentName, studentResult from student as a 
left join result as r
on s.studentNO = r.studentNO
```

|    操作    |                   描述                   |
| :--------: | :--------------------------------------: |
| inner join | 如果表中至少有一个匹配，两个表中任意字段 |
| left join  |  返回左表中所有的值，即使右表中没有匹配  |
| right join |    返回右表中所有值，即使左表没有匹配    |

## 4.4 分页排序

> 排序 order by 分页 limit

order by 要放在where后面

```sql
order by 字段 排序条件
limit a, b   -- a:起始位置 (page - 1) * pagesize, b:选取长度 pagesize
```

## 4.5 子查询

where [条件],该条件是计算出来的

相当于where中嵌套一个查询

```sql
where studentNo = (select studentNo from subject where subjectname='数据结构')

-- 查询数据结构分数大于等于80分的学生学号和姓名

-- 方式一
select stu.studentNo stu.studentName from student as stu
inner join scores as s
on s.studentNo = s.studentNo
inner join subject as su
on su.sujectNo = s.subjectNo
where s.scroe >= 80 and su.subjectname='数据结构'

-- 方式二
select stu.studentNo stu.studentName from student as stu
inner join scores as s
on s.studentNo = s.studentNo
where s.scroe >= 80 and s.subjectNo=(select subjectNo from subject where subjectname='数据结构')
```

## 4.6 分组过滤

```sql
-- 查询不同课程的平均分、最高分、最低分和平均分大于80
select subjecName, avg(scroe) as '平均分', max(score) as '最高分', min(score) as '最低分' from scores s
inner join subject sub
on s.subjectNo = sub.subjectNo
group by s.subjectNo -- 通过什么字段分组
having 平均分>80
```



# 5 MySQL函数

## 5.1 系统函数

[官网链接](https://dev.mysql.com/doc/refman/5.7/en/sql-function-reference.html)

## 5.2 聚合函数

```sql
select count(colnum) from 表名 -- 获取字段的个数 会忽略null
select count(*) from 表名 -- 不会忽略null
select count(1) from 表名 -- 

-- 计算
sum() -- 求和
avg() -- 平均
max() -- 最大
min() -- 最小
```

# 6 事务

MySQL默认开启事务,命令`set autocommit=value`,value:  0=>关闭  1=>开启

```sql
-- 开启事务
start transaction -- 标记事务开始
insert ...
update ...
-- 提交(成功)
commit
-- 回滚(失败)
rollback
-- 事务结束
```


> 事务原则: ACID原则 原子性、一致性、隔离性、持久性

+ **原子性**: 一起成功 一起失败
+ **一致性**：数据前后完整性一致
+ **持久性**: 事务一旦提交不可逆转
+ **隔离性** : 为每个用户开启事务不会被其他事务操作干扰

# 7 索引

> MySQL的索引是为了帮助高效获取数据的数据结构,在大数据量时效果明显

## 7.1 索引的分类

+ 主键索引 `PRIMARY KEY`
  + 唯一标示符，值不可重复
+ 唯一索引 `UNIQUE KEY`
  + 避免重复的列出现，值可重复
+ 常规索引 `KEY/INDEX`
  + 默认的，index或key关键字设置
+ 全文索引 `FullText`
  + 在特定的数据库引擎下才有，快速定位数据

## 7.2 索引原则

+ 索引不是越多越好
+ 经常变动数据不添加索引
+ 小数据量不需要索引
+ 一般放在常查询的数据上

# 8 数据库设计规范

> 良好的数据库

+ 节省内存空间
+ 保证数据库完整性
+ 方便我们开发系统

> 设计步骤

+ 收集信息，分析需求
  + 用户表 (用户信息)
  + 分类表 (文章分类)
  + 文章表 (文章信息)
  + 友联表 (友联信息)
  + 自定义表 (系统信息，某些关键字，或者一些想使用的keyvalue字段)
+ 标识实体 (需求落地)

## 8.1 三大范式

+ 第一范式 `原子性数据项,每一列不可再分`
+ 第二范式 `每张表只描述一件事`
+ 第三范式 `每一列都必需和主键直接相关，不能间接相关`

**范式和性能的问题**

关联查询不得超过三张表,超过三张有性能问题

# 9 JDBC (重点)

## 9.1 数据库驱动

mysql-connector-java

## 9.2 使用jdbc

1. 加载驱动 maven管理会自动加载
2. 数据库链接
3. 获取sql执行对象
4. 获取结果集
5. 输出结果集
6. 关闭相关对象

```java
//        // 加载驱动 固定写法, maven管理会自动加载驱动
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        // 用户信息和url
        String url = "jdbc:mysql://47.105.91.34:3306/school?useUnicode=true&characterEncoding=utf8&useSSL=true";
        String user = "root";
        String pwd = "123456";

        // 链接数据库,获取数据库对象
        Connection connection = null;
        Statement statement = null;
				ResultSet resultset = null;
        try {
            connection = DriverManager.getConnection(url, user, pwd);

            // 获取sql执行对象
            statement = connection.createStatement();
            String sql = "select * from student";
            
            // 获取结果集对象
            resultset = statement.executeQuery(sql);

            while (resultset.next()) {
                System.out.println(resultset.getObject("id"));
                System.out.println(resultset.getObject("name"));
                System.out.println(resultset.getObject("age"));
            }

            // 关闭相关对象
            resultset.close();
            statement.close();
            connection.close();

        } catch (Exception error) {
            System.out.println("报错了");
        }
```

## 9.3 statement对象

> 用于执行sql语句, 增删改使用executeUpdate, 查询使用executeQuery

CRUD

## 9.4 SQL注入

利用where条件设置为true来实现

```sql
-- name 和 pwd 由外部传入
select * from student where name='abc' and pwd='123456'
-- 如果name='' or '1=1', pwd= '' or '1=1'
select * from student where name='' or '1=1' and pwd= '' or '1=1'
-- 上述sql会被转化为,由此拿到数据库数据,解决方法输入校验
select * from student
```

可使用preparedStatement来防止sql处理，他会将传入数据作为字符串处理

> preparedStatement 使用 ?做占位符，再后续填充

```sql
// 链接数据库,获取数据库对象
Connection connection = null;
PreparedStatement statement = null;
ResultSet resultset = null;
try {
    connection = DriverManager.getConnection(url, user, pwd);

    // 获取sql执行对象
    String sql = "select * from student where name=? and pwdd=?";
    // 预处理
    statement = connection.prepareStatement(sql);
    st.setString(1,"mrbai");
    st.setString(2,123456);
    
		
    // 获取结果集对象, 如非查询 返回结果大于0即成功
    resultset = st.executeQuery();

    while (resultset.next()) {
        System.out.println(resultset.getObject("id"));
        System.out.println(resultset.getObject("name"));
        System.out.println(resultset.getObject("age"));
    }

    // 关闭相关对象
    resultset.close();
    statement.close();
    connection.close();

} catch (Exception error) {
    System.out.println("报错了");
}
```