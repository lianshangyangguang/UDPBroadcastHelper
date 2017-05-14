package com.gwell.view.library;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/5/14.
 */
public class ReceiveDatagramPacket implements Serializable{

    private InetAddress mInetAddress;
    private byte[] data;


    public ReceiveDatagramPacket(InetAddress mInetAddress, byte[] data) {
        this.mInetAddress = mInetAddress;
        this.data = data;
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
