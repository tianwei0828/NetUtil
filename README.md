# NetUtil
### 参考谷歌官方文档，Android 监听网络变化、判断网络连接类型、网络制式等工具类
#### 使用示例：
##### 1、判断网络是否连接
```java
public void isNetConnected(View view) {
     boolean netConnected = NetUtil.isNetConnected(this);
}
```

##### 2、判断是否移动网络连接
```java
public void isMobileConnected(View view) {
     boolean mobileConnected = NetUtil.isMobileConnected(this);
}
```

##### 3、判断是否移动2G网络连接
```java
public void is2GConnected(View view) {
     boolean mobileConnected = NetUtil.is2GConnected(this);
}
```

##### 4、判断是否移动3G网络连接
```java
public void is3GConnected(View view) {
     boolean mobileConnected = NetUtil.is3GConnected(this);
}
```

##### 5、判断是否移动4G网络连接
```java
public void is4GConnected(View view) {
     boolean mobileConnected = NetUtil.is4GConnected(this);
}
```

##### 6、获取移动网络运营商名称
```java
public void getNetworkOperatorName(View view) {
     String getNetworkOperatorName = NetUtil.getNetworkOperatorName(this);
}
```

##### 7、获取移动终端类型
```java
public void getPhoneType(View view) {
     String getNetworkOperatorName = NetUtil.getPhoneType(this);
}
```

##### 8、判断是否Wifi连接
```java
public void isWifiConnected(View view) {
     String getNetworkOperatorName = NetUtil.isWifiConnected(this);
}
```

##### 9、注册网络变化监听
```java
public void registerNetConnChangedReceiver(View view) {
      NetUtil.registerNetConnChangedReceiver(this);
}
```

##### 10、移除注册网络变化监听
```java
public void unregisterNetConnChangedReceiver(View view) {
      NetUtil.unregisterNetConnChangedReceiver(this);
}
```

##### 11、添加网络变化监听
```java
public void addNetConnChangedListener(View view) {
      NetUtil.addNetConnChangedListener((connectStatus) -> Log.e("##", "connectStatus: " + connectStatus));
}
```

##### 12、移除网络变化监听
```java
public void removeNetConnChangedListener(View view) {
      NetUtil.removeNetConnChangedListener(listener));
}
```


