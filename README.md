# UDPBroadcastHelper
UDP广播  
使用如下：  

1.创建UDPBroadcastHelper对象
```
UDPBroadcastHelper mHelper = new UDPBroadcastHelper(context);

```
2.接收
```
 //参数：端口号，发送处理handler
 mHelper.receive(9988, handle);
 ```
3.发送
```
//参数：端口号，发送的消息，发送处理handler
 mHelper.send(port,msg,handler);
 ```
4.关闭资源
```
 if (mHelper != null) {
            mHelper.StopListen();
        }
···        
        
