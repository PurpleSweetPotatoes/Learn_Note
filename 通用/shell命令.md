# shell命令

## sed

### 文本替换

```shell
# Linux
# sed -i 's/源字符串/替换字符串' 文件
# str1、str2会被替换为随后//中间内容，使用/g表示全局替换
sed -i 's/str1//g;s/str2//g' ./*.html

# macos
# sed -i 替换字符串 's/源字符串' 文件
sed -i '' 's/user-select: none//g;s/-webkit-line-clamp: 5;//g'
```

