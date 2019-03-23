/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.util;

import java.util.Arrays;

/**
 * 描述 封装对集合操作的工具类
 * @author 胡裕
 * @created 2017年2月2日 下午8:01:39
 */
public class PlatCollectionUtil {
    /**
     * 
     * 描述 对数组进行升序排
     * @author 胡裕
     * @created 2014年10月3日 下午12:49:25
     * @param arr
     * @return
     */
    public static int[] sortByAsc(int[] arr){
        Arrays.sort(arr);
        return arr;
    }
    /**
     * 
     * 描述 对数组进行降序排
     * @author 胡裕
     * @created 2014年10月3日 下午12:53:01
     * @param arr
     * @return
     */
    public static int[] sortByDesc(int[] arr){
        Arrays.sort(arr);
        int[] nums = new int[arr.length];
        int j =0;
        for(int i =arr.length-1;i>=0;i--){
            nums[j] = arr[i];
            j++;
        }
        return nums;
    }
}
