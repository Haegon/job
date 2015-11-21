package com.toast.android;

/**
 * Created by Gohn on 2015. 11. 1..
 */
public class NativeString {
    static {
        System.loadLibrary("quiz");
    }

    public native int find(String arg1, String arg2);
    public native int getBuffer(String arg1);
    public native int getString(String arg1);
}