package com.qkwl.common.redis;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class MemCache {
	
	/**
     * Jedis连接池
     */
    protected JedisPool jedisPool;
    
    /**
     * 设置连接池
     */
    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    /**
     * 新增不过期的数据
     */
    public boolean setNoExpire(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
        	String result = jedis.set(key, value);
        	if (result.equals("OK")) {
        		return true;
        	}
            return false;
        } catch (Exception e) {
            return false;
		}
    }

    /**
     * 新增不过期的数据
     */
    public boolean setNoExpire(int db, String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
        	jedis.select(db);
            String result = jedis.set(key, value);
        	if (result.equals("OK")) {
        		return true;
        	}
            return false;
        } catch (Exception e) {
        	return false;
		}
    }
    
    /**
     * 设置带过期时间的数据
     */
    public boolean set(String key, String value, int expireSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.setex(key, expireSeconds, value);
            if (result.equals("OK")) {
        		return true;
        	}
            return false;
        } catch (Exception e) {
        	return false;
		}
    }
    
    /**
     * 设置带过期时间的数据
     */
    public boolean set(int db, String key, String value, int expireSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
        	jedis.select(db);
            String result = jedis.setex(key, expireSeconds, value);
            if (result.equals("OK")) {
        		return true;
        	}
            return false;
        } catch (Exception e) {
        	return false;
		}
    }
    
    
    /**
     * 新增不过期的数据(带数据压缩)
     */
    public boolean setNoExpireZip(String key, String value) {
    	//数据压缩
    	value = RedisDataUtils.serializationObject(value);
    	return setNoExpire(key, value);
    }
    
    /**
     * 新增不过期的数据(带数据压缩)
     */
    public boolean setNoExpireZip(int db, String key, String value) {
    	//数据压缩
    	value = RedisDataUtils.serializationObject(value);
    	return setNoExpire(db, key, value);
    }
    
    /**
     * 设置带过期时间的数据(带数据压缩)
     */
    public boolean setZip(String key, String value, int expireSeconds) {
    	//数据压缩
    	value = RedisDataUtils.serializationObject(value);
    	return set(key, value, expireSeconds);
    }
    
    /**
     * 设置带过期时间的数据(带数据压缩)
     */
    public boolean setZip(int db, String key, String value, int expireSeconds) {
    	//数据压缩
    	value = RedisDataUtils.serializationObject(value);
    	return set(db, key, value, expireSeconds);
    }

    /**
     * 读取
     */
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
        	return null;
		}
    }

    /**
     * 读取
     */
    public String get(int db, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
        	jedis.select(db);
            return jedis.get(key);
        } catch (Exception e) {
        	return null;
		}
    }

    /**
     * 拼接数据
     */
    public Long append(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.append(key, value);
        } catch (Exception e) {
        	return null;
		}
    }

    /**
     * 拼接数据
     */
    public Long append(int db, String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
        	jedis.select(db);
            return jedis.append(key, value);
        } catch (Exception e) {
        	return null;
		}
    }
    
    /**
     * 读取(已解压为对象)
     */
    public RedisObject getZip(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
        	String json = jedis.get(key);
        	if(!StringUtils.isEmpty(json)){
        		return RedisDataUtils.deserializationObject(json, RedisObject.class);
        	}
            return null;
        } catch (Exception e) {
        	return null;
		}
    }

    /**
     * 读取(已解压为对象)
     */
    public RedisObject getZip(int db, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
        	jedis.select(db);
        	String json = jedis.get(key);
        	if(!StringUtils.isEmpty(json)){
        		return RedisDataUtils.deserializationObject(json, RedisObject.class);
        	}
            return null;
        } catch (Exception e) {
        	return null;
		}
    }

    
    /**
     * 删除
     */
    public Long delete(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(keys);
        } catch (Exception e) {
        	return 0L;
		}
    }
    
    /**
     * 删除
     */
    public Long delete(int db, String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
        	jedis.select(db);
            return jedis.del(keys);
        } catch (Exception e) {
        	return 0L;
		}
    }
    
    /**
     * 设置到期时间
     */
    public Long expire(String key, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, seconds);
        } catch (Exception e) {
        	return 0L;
		}
    }
    
    /**
     * 设置到期时间
     */
    public Long expire(int db, String key, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
        	jedis.select(db);
            return jedis.expire(key, seconds);
        } catch (Exception e) {
        	return 0L;
		}
    }
    
    /**
     * 获取(删除用)
     */
    public Set<String> keys(String keys) {
        try (Jedis jedis = jedisPool.getResource()) {
        	return jedis.keys(keys);
        } catch (Exception e) {
        	return null;
		}
    }
    
    /**
     * 获取(删除用)
     */
    public Set<String> keys(int db, String keys) {
        try (Jedis jedis = jedisPool.getResource()) {
        	jedis.select(db);
        	return jedis.keys(keys);
        } catch (Exception e) {
        	return null;
		}
    }
    
    /**
     * 按字符匹配删除
     */
	public void removeByPattern(String key){
		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> sets = jedis.keys(key + "*");
			for (String k : sets) {
				delete(k);
			}
	    }
	}
    
    /**
     * 按字符匹配删除
     */
	public void removeByPattern(int db, String key){
		try (Jedis jedis = jedisPool.getResource()) {
        	jedis.select(db);
			Set<String> sets = jedis.keys(key + "*");
			for (String k : sets) {
				delete(k);
			}
	    }
	}

    /**
     * 设置带过期时间的数据
     */
    public boolean zadd(int db, String key, Double score, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            long result = jedis.zadd(key, score, member);
            if (result > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 给指定成员修改分数
     * @param db
     * @param key
     * @param score
     * @param member
     * @return
     */
    public boolean zincrby(int db, String key, Double score, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            double result = jedis.zincrby(key, score, member);
            if (result > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除给指定成员分数
     */
    public boolean zrem(int db, String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            Long result = jedis.zrem(key, member);
            if (result > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * 查询分数
     * @param db
     * @param key
     * @param member
     * @return
     */
    public double zscore(int db, String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            Double result = jedis.zscore(key, member);
            if (result == null) {
                return 0d;
            }
            return result;
        } catch (Exception e) {
            return 0d;
        }
    }

    /**
     * 查询排名
     * @param db
     * @param key
     * @param member
     * @return
     */
    public Long zrevrank(int db, String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            Long result = jedis.zrevrank(key, member);
            if (result == null) {
                return -1L;
            }
            return result;
        } catch (Exception e) {
            return -1L;
        }
    }

    /**
     * 查询排名（从大到小）
     * @param db
     * @param key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @return
     */
    public Set<String> zrevrangeByScore(int db, String key, Double max,Double min,Integer offset, Integer count) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            Set<String> result = jedis.zrevrangeByScore(key, max, min, offset, count);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /*********操作集合（set）************/
    /**
     * 向集合添加一个成员
     * @param db
     * @param key
     * @param value
     * @return
     */
    public boolean sadd(int db, String key, String value) {
        //	SADD key member1 [member2]
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            long result = jedis.sadd(key, value);
            if (result > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断 member 元素是否是集合 key 的成员
     * @param db
     * @param key
     * @param value
     * @return
     */
    public boolean sismember(int db, String key, String value) {
        //	SISMEMBER key member
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            boolean result = jedis.sismember(key, value);
            return result;
        } catch (Exception e) {
            return false;
        }
    }
}
