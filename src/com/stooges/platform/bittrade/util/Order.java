/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.util;

import java.io.Serializable;

/**
 * @author 胡裕
 *
 * 
 */
public class Order implements Serializable {
    /**
     * 买入价
     */
    private double price;
    /**
     * 数量
     */
    private double num;
    /**
     * 成本价
     */
    private double cost;
    /**
     * 止盈价
     */
    private double sell;
    /**
     * 花费钱
     */
    private double money;
    
    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }
    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }
    /**
     * @return the num
     */
    public double getNum() {
        return num;
    }
    /**
     * @param num the num to set
     */
    public void setNum(double num) {
        this.num = num;
    }
    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }
    /**
     * @param cost the cost to set
     */
    public void setCost(double cost) {
        this.cost = cost;
    }
    /**
     * @return the sell
     */
    public double getSell() {
        return sell;
    }
    /**
     * @param sell the sell to set
     */
    public void setSell(double sell) {
        this.sell = sell;
    }
    /**
     * @return the money
     */
    public double getMoney() {
        return money;
    }
    /**
     * @param money the money to set
     */
    public void setMoney(double money) {
        this.money = money;
    }
}
