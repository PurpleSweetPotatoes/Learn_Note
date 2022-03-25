# Xcode相关

## 资源定位

### 描述证书 

```shell
~/Library/MobileDevice/Provisioning\ Profiles
```

### 代码片段

```shell
~/Library/Developer/Xcode/UserData/CodeSnippets
```

### 自定义模板

> 自定义宏配合模板使用，存放于用户数据`IDETemplateMacros`文件中

```shell
~/Library/Developer/Xcode/Templates
```

## 缓存定位

> 删除缓存可有效减少xcode体积

```shell
# 路径说明
# 1.xcode的项目索引 ~/Library/Developer/Xcode/DerivedData/
# 2.真机编译支持 ~/Library/Developer/Xcode/iOS DeviceSupport/
# 3.打包文件 ~/Library/Developer/Xcode/Archives/
# 4.无用 ~/Library/Developer/Xcode/Products/
# 5.模拟器数据 ~/Library/Developer/CoreSimulator/Devices/
# 6.playground缓存 ~/Library/Developer/XCPGDevices/
# 7.模拟器运行时缓存文件 ~/Library/Developer/CoreSimulator/Caches/

filePaths[0]=~/Library/Developer/Xcode/DerivedData/
filePaths[1]=~/Library/Developer/Xcode/iOS*DeviceSupport/
filePaths[2]=~/Library/Developer/Xcode/Archives/
filePaths[3]=~/Library/Developer/Xcode/Products/
filePaths[4]=~/Library/Developer/CoreSimulator/Devices/
filePaths[5]=~/Library/Developer/XCPGDevices/
filePaths[6]=~/Library/Developer/CoreSimulator/Caches/
for filePath in ${filePaths[@]}; do
    rm -rf "$filePath" -r
    mkdir "$filePath"
done
```

