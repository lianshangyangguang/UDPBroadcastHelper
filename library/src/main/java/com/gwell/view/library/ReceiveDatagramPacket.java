package com.gwell.view.library;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/5/14.
 */
public class ReceiveDatagramPacket {
    public static final int RECEIVE_MSG_ERROR = 0x01;
    public static final int RECEIVE_MSG_SUCCESS = 0x02;
    private InetAddress mInetAddress;
    private byte[] data;
    private int state;

    public ReceiveDatagramPacket() {
    }

    public ReceiveDatagramPacket(InetAddress mInetAddress, byte[] data, int state) {
        this.mInetAddress = mInetAddress;
        this.data = data;
        this.state = state;
    }

    public ReceiveDatagramPacket(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public InetAddress getmInetAddress() {
        return mInetAddress;
    }

    public void setmInetAddress(InetAddress mInetAddress) {
        this.mInetAddress = mInetAddress;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "ReceiveDatagramPacket{" +
                "mInetAddress=" + mInetAddress +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
