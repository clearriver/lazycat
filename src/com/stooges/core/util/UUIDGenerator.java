/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.util;

import java.net.InetAddress;

/**
 * 描述 UUID生成器
 * @author 胡裕
 * @version 1.0
 * @created 2016年2月21日 下午5:16:47
 */
public class UUIDGenerator {
    /**
     * IP
     */
    private static final int IP;
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月6日 上午8:50:47
     * @param bytes
     * @return
     */
    public static int iptoInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
        }
        return result;
    }

    static {
        int ipadd;
        try {
            ipadd = iptoInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
    }
    /**
     * counter
     */
    private static short counter = (short) 0;
    /**
     * JVM
     */
    private static final int JVM = (int) (System.currentTimeMillis() >>> 8);
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月6日 上午8:51:05
     */
    public UUIDGenerator() {
    }

    /**
     * Unique across JVMs on this machine (unless they load this class in the
     * same quater second - very unlikely)
     */
    protected static int getJVM() {
        return JVM;
    }

    /**
     * Unique in a millisecond for this JVM instance (unless there are >
     * Short.MAX_VALUE instances created in a millisecond)
     */
    protected static short getCount() {
        synchronized (UUIDGenerator.class) {
            if (counter < 0)
                counter = 0;
            return counter++;
        }
    }

    /**
     * Unique in a local network
     */
    protected static int getIP() {
        return IP;
    }

    /**
     * Unique down to millisecond
     */
    protected static short getHiTime() {
        return (short) (System.currentTimeMillis() >>> 32);
    }
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月6日 上午8:51:15
     * @return
     */
    protected static int getLoTime() {
        return (int) System.currentTimeMillis();
    }
    /**
     * sep
     */
    private final static String SEP = "";
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月6日 上午8:51:24
     * @param intval
     * @return
     */
    protected static String format(int intval) {
        String formatted = Integer.toHexString(intval);
        StringBuffer buf = new StringBuffer("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月6日 上午8:51:28
     * @param shortval
     * @return
     */
    protected static String format(short shortval) {
        String formatted = Integer.toHexString(shortval);
        StringBuffer buf = new StringBuffer("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月6日 上午8:51:32
     * @return
     */
    public static String getUUID() {
        return new StringBuffer(36).append(format(getIP())).append(SEP)
                .append(format(getJVM())).append(SEP)
                .append(format(getHiTime())).append(SEP)
                .append(format(getLoTime())).append(SEP)
                .append(format(getCount())).toString();
    }
}
