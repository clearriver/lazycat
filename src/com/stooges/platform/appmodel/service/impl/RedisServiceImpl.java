/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Tuple;

import com.alibaba.fastjson.JSON;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.platform.appmodel.service.Function;
import com.stooges.platform.appmodel.service.RedisService;

/**
 * 描述 Redis数据业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-08-27 09:59:41
 */
@Service("redisService")
public class RedisServiceImpl implements RedisService {

    /**
     * 
     */
    @Autowired(required = false)
    private ShardedJedisPool shardedJedisPool;
    
    /**
     * 
     * @param fun
     * @return
     */
    private <T> T execute(Function<ShardedJedis, T> fun) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedJedisPool.getResource();
            return fun.callback(shardedJedis);
        } finally {
            if (null != shardedJedis) {
                // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
                shardedJedis.close();
            }
        }
    }
    

    /**
     * 设置值
     * 
     * @param key
     * @param value
     * @return
     */
    public String set(final String key, final String value) {
        return this.execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    /**
     * 获取值
     * 
     * @param key
     * @return
     */
    public String get(final String key) {
        return this.execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis e) {
                return e.get(key);
            }
        });
    }

    /**
     * 删除
     * 
     * @param key
     * @return
     */
    public Long del(final String key) {
        return this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis e) {
                return e.del(key);
            }
        });
    }
    
    /**
     * 根据KEY删除缓存
     * @param keys
     */
    public void deleteByKeys(String[] keys){
        for(String key:keys){
            this.del(key);
        }
    }

    /**
     * 设置生存时间
     * 
     * @param key
     * @return
     */
    public Long expire(final String key, final Integer seconds) {
        return this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis e) {
                return e.expire(key, seconds);
            }
        });
    }

    /**
     * 设置值以及生存时间，单位为秒
     * 
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public String set(final String key, final String value, final Integer seconds) {
        return this.execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis jedis) {
                String result = jedis.set(key, value);
                jedis.expire(key, seconds);
                return result;
            }
        });
    }
    
    /**
     * 根据模糊匹配出KEY的集合
     * @param keyName
     * @return
     */
    public Set<String> getKeySetByKeyPattern(String keyName){
        final String keyPattern = "*"+keyName+"*";
        final Set<String> result = new HashSet<String>();
        this.execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis jedis) {
                Collection<Jedis> jedisList = jedis.getAllShards();
                for(Jedis j:jedisList){
                    Set<String> keySet = j.keys(keyPattern);
                    result.addAll(keySet);
                }
                return null;
            }
        });
        return result;
    }
    
    /**
     * 获取Key列表
     * @param keyPattern
     * @return
     */
    public List<Map<String,Object>> findKeyList(final String keyPattern){
        final List<Map<String,Object>> list  = new ArrayList<Map<String,Object>>();
        this.execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis jedis) {
                Collection<Jedis> jedisList = jedis.getAllShards();
                for(Jedis j:jedisList){
                    Set<String> keySet = j.keys(keyPattern);
                    for(String key:keySet){
                        String keyType = j.type(key);
                        Long timeLimit = j.ttl(key);
                        Map<String,Object> keyInfo = new HashMap<String,Object>();
                        keyInfo.put("KEY_NAME", key);
                        keyInfo.put("KEY_TYPE", keyType);
                        if(timeLimit!=-1){
                            Date nowDate = new Date();
                            Date deadLineTime = PlatDateTimeUtil.getNextTime(nowDate,
                                    Calendar.SECOND,timeLimit.intValue());
                            keyInfo.put("KEY_TIMELIMIT",PlatDateTimeUtil.
                                    formatDate(deadLineTime, "yyyy-MM-dd HH:mm:ss"));
                        }else{
                            keyInfo.put("KEY_TIMELIMIT", timeLimit);
                        }
                        list.add(keyInfo);
                    }
                }
                return null;
            }
        });
        return list;
    }
    
    
    /**
     * 根据filter和配置信息获取KEY列表
     * @param filter
     * @param fieldInfo
     * @return
     */
    public List<Map<String,Object>> findKeyList(SqlFilter filter,Map<String,Object> fieldInfo){
        String keyName = filter.getRequest().getParameter("keyName");
        String keyPattern = "*";
        if(StringUtils.isNotEmpty(keyName)){
            keyPattern = "*"+keyName+"*";
        }
        return this.findKeyList(keyPattern);
    }
    
    /**
     * 设置KEY的失效时间
     * @param keyNames
     * @param timelimit
     */
    public void expireKeys(final String keyNames,final int timelimit){
        this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis e) {
                String[] keyNameArray = keyNames.split(",");
                for(String keyName:keyNameArray){
                    e.expire(keyName, timelimit);
                }
                return null;
            }
        });
    }
    
    /**
     * 设置KEY的具体失效截止时间
     * @param keyNames
     * @param timestamp 时间戳
     */
    public void expireAtTimeStamp(final String keyNames,final Long timestamp){
        this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis e) {
                String[] keyNameArray = keyNames.split(",");
                for(String keyName:keyNameArray){
                    e.expireAt(keyName, timestamp);
                }
                return null;
            }
        });
    }
    
    /**
     * 持久化KEY
     * @param keyNames
     */
    public void persist(final String keyNames[]){
        this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis e) {
                for(String keyName:keyNames){
                    e.persist(keyName);
                }
                return null;
            }
        });
    }
    
    /**
     * 获取KEY的基本信息
     * @param keyName
     * @return
     */
    public Map<String,Object> getKeyInfo(final String keyName){
        final Map<String,Object> keyInfo = new HashMap<String,Object>();
        keyInfo.put("KEY_NAME",keyName);
        this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis e) {
                keyInfo.put("KEY_TYPE",e.type(keyName));
                Long timeLimit = e.ttl(keyName);
                if(timeLimit!=-1){
                    Date nowDate = new Date();
                    Date deadLineTime = PlatDateTimeUtil.getNextTime(nowDate,
                            Calendar.SECOND,timeLimit.intValue());
                    keyInfo.put("KEY_TIMELIMIT",PlatDateTimeUtil.
                            formatDate(deadLineTime, "yyyy-MM-dd HH:mm:ss"));
                }else{
                    keyInfo.put("KEY_TIMELIMIT", "永久有效");
                }
                return null;
            }
        });
        return keyInfo;
    }
    
    /**
     * 根据keyName获取集合
     * @param keyName
     * @return
     */
    public Set<String> getSetValue(final String keyName){
        final Set<String> keyValueSet = new HashSet<String>();
        this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis e) {
                keyValueSet.addAll(e.smembers(keyName));
                return null;
            }
        });
        return keyValueSet;
    }
    
    /**
     * 获取zset的值
     * @param keyName
     * @return
     */
    public Set<Tuple> getZSetValue(final String keyName){
        final Set<Tuple> keyValueSet = new HashSet<Tuple>();
        this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis jedis) {
                keyValueSet.addAll(jedis.zrangeWithScores(keyName,0,-1));
                return null;
            }
        });
        return keyValueSet;
    }
    
    /**
     * 获取hash的值
     * @param keyName
     * @return
     */
    public Map<String,String> getHashValue(final String keyName){
        final Map<String,String> hashValue = new HashMap<String,String>();
        this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis jedis) {
                hashValue.putAll(jedis.hgetAll(keyName));
                return null;
            }
        });
        return hashValue;
    }
    
    /**
     * 获取list的值
     * @param keyName
     * @return
     */
    public List<String> getListValue(final String keyName){
        final List<String> list = new ArrayList<String>();
        this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis jedis) {
                list.addAll(jedis.lrange(keyName, 0, -1));
                return null;
            }
        });
        return list;
    }
    
    /**
     * 根据filter获取KEY的值
     * @param filter
     * @return
     */
    public List<Map> findValuesByFilter(SqlFilter filter){
        String keyName = filter.getRequest().getParameter("KEY_NAME");
        String keyType = filter.getRequest().getParameter("KEY_TYPE");
        List<Map> list = new ArrayList<Map>();
        if(keyType.equals("set")){
            Set<String> keySet = this.getSetValue(keyName);
            for(String key:keySet){
                Map<String,Object> value = new HashMap<String,Object>();
                value.put("VALUE_CONTENT", key);
                list.add(value);
            }
        }else if(keyType.equals("zset")){
            Set<Tuple> keyValueSet = this.getZSetValue(keyName);
            for(Tuple tuple:keyValueSet){
                Map<String,Object> value = new HashMap<String,Object>();
                value.put("VALUE_KEY", tuple.getScore());
                value.put("VALUE_CONTENT", tuple.getElement());
                list.add(value);
            }
        }else if(keyType.equals("hash")){
            Map<String,String> hashValue = this.getHashValue(keyName);
            Iterator it = hashValue.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,String> entry = (Map.Entry<String,String>) it.next();
                Map<String,Object> value = new HashMap<String,Object>();
                value.put("VALUE_KEY", entry.getKey());
                value.put("VALUE_CONTENT", entry.getValue());
                list.add(value);
            }
        }else if(keyType.equals("list")){
            List<String> listValue = this.getListValue(keyName);
            for(String key:listValue){
                Map<String,Object> value = new HashMap<String,Object>();
                value.put("VALUE_CONTENT", key);
                list.add(value);
            }
        }
        return list;
    }
    
    /**
     * 设置SET对象值
     * @param keyName
     * @param setValue
     * @return
     */
    public Long setSetValue(final String keyName,final Set<String> setValue){
        final List<String> setArray = new ArrayList<String>();
        for(String value:setValue){
            setArray.add(value);
        }
        return this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis jedis) {
                return jedis.sadd(keyName,setArray.toArray(new String[setArray.size()]));
            }
        });
    }
    
    /**
     * 设置zSet的值
     * @param keyName
     * @param zsetValue
     * @return
     */
    public Long setZSetValue(final String keyName,final Map<String, Double> zsetValue){
        return this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis jedis) {
                return jedis.zadd(keyName, zsetValue);
            }
        });
    }
    
    /**
     * 设置hash的值
     * @param keyName
     * @param hashValue
     * @return
     */
    public String setHashValue(final String keyName,final Map<String,String> hashValue){
        this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis jedis) {
                jedis.hmset(keyName, hashValue);
                return null;
            }
        });
        return null;
    }
    
    /**
     * 设置list的值
     * @param keyName
     * @param hashValue
     * @return
     */
    public Long setListValue(final String keyName,final List<String> values){
        return this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis jedis) {
                return jedis.lpush(keyName,values.toArray(new String[values.size()]));
            }
        });
    }
    
    /**
     * 更新非string类型的值
     * @param keyName
     * @param keyType
     * @param valueJson
     */
    public void updateNoStringValue(final String keyName,String keyType,String valueJson){
        List<Map> values = JSON.parseArray(valueJson,Map.class);
        if(keyType.equals("set")){
            final Set<String> members = new HashSet<String>();
            for(Map value:values){
                String VALUE_CONTENT = (String) value.get("VALUE_CONTENT");
                members.add(VALUE_CONTENT);
            }
            this.del(keyName);
            if(members.size()>0){
                this.setSetValue(keyName, members);
            }
        }else if(keyType.equals("zset")){
            final Map<String,Double> targetValue = new HashMap<String,Double>();
            for(Map value:values){
                String VALUE_KEY = (String) value.get("VALUE_KEY");
                String VALUE_CONTENT = (String) value.get("VALUE_CONTENT");
                targetValue.put(VALUE_CONTENT, Double.parseDouble(VALUE_KEY));
            }
            this.del(keyName);
            if(targetValue.size()>0){
                this.setZSetValue(keyName, targetValue);
            }
        }else if(keyType.equals("hash")){
            final Map<String,String> targetValue = new HashMap<String,String>();
            for(Map value:values){
                String VALUE_KEY = (String) value.get("VALUE_KEY");
                String VALUE_CONTENT = (String) value.get("VALUE_CONTENT");
                targetValue.put(VALUE_KEY,VALUE_CONTENT);
            }
            this.del(keyName);
            if(targetValue.size()>0){
                this.setHashValue(keyName, targetValue);
            }
        }else if(keyType.equals("list")){
            final List<String> targetValue = new ArrayList<String>();
            for(Map value:values){
                String VALUE_CONTENT = (String) value.get("VALUE_CONTENT");
                targetValue.add(VALUE_CONTENT);
            }
            this.del(keyName);
            if(targetValue.size()>0){
                this.setListValue(keyName, targetValue);
            }
        }
    }
    
    /**
     * 根据模糊匹配删除KEY
     * @param keyName
     */
    public void deleteByLikeKeyName(String keyName){
        Set<String> keySet = this.getKeySetByKeyPattern(keyName);
        for(String key:keySet){
            this.del(key);
        }
    }

}
