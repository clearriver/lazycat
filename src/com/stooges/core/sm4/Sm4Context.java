/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 */
package com.stooges.core.sm4;

/**
 * @author 胡裕
 *
 * 2017年8月1日
 */
public class Sm4Context {
    /**
     * mode
     */
    public int mode;
    /**
     * sk
     */
    public long[] sk;
    /**
     * isPadding
     */
    public boolean isPadding;

    /**
     * 描述
     * 
     * @author Toddle Chen
     * @created Jul 27, 2017 11:33:45 AM
     */
    public Sm4Context() {
        this.mode = 1;
        this.isPadding = true;
        this.sk = new long[32];
    }
}
