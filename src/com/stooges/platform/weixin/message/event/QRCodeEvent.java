/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.message.event;

/**
 * @author 胡裕
 * 扫描带参数二维码事件
 * 
 */
public class QRCodeEvent extends BaseEvent {
    /**
     * 事件KEY值
     */
    private String EventKey;
    // 
    /**
     * 用于换取二维码图片
     */
    private String Ticket;

    public String getEventKey() {
        return EventKey;
    }

    public void setEventKey(String eventKey) {
        EventKey = eventKey;
    }

    public String getTicket() {
        return Ticket;
    }

    public void setTicket(String ticket) {
        Ticket = ticket;
    }
}
