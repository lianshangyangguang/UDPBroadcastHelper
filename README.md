# UDPBroadcastHelper
[![](https://jitpack.io/v/lianshangyangguang/UDPBroadcastHelper.svg)](https://jitpack.io/#lianshangyangguang/UDPBroadcastHelper)  
UDP广播  
使用如下：  

1.创建UDPBroadcastHelper对象
```
UDPBroadcastHelper mHelper = new UDPBroadcastHelper(context);

```
2.接收
```
 //参数：端口号，接收处理回调OnReceive
 mHelper.receive(9988, onReceive);
 ```
3.发送
```
//参数：端口号，发送的消息，发送处理回调OnSend
 mHelper.send(port,msg,onSend);
 ```
4.关闭资源
```
 if (mHelper != null) {
            mHelper.StopListen();
        }
```         
