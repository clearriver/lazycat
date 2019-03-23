/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.message.req;

/**
 * @author 胡裕
 * 地理位置消息
 * 
 */
public class LocationMessage extends BaseMessage {
    /**
     * 地理位置维度
     */
    private String LocationX;
    /**
     * 地理位置经度
     */
    private String LocationY;
    /**
     * 地图缩放大小
     */
    private String Scale;
    /**
     * 地理位置信息
     */
    private String Label;

    public String getScale() {
        return Scale;
    }

    public void setScale(String scale) {
        Scale = scale;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }
    /**
     * @return the locationX
     */
    public String getLocationX() {
        return LocationX;
    }

    /**
     * @param locationX the locationX to set
     */
    public void setLocationX(String locationX) {
        LocationX = locationX;
    }

    /**
     * @return the locationY
     */
    public String getLocationY() {
        return LocationY;
    }

    /**
     * @param locationY the locationY to set
     */
    public void setLocationY(String locationY) {
        LocationY = locationY;
    }

}
