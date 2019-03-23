/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

/**
 * 描述 Redis数据业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-08-27 09:59:41
 */
public interface Function<E, T> {
    public T callback(E e);
}
