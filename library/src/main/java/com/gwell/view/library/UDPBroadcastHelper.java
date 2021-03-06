package com.gwell.view.library;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class UDPBroadcastHelper {
    public Boolean IsThreadDisable = false;
    private final static String UDPBroadcastHelper = "UDPBroadcastHelper";
    public int port;
    InetAddress mInetAddress;
    MulticastSocket receiveSocket = null;
    MulticastSocket sendSocket = null;
    public static final int RECEIVE_MSG_ERROR = 0x01;
    public static final int RECEIVE_MSG_SUCCESS = 0x02;
    public static final int SEND_MSG_SUCCESS = 0x03;
    public static final int SEND_MSG_ERROR = 0x04;
    private WeakReference<Context> mActivityReference;
    private WifiManager.MulticastLock lock;
    private boolean isStartSuccess = false;
    private OnReceive onReceive;
    private OnSend onSend;

    public UDPBroadcastHelper(Context mContext) {
        mActivityReference = new WeakReference<>(mContext);
        WifiManager manager = (WifiManager) mActivityReference.get().getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock(UDPBroadcastHelper);
    }

    ReceiveDatagramPacket receiveData;

    public interface OnReceive {
        void onReceive(int state, Bundle data);
    }

    public interface OnSend {
        void onSend(int state);
    }

    public void receive(int port, OnReceive onReceive) {
        this.port = port;
        this.onReceive = onReceive;
        //部分手机此处开始监听时会报异常，所以循环尝试开始监听
        new Thread() {
            @Override
            public void run() {
                isStartSuccess = false;
                while (!isStartSuccess) {
                    listen();
                    try {
                        sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECEIVE_MSG_SUCCESS:
                    if (onReceive != null) {
                        Bundle bundler = msg.getData();
                        onReceive.onReceive(RECEIVE_MSG_SUCCESS, bundler);
                    }
                    break;
                case RECEIVE_MSG_ERROR:
                    if (onReceive != null) {
                        onReceive.onReceive(RECEIVE_MSG_ERROR, null);
                    }
                    break;
                case SEND_MSG_SUCCESS:
                    onSend.onSend(SEND_MSG_SUCCESS);
                    break;
                case SEND_MSG_ERROR:
                    onSend.onSend(SEND_MSG_ERROR);
                    break;
            }
        }
    };

    public void listen() {
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
                if (data[0] == 1 && null != onReceive) {
                    ReceiveDatagramPacket receiveData = new ReceiveDatagramPacket(mInetAddress, data);
                    Bundle bundler = new Bundle();
                    bundler.putSerializable("receiveData", receiveData);
                    Message msg = handler.obtainMessage();
                    msg.what = RECEIVE_MSG_SUCCESS;
                    msg.setData(bundler);
                    handler.sendMessage(msg);
                    break;
                }
                MulticastUnLock();
            }
        } catch (SocketException e) {
            e.printStackTrace();
            //如果是此异常，isStartSuccess没有置为true，还会重新监听，所以这里不处理
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("zxy", "listen: " + e.toString());
            IsThreadDisable = true;
            if (null != onReceive) {
                handler.sendEmptyMessage(RECEIVE_MSG_ERROR);
            }
        } finally {
            MulticastUnLock();
            if (null != receiveSocket) {
                receiveSocket.close();
                receiveSocket = null;
            }
        }
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

    public void send(final int port, final String message, final OnSend onSend) {
        this.onSend = onSend;
        new Thread() {
            @Override
            public void run() {
                isStartSuccess = false;
                while (!isStartSuccess) {
                    try {
                        sendSocket = new MulticastSocket(port);
                        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName("255.255.255.255"), port);
                        MulticastLock();
                        sendSocket.setBroadcast(true);
                        sendSocket.setSoTimeout(120 * 1000);
                        isStartSuccess = true;
                        sendSocket.send(dp);
                        sendSocket.close();
                        if (onSend != null) {
                            handler.sendEmptyMessage(SEND_MSG_SUCCESS);
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("zxy", "sendError: " + e.toString());
                        if (onSend != null) {
                            handler.sendEmptyMessage(SEND_MSG_ERROR);
                        }
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