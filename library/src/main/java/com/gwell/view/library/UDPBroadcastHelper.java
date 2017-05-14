package com.gwell.view.library;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class UDPBroadcastHelper {
    public Boolean IsThreadDisable = false;
    private final static String UDPHelper = "UDPHelper";
    public int port;
    InetAddress mInetAddress;
    MulticastSocket receiveSocket = null;
    MulticastSocket sendSocket = null;
      private WeakReference<Context> mActivityReference;
    private WifiManager.MulticastLock lock;
    private boolean isStartSuccess = false;
    private ReceiveDatagramPacket receiveData;

    public UDPBroadcastHelper(Context mContext) {
        mActivityReference = new WeakReference<>(mContext);
        WifiManager manager = (WifiManager) mActivityReference.get().getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock(UDPHelper);
    }

        public ReceiveDatagramPacket receive(int port) {
        this.port = port;
        //部分手机此处开始监听时会报异常，所以循环尝试开始监听
        new Thread() {
            @Override
            public void run() {
                isStartSuccess = false;
                while (!isStartSuccess) {
                    receiveData = listen();
                    if (receiveData.getState() == ReceiveDatagramPacket.RECEIVE_MSG_SUCCESS) {
                        break;
                    }
                    try {
                        sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        return receiveData;
    }

    private ReceiveDatagramPacket listen() {
        ReceiveDatagramPacket receiveData = null;
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        byte[] message = new byte[1024];
        try {
            // 建立Socket连接
            try {
                receiveSocket = new MulticastSocket(port);
            } catch (Exception e) {
                port = 57521;
                receiveSocket = new MulticastSocket(port);
                e.printStackTrace();
            }
            receiveSocket.setBroadcast(true);
            receiveSocket.setSoTimeout(120 * 1000);
            DatagramPacket datagramPacket = new DatagramPacket(message,
                    message.length);
            isStartSuccess = true;
            while (!IsThreadDisable) {
                // 准备接收数据
                MulticastLock();
                receiveSocket.receive(datagramPacket);
                mInetAddress = datagramPacket.getAddress();
                byte[] data = datagramPacket.getData();
                if (data[0] == 1) {
                    receiveData = new ReceiveDatagramPacket(mInetAddress, data, ReceiveDatagramPacket.RECEIVE_MSG_SUCCESS);
                    break;
                }
                MulticastUnLock();
            }
        } catch (SocketException e) {
            e.printStackTrace();
            //如果是此异常，isStartSuccess没有置为true，还会重新监听，所以这里不处理
        } catch (Exception e) {
            e.printStackTrace();
            IsThreadDisable = true;
            receiveData = new ReceiveDatagramPacket(ReceiveDatagramPacket.RECEIVE_MSG_ERROR);

        } finally {
            MulticastUnLock();
            if (null != receiveSocket) {
                receiveSocket.close();
                receiveSocket = null;
            }
        }
        return receiveData;
    }

    public void StopListen() {
        this.IsThreadDisable = true;
        this.isStartSuccess = true;
        if (null != receiveSocket) {
            receiveSocket.close();
            receiveSocket = null;
        }
    }

    private void MulticastLock() {
        if (this.lock != null) {
            try {
                this.lock.acquire();
            } catch (Exception e) {
                Log.e("SDK", "MulticastLock error");
            }
        }
    }

    private void MulticastUnLock() {
        if (this.lock != null) {
            try {
                this.lock.release();
            } catch (Exception e) {
                Log.e("SDK", "MulticastUnLock error");
            }
        }
    }

    public void send(final String host, final int port, final String message) {
        new Thread() {
            @Override
            public void run() {
                isStartSuccess = false;
                while (!isStartSuccess) {
                    try {
                        InetAddress group = InetAddress.getByName(host);
                        sendSocket = new MulticastSocket();
                        //加入多播组
                        sendSocket.joinGroup(group);
                        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), group, port);
                        MulticastLock();
                        sendSocket.setBroadcast(true);
                        sendSocket.setSoTimeout(120 * 1000);
                        isStartSuccess = true;
                        sendSocket.send(dp);
                        sendSocket.close();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        MulticastUnLock();
                        if (null != sendSocket) {
                            sendSocket.close();
                            sendSocket = null;
                        }
                    }
                    try {
                        sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }
}