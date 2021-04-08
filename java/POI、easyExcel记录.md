#  Excel记录

> 三方库: `POI` `easyExcel`, excel03版最多65536行。

maven导入

```xml
<!--  阿里巴巴excel操作工具-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>2.2.7</version>
</dependency>

<!--  时间工具类 -->
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.10.10</version>
</dependency>

<!--  测试框架-->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
</dependency>
```

![](http://blog-imgs.nos-eastchina1.126.net/1617694990.png)

## POI

> 较为原生，底层，有内存方面问题
>
> 一次性读写所有记录

### 写

> 默认XSSF速度较慢，通常可使用SXSSF来进行效率提升
>
> SXSSF会生成缓存来操作，会生成临时文件需要清除`dispose()`

```java
// 03和07版 主要是对象不同 一个为 HSSF 一个为 XSSF
// 文件名对应 xls 和 xlsx
        // 1. 创建工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 2. 创建工作表
        HSSFSheet sheet = workbook.createSheet("默认表");
        // 3. 创建一个行 (0)
        Row row1 = sheet.createRow(0);
        // 4. 创建一个单元格
        Cell cell11 = row1.createCell(0);
        cell11.setCellValue("新增观众");

        Cell cell12 = row1.createCell(1);
        cell12.setCellValue("666");

        Row row2 = sheet.createRow(1);
        Cell cell21 = row2.createCell(0);
        cell21.setCellValue("统计时间");

        Cell cell22 = row2.createCell(1);
        String time = new DateTime().toString("yyy-MM-dd HH:mm:ss");
        cell22.setCellValue(time);

        FileOutputStream fileOutputStream = new FileOutputStream(Path + "test.xls");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        System.out.println("表生成完成");
```



### 读

## easyExcel

> 基于POI的重写，对内存有良好的使用
>
> 一行一行读取

 