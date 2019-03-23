/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.util;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 胡裕
 * 公众号token对象
 * 
 */
public class PublicToken implements Serializable {
    /**
     * token的值
     */
    private String token;
    /**
     * 过期的时间对象
     */
    private Date timeoutDate;
    
    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }
    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }
    /**
     * @return the timeoutDate
     */
    public Date getTimeoutDate() {
        return timeoutDate;
    }
    /**
     * @param timeoutDate the timeoutDate to set
     */
    public void setTimeoutDate(Date timeoutDate) {
        this.timeoutDate = timeoutDate;
    }
}
