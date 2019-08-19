package com.library.sboot.redis.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * ClassName: SentinelJedisUtil
 *
 * @Description: 高可用SentinelJedis工具类
 * @author feifei.liu
 * @date 2016年6月28日 下午5:17:23
 */
@Component
public class SentinelJedisUtil {
	private static Logger logger = LoggerFactory
			.getLogger(SentinelJedisUtil.class);
	private static SentinelJedisUtil sentinelJedisUtil;
	@Autowired
	private JedisSentinelPool jedisSentinelPool;

	@PostConstruct
	public void init() {
		sentinelJedisUtil = this;
		sentinelJedisUtil.jedisSentinelPool = this.jedisSentinelPool;
	}

	public static SentinelJedisUtil getInstance(){
		return sentinelJedisUtil;
	}

	/**
	 * @Description: 获取Jedis对象
	 * @return
	 * @return Jedis
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:54:50
	 */
	public Jedis getJedis() {
		return sentinelJedisUtil != null ? sentinelJedisUtil.jedisSentinelPool
				.getResource() : null;
	}

	/**
	 * @Description: 设置一个key的过期时间（单位：秒）
	 * @param key
	 *            REDIS键值
	 * @param seconds
	 *            多少秒后过期
	 * @return 1：设置了过期时间 0：没有设置过期时间/不能设置过期时间
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:18:15
	 */
	public static long expire(String key, int seconds) {
		if (key == null || key.equals("")) {
			return 0;
		}

		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.expire(key, seconds);
		} catch (Exception ex) {
			logger.error("EXPIRE error[key=" + key + " seconds=" + seconds
					+ "]" + ex.getMessage(), ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return 0;
	}

	/**
	 * @Description: 设置一个key在某个时间点过期
	 * @param key
	 *            REDIS键值
	 * @param unixTimestamp
	 *            unix时间戳，从1970-day01-day01 00:00:00开始到现在的秒数
	 * @return
	 * @return long 1：设置了过期时间 0：没有设置过期时间/不能设置过期时间
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:19:21
	 */
	public static long expireAt(String key, int unixTimestamp) {
		if (key == null || key.equals("")) {
			return 0;
		}

		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.expireAt(key, unixTimestamp);
		} catch (Exception ex) {
			logger.error("EXPIRE error[key=" + key + " unixTimestamp="
					+ unixTimestamp + "]" + ex.getMessage(), ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return 0;
	}

	/**
	 * DEL命令
	 * @Title:  delKey
	 * @Description: (移除给定的一个或多个key。)
	 * @param key 数组
	 * @return: 被移除key的数量。
	 */
	public static Long delKey(String... key){
		Long result = null;
		boolean broken = false;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			result = jedis.del(key);
		}catch(Exception e){
//			log.error("SET failed",e);
			broken = true;
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	/**
	 * DEL命令
	 * @Title:  delKey
	 * @Description: (移除给定的一个或多个key。)
	 * @param key 数组
	 * @return: 被移除key的数量。
	 */
	public static Long delKey(byte[]... key){
		Long result = null;
		boolean broken = false;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			result = jedis.del(key);
		}catch(Exception e){
//			log.error("SET failed",e);
			broken = true;
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * KEYS命令
	 * @Title:  keysKey
	 * @Description: (查找符合给定模式的key。
	 * KEYS *命中数据库中所有key。
	 * KEYS h?llo命中hello， hallo and hxllo等。
	 * KEYS h*llo命中hllo和heeeeello等。
	 * KEYS h[ae]llo命中hello和hallo，但不命中hillo。
	 * )
	 * @param key
	 * @return: Long
	 */
	public static Set<String> keysKey(String key){

		Set<String> results = null;
		boolean broken = false;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			results = jedis.keys(key);
		}catch(Exception e){
//			log.error("keys failed",e);
			broken = true;
		} finally {
			returnResource(jedis);
		}
		return results;
	}

	//检查给定key是否存在 true存在  false不存在
	public static boolean existsKey(String key){

		boolean results = false;
		boolean broken = false;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			results = jedis.exists(key);
		}catch(Exception e){
//			log.error("existsKey failed",e);
			broken = true;
		} finally {
			returnResource(jedis);
		}
		return results;
	}
	//检查给定key是否存在 true存在  false不存在
	public static boolean existsKey(byte[] key){

		boolean results = false;
		boolean broken = false;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			results = jedis.exists(key);
		}catch (JedisConnectionException e1){
			//log.error("cliString SET JedisConnectionException",e1);
			broken = true;
		}catch(Exception e){
//			log.error("existsKey failed",e);
			broken = true;
		} finally {
			returnResource(jedis);
		}
		return results;
	}

	/**
	 * @Title:  clearKeys
	 * @Description: (清除所有匹配的Key)
	 */
	public static Long clearKeys(String query){
		Set<String> strs = keysKey(query);
		System.out.println("总Key数："+strs.size());
		String[] str = (String[])strs.toArray(new String[strs.size()]);
		if(str.length<=0){
			return null;
		}
		Long keynum = delKey(str);
		System.out.println("删除的Key数:"+keynum);
		return keynum;
	}
	/**
	 * @Title:  clearKeys
	 * @Description: (清除所有匹配的Key)
	 */
	public static void clearKeys(String... strs){
		Long keynum = delKey(strs);
		System.out.println("删除的Key数:"+keynum);
	}
	/**
	 * @Title:  模糊匹配 ProjectId 并清除Key
	 * @Description: (清除所有匹配的Key)
	 */
	public static Long clearKeysByProjectId(String projectId){
		return clearKeys("*"+projectId+"*");
	}

	/**
	 * @Description: 截断一个List
	 * @param key
	 *            REDIS键值
	 * @param start
	 *            开始位置 从0开始
	 * @param end
	 *            结束位置
	 * @return
	 * @return String 状态码
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:20:52
	 */
	public static String trimList(String key, long start, long end) {
		if (key == null || key.equals("")) {
			return "-";
		}
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.ltrim(key, start, end);
		} catch (Exception ex) {
			logger.error("LTRIM 出错[key=" + key + " start=" + start + " end="
					+ end + "]" + ex.getMessage(), ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return "-";
	}

	/**
	 * @Description: 检查Set长度
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:21:28
	 */
	public static long countSet(String key) {
		if (key == null) {
			return 0;
		}
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.scard(key);
		} catch (Exception ex) {
			logger.error("countSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return 0;
	}

	/**
	 * @Description: 添加到Set中（同时设置过期时间）
	 * @param key
	 *            REDIS键值
	 * @param seconds
	 *            过期时间 单位s
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:23:57
	 */
	public static boolean addSet(String key, int seconds, String... value) {
		boolean result = addSet(key, value);
		if (result) {
			long i = expire(key, seconds);
			return i == 1;
		}
		return false;
	}

	/**
	 * @Description: 添加到Set中
	 * @param key
	 *            REDIS键值
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:24:47
	 */
	public static boolean addSet(String key, String... value) {
		if (key == null || value == null) {
			return false;
		}
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.sadd(key, value);
			return true;
		} catch (Exception ex) {
			logger.error("setList error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 判断值是否包含在set中
	 * @param key
	 *            REDIS键值
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:25:28
	 */
	public static boolean containsInSet(String key, String value) {
		if (key == null || value == null) {
			return false;
		}
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.sismember(key, value);
		} catch (Exception ex) {
			logger.error("setList error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 获取Set
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return Set<String>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:25:54
	 */
	public static Set<String> getSet(String key) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.smembers(key);
		} catch (Exception ex) {
			logger.error("getList error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 从set中删除value
	 * @param key
	 *            REDIS键值
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:26:12
	 */
	public static boolean removeSetValue(String key, String... value) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.srem(key, value);
			return true;
		} catch (Exception ex) {
			logger.error("getList error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: list中删除value 默认count 1
	 * @param key
	 *            REDIS键值
	 * @param values
	 * @return
	 * @return int
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:26:39
	 */
	public static int removeListValue(String key, List<String> values) {
		return removeListValue(key, 1, values);
	}

	/**
	 * @Description: 从list中删除value
	 * @param key
	 *            REDIS键值
	 * @param count
	 * @param values
	 * @return
	 * @return int
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:27:day01
	 */
	public static int removeListValue(String key, long count,
			List<String> values) {
		int result = 0;
		if (values != null && values.size() > 0) {
			for (String value : values) {
				if (removeListValue(key, count, value)) {
					result++;
				}
			}
		}
		return result;
	}

	/**
	 * @Description: 从list中删除value
	 * @param key
	 *            REDIS键值
	 * @param count
	 *            要删除个数
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:27:32
	 */
	public static boolean removeListValue(String key, long count, String value) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.lrem(key, count, value);
			return true;
		} catch (Exception ex) {
			logger.error("getList error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 截取List
	 * @param key
	 *            REDIS键值
	 * @param start
	 *            起始位置
	 * @param end
	 *            结束位置
	 * @return
	 * @return List<String>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:28:07
	 */
	public static List<String> rangeList(String key, long start, long end) {
		if (key == null || key.equals("")) {
			return null;
		}
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.lrange(key, start, end);
		} catch (Exception ex) {
			logger.error("rangeList 出错[key=" + key + " start=" + start
					+ " end=" + end + "]" + ex.getMessage(), ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 检查List长度
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:28:33
	 */
	public static long countList(String key) {
		if (key == null) {
			return 0;
		}
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.llen(key);
		} catch (Exception ex) {
			logger.error("countList error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return 0;
	}

	/**
	 * @Description: 添加到Map中
	 * @param key
	 *            REDIS键值
	 * @param map
	 *            Map对象
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午2:30:31
	 */
	public static boolean addHMap(String key, Map<String, String> map) {
		if (key == null || map == null) {
			return false;
		}
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.hmset(key, map);
			return true;
		} catch (Exception ex) {
			logger.error("setHMap error.", ex);
			returnBrokenResource(jedis);
		}
		finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 添加到Map中
	 * @param key
	 *            REDIS键值
	 * @param mapKey
	 *            Map对象 key值
	 * @param mapValue
	 *            Map对象 value值
	 *
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午3:03:50
	 */
	public static boolean addHMap(String key, String mapKey, String mapValue) {
		if (key == null || mapKey == null) {
			return false;
		}
		Map<String, String> map = new LinkedHashMap<>();
		map.put(mapKey, mapValue);
		return addHMap(key, map);
	}

	/**
	 * @Description: 添加到Map中
	 * @param key
	 *            REDIS键值
	 * @param map
	 *            Map对象
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午3:26:00
	 */
	public static boolean addHMap(byte[] key, Map<byte[], byte[]> map) {
		if (key == null || map == null) {
			return false;
		}
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.hmset(key, map);
			return true;
		} catch (Exception ex) {
			logger.error("setHMap error.", ex);
			returnBrokenResource(jedis);
		}
		finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 添加到Map中
	 * @param key
	 *            REDIS键值
	 * @param mapKey
	 *            Map对象 key值
	 * @param mapValue
	 *            Map对象 value值
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午3:31:41
	 */
	public static boolean addHMap(byte[] key, byte[] mapKey, byte[] mapValue) {
		if (key == null || mapKey == null) {
			return false;
		}
		Map<byte[], byte[]> map = new LinkedHashMap<>();
		map.put(mapKey, mapValue);
		return addHMap(key, map);
	}

	/**
	 * @Description: 获取Map对应的value值 返回字节数组
	 * @param key
	 *            REDIS键值
	 * @param field
	 * @return
	 * @return byte[]
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月30日 下午8:23:34
	 */
	public static byte[] getHMap(byte[] key, byte[] field) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.hget(key, field);

		} catch (Exception ex) {
			logger.error("getHMap error.", ex);
			returnBrokenResource(jedis);
		}
		finally {
			returnResource(jedis);
		}
		return null;
	}
	/**
	 * @Description: 获取Map对应的value值 返回字节数组
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return byte[]
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月30日 下午8:23:34
	 */
	public static Map<byte[], byte[]> getHMapAll(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.hgetAll(key);

		} catch (Exception ex) {
			logger.error("getHMap error.", ex);
			returnBrokenResource(jedis);
		}
		finally {
			returnResource(jedis);
		}
		return null;
	}
	/**
	 * @Description: 获取Map对应的value值 返回字节数组
	 * @param key
	 *            REDIS键值
	 * @param fields
	 * @return
	 * @return byte[]
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月30日 下午8:23:34
	 */
	public static Long delHMap(byte[] key,byte[]... fields) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.hdel(key, fields);

		} catch (Exception ex) {
			logger.error("getHMap error.", ex);
			returnBrokenResource(jedis);
		}
		finally {
			returnResource(jedis);
		}
		return null;
	}
	/**
	 * @Description: 获取Map对应的value值 返回集合
	 * @param key
	 *            REDIS键值
	 * @param fields
	 *            Map keys
	 * @return
	 * @return List<String>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午3:37:17
	 */
	public static List<String> getHMap(String key, String... fields) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.hmget(key, fields);
		} catch (Exception ex) {
			logger.error("getHMap error.", ex);
			returnBrokenResource(jedis);
		}
		finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 获取Map对应的value值 返回集合
	 * @param key
	 *            REDIS键值
	 * @param fields
	 *            Map keys
	 * @return
	 * @return List<byte[]>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午3:39:31
	 */
	public static List<byte[]> getHMap(byte[] key, byte[]... fields) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.hmget(key, fields);
		} catch (Exception ex) {
			logger.error("getHMap error.", ex);
			returnBrokenResource(jedis);
		}
		finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 根据Map key值删除Map中的数据
	 * @param key
	 *            REDIS键值
	 * @param fields
	 *            Map keys
	 * @return
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午3:39:59
	 */
	public static long delHMap(String key, String... fields) {
		Jedis jedis = null;
		long count = 0;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			count = jedis.hdel(key, fields);
		} catch (Exception ex) {
			logger.error("delHMap error.", ex);
			returnBrokenResource(jedis);
		}
		finally {
			returnResource(jedis);
		}
		return count;
	}

	/**
	 * @Description: 根据Map key值删除Map中的数据
	 * @param key
	 *            REDIS键值
	 * @param fields
	 *            Map keys
	 * @return
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午3:40:51
	 */
	public static long delHashMap(byte[] key, byte[]... fields) {
		Jedis jedis = null;
		long count = 0;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			count = jedis.hdel(key, fields);
		} catch (Exception ex) {
			logger.error("delHMap error.", ex);
			returnBrokenResource(jedis);
		}
		finally {
			returnResource(jedis);
		}
		return count;
	}

	/**
	 * @Description: 判断Map中是否存在该key值
	 * @param key
	 *            REDIS键值
	 * @param field
	 *            Map key
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午3:41:07
	 */
	public static boolean existsHMap(String key, String field) {
		Jedis jedis = null;
		boolean isExist = false;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			isExist = jedis.hexists(key, field);
		} catch (Exception ex) {
			logger.error("existsHMap error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return isExist;
	}

	/**
	 * @Description: 判断Map中是否存在该key值
	 * @param key
	 *            REDIS键值
	 * @param field
	 *            Map key
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午3:42:35
	 */
	public static boolean existsHMap(byte[] key, byte[] field) {
		Jedis jedis = null;
		boolean isExist = false;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			isExist = jedis.hexists(key, field);
		} catch (Exception ex) {
			logger.error("existsHMap error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return isExist;
	}

	/**
	 * @Description: 添加到List中（同时设置过期时间）
	 * @param key
	 *            REDIS键值
	 * @param seconds
	 *            过期时间 单位s
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:29:15
	 */
	public static boolean addList(String key, int seconds, String... value) {
		boolean result = addList(key, value);
		if (result) {
			long i = expire(key, seconds);
			return i == 1;
		}
		return false;
	}

	/**
	 * @Description: 添加到List
	 * @param key
	 *            REDIS键值
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:29:43
	 */
	public static boolean addList(String key, String... value) {
		if (key == null || value == null) {
			return false;
		}
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.lpush(key, value);
			return true;
		} catch (Exception ex) {
			logger.error("setList error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 添加到List(只新增)
	 * @param key
	 *            REDIS键值
	 * @param list
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:30:03
	 */
	public static boolean addList(String key, List<String> list) {
		if (key == null || list == null || list.size() == 0) {
			return false;
		}
		for (String value : list) {
			addList(key, value);
		}
		return true;
	}

	/**
	 * @Description: 获取List
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return List<String>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:30:31
	 */
	public static List<String> getList(String key) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.lrange(key, 0, -1);
		} catch (Exception ex) {
			logger.error("getList error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 获取子列表
	 * @param key
	 * @param start
	 * @param end
	 * @return List<String>
	 * @throws
	 * @author chong.cheng
	 * @date 2017年7月4日 上午11:00:09
	 */
	public static List<String> getSubList(String key,int start,int end){
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.lrange(key, start, end);
		} catch (Exception ex) {
			logger.error("getList error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 设置HashSet对象
	 * @param key
	 *            REDIS键值
	 * @param hsetKey
	 *
	 * @param hsetValue
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:30:50
	 */
	public static boolean setHSet(String key, String hsetKey, String hsetValue) {
		if (hsetValue == null)
			return false;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.hset(key, hsetKey, hsetValue);
			return true;
		} catch (Exception ex) {
			logger.error("setHSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * hset 命令
	 * @Description: (将哈希表 key 中的域 field 的值设为 value 。
	 * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
	 * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
	 * )
	 * @return:
	 * 如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。
	 * 如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。
	 */
	public static Long hset(byte[] key,byte[] field,byte[] value){
		Long result = null;
		if (value == null)
			return null;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			result = jedis.hset(key, field, value);
		} catch (Exception ex) {
			logger.error("setHSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * @Description: 获得HashSet对象
	 * @param key
	 *            REDIS键值
	 * @param hsetKey
	 *
	 * @return
	 * @return String
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:31:30
	 */
	public static String getHSet(String key, String hsetKey) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.hget(key, hsetKey);
		} catch (Exception ex) {
			logger.error("getHSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * hget 命令
	 * @Description: (返回哈希表 key 中给定域 field 的值。。)
	 * @return:
	 * 给定域的值。
	 * 当给定域不存在或是给定 key 不存在时，返回 nil 。
	 */
	public static byte[] hget(byte[] key,byte[] field){

		byte[] result = null;
		boolean broken = false;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			result = jedis.hget(key,field);
		}catch(Exception e){
			broken = true;
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * @Description: 删除HashSet对象
	 * @param key
	 *            域名
	 * @param hsetKey
	 *
	 * @return
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:31:55
	 */
	public static long delHSet(String key, String hsetKey) {
		Jedis jedis = null;
		long count = 0;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			count = jedis.hdel(key, hsetKey);
		} catch (Exception ex) {
			logger.error("delHSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return count;
	}

	/**
	 * @Description: 删除HashSet对象
	 * @param key
	 *            REDIS键值
	 * @param fields
	 *
	 * @return
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:32:58
	 */
	public static long delHSet(String key, String... fields) {
		Jedis jedis = null;
		long count = 0;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			count = jedis.hdel(key, fields);
		} catch (Exception ex) {
			logger.error("delHSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return count;
	}

	/**
	 * HDEL命令
	 * @Description: (删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。)
	 * @return: 被成功移除的域的数量，不包括被忽略的域
	 */
	public static Long hdel(byte[] key,byte[]... fields){

		Long result = null;
		boolean broken = false;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			result = jedis.hdel(key,fields);
		}catch(Exception e){
			broken = true;
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * @Description: 判断key是否存在
	 * @param key
	 *            REDIS键值
	 * @param field
	 *
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:33:23
	 */
	public static boolean existsHSet(String key, String field) {
		Jedis jedis = null;
		boolean isExist = false;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			isExist = jedis.hexists(key, field);
		} catch (Exception ex) {
			logger.error("existsHSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return isExist;
	}

	/**
	 * hexists 命令
	 * @Description: (查看哈希表 key 中，给定域 field 是否存在。)
	 * @return:
	 * 如果哈希表含有给定域，返回 1 。
	 * 如果哈希表不含有给定域，或 key 不存在，返回 0 。
	 */
	public static Boolean hexists(byte[] key,byte[] field){
		Jedis jedis = null;
		Boolean result = false;
		boolean broken = false;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			result = jedis.hexists(key,field);
		}catch(Exception e){
			broken = true;
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * @Description: 全局扫描hset
	 * @param key
	 *            REDIS键值
	 * @param match
	 *            field匹配模式
	 * @return
	 * @return List<Map.Entry<String,String>>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:33:43
	 */
	public static List<Map.Entry<String, String>> scanHSet(String key,
			String match) {
		Jedis jedis = null;
		try {
			int cursor = 0;
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			ScanParams scanParams = new ScanParams();
			scanParams.match(match);
			ScanResult<Map.Entry<String, String>> scanResult;
			List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>();
			do {
				scanResult = jedis.hscan(key, String.valueOf(cursor),
						scanParams);
				list.addAll(scanResult.getResult());
				cursor = Integer.parseInt(scanResult.getStringCursor());
			} while (cursor > 0);
			return list;
		} catch (Exception ex) {
			logger.error("scanHSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 全局扫描hset
	 * @param match
	 *            field匹配模式
	 * @return
	 * @return Set<String>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:34:12
	 */
	public static Set<String> scan(String match) {
		Jedis jedis = null;
		try {
			int cursor = 0;
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			ScanParams scanParams = new ScanParams();
			scanParams.match(match);
			ScanResult<String> scanResult;
			Set<String> retSet = new HashSet<String>();
			do {
				scanResult = jedis.scan(String.valueOf(cursor), scanParams);
				retSet.addAll(scanResult.getResult());
				cursor = Integer.parseInt(scanResult.getStringCursor());
			} while (cursor > 0);
			return retSet;
		} catch (Exception ex) {
			logger.error("scanHSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 返回 key 指定的哈希集中所有字段的value值
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return List<String>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:34:36
	 */
	public static List<String> hvals(String key) {
		Jedis jedis = null;
		List<String> retList = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			retList = jedis.hvals(key);
		} catch (Exception ex) {
			logger.error("hvals error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return retList;
	}

	/**
	 * @Description: 返回 key 指定的哈希集中所有字段的键值
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return Set<String>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:35:08
	 */
	public static Set<String> hkeys(String key) {
		Jedis jedis = null;
		Set<String> retList = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			retList = jedis.hkeys(key);
		} catch (Exception ex) {
			logger.error("hkeys error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return retList;
	}

	/**
	 * @Description: 返回 key 指定的哈希键值总数
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:35:46
	 */
	public static long lenHset(String key) {
		Jedis jedis = null;
		long retList = 0;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			retList = jedis.hlen(key);
		} catch (Exception ex) {
			logger.error("hkeys error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return retList;
	}

	/**
	 * @Description: 设置排序集合
	 * @param key
	 *            REDIS键值
	 * @param score
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:36:23
	 */
	public static boolean setSortedSet(String key, long score, String value) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.zadd(key, score, value);
			return true;
		} catch (Exception ex) {
			logger.error("setSortedSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 获得排序集合
	 * @param key
	 *            REDIS键值
	 * @param startScore
	 * @param endScore
	 * @param orderByDesc
	 * @return
	 * @return Set<String>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:38:27
	 */
	public static Set<String> getSoredSet(String key, long startScore,
			long endScore, boolean orderByDesc) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			if (orderByDesc) {
				return jedis.zrevrangeByScore(key, endScore, startScore);
			} else {
				return jedis.zrangeByScore(key, startScore, endScore);
			}
		} catch (Exception ex) {
			logger.error("getSoredSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 计算排序长度
	 * @param key
	 *            REDIS键值
	 * @param startScore
	 * @param endScore
	 * @return
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:38:43
	 */
	public static long countSoredSet(String key, long startScore, long endScore) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			Long count = jedis.zcount(key, startScore, endScore);
			return count == null ? 0L : count;
		} catch (Exception ex) {
			logger.error("countSoredSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return 0L;
	}

	/**
	 * @Description: 删除排序集合
	 * @param key
	 *            REDIS键值
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:39:03
	 */
	public static boolean delSortedSet(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			long count = jedis.zrem(key, value);
			return count > 0;
		} catch (Exception ex) {
			logger.error("delSortedSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 获得排序集合
	 * @param key
	 *            REDIS键值
	 * @param startRange
	 * @param endRange
	 * @param orderByDesc
	 * @return
	 * @return Set<String>
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:39:24
	 */
	public static Set<String> getSoredSetByRange(String key, int startRange,
			int endRange, boolean orderByDesc) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			if (orderByDesc) {
				return jedis.zrevrange(key, startRange, endRange);
			} else {
				return jedis.zrange(key, startRange, endRange);
			}
		} catch (Exception ex) {
			logger.error("getSoredSetByRange error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 获得排序打分
	 * @param key
	 *            REDIS键值
	 * @param member
	 * @return
	 * @return Double
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:39:44
	 */
	public static Double getScore(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.zscore(key, member);
		} catch (Exception ex) {
			logger.error("getSoredSet error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 添加数据并制定过期时间
	 * @param key
	 *            REDIS键值
	 * @param value
	 * @param second
	 *            过期时间，单位：秒
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:40:16
	 */
	public static boolean setex(String key, String value, int second) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.setex(key, second, value);
			return true;
		} catch (Exception ex) {
			logger.error("set error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 添加数据并制定过期时间
	 * @param key
	 *            REDIS键值
	 * @param value
	 * @param second
	 *            过期时间，单位：秒
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:40:16
	 */
	public static boolean setex(byte[] key, byte[] value, int second) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.setex(key, second, value);
			return true;
		} catch (Exception ex) {
			logger.error("set error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 添加数据
	 * @param key
	 *            REDIS键值
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:42:08
	 */
	public static boolean set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.set(key, value);
			return true;
		} catch (Exception ex) {
			logger.error("set error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 添加数据
	 * @param key
	 *            REDIS键值
	 * @param value
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午4:12:30
	 */
	public static boolean set(byte[] key, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.set(key, value);
			return true;
		} catch (Exception ex) {
			logger.error("set error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	/**
	 * @Description: 获取数据，无数据时返回默认值
	 * @param key
	 *            REDIS键值
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @return String
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:42:24
	 */
	public static String get(String key, String defaultValue) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.get(key) == null ? defaultValue : jedis.get(key);
		} catch (Exception ex) {
			logger.error("get error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return defaultValue;
	}

	/**
	 * @Description: 获取数据
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return String
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:43:20
	 */
	public static String get(String key) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.get(key);
		} catch (Exception ex) {
			logger.error("get error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 获取数据
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return byte[]
	 * @throws
	 * @author feifei.liu
	 * @date 2016年7月1日 下午4:16:23
	 */
	public static byte[] get(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.get(key);
		} catch (Exception ex) {
			logger.error("get error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * @Description: 删除数据
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return boolean
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:43:37
	 */
	public static boolean del(String key) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.del(key);
			return true;
		} catch (Exception ex) {
			logger.error("del error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}
	/**
	 * @Description:删除数据
	 * @param
	 * @return
	 * @throws
	 * @author changmeng.wang
	 * @date 2016/8/27 17:27
	 */
	public static boolean delByte(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			jedis.del(key);
			return true;
		} catch (Exception ex) {
			logger.error("del error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return false;
	}
	/**
	 * @Description: 数字递增存储键值键
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:47:52
	 */
	public static long incr(String key) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.incr(key);
		} catch (Exception ex) {
			logger.error("incr error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return 0;
	}

	/**
	 * @Description: 数字递减存储键值
	 * @param key
	 *            REDIS键值
	 * @return
	 * @return long
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:48:34
	 */
	public static long decr(String key) {
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			return jedis.decr(key);
		} catch (Exception ex) {
			logger.error("incr error.", ex);
			returnBrokenResource(jedis);
		} finally {
			returnResource(jedis);
		}
		return 0;
	}

	/**
	 * @Description: returnBrokenResource
	 * @param jedis
	 * @return void
	 * @throws
	 * @author feifei.liu
	 * @date 2016年6月28日 下午5:46:05
	 */
	@SuppressWarnings("deprecation")
	public static void returnBrokenResource(Jedis jedis) {
		try {
			if (jedis != null)
			sentinelJedisUtil.jedisSentinelPool.returnBrokenResource(jedis);
		} catch (Exception e) {
			//logger.error("returnBrokenResource error.", e);
		}
	}

	/**
	 * @Description: 使用于finally方法
	 * @param jedis
	 * @return void
	 * @throws
	 * @author lizs
	 * @date 2016年8月9日 下午8:07:16
	 */
	public static void returnResource(Jedis jedis) {
		try {
			if (jedis != null)
			sentinelJedisUtil.jedisSentinelPool.returnResource(jedis);
		} catch (Exception e) {
			//logger.error("returnBrokenResource error.", e);
		}
	}

	public static void pipeline(String... keysvalues){
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			Pipeline pl = jedis.pipelined();
			for(int i =0; i<keysvalues.length; i++){
				pl.mset(keysvalues[i]);
			}
			pl.sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(jedis!=null){
				jedis.disconnect();
			}
		}
	}

	/**
	 * SETNX命令
	 * @Title:  setNxStr
	 * @Description: (将key的值设为value，当且仅当key不存在。若给定的key已经存在，则SETNX不做任何动作。)
	 * @author: ziyu.zhang
	 * @date:   2015年8月18日 下午4:29:36
	 * @update: 2015年8月18日 下午4:29:36
	 * @param key
	 * @param value
	 * @return: 设置成功，返回1。  设置失败，返回0。
	 * @throws:
	 * Why & What is modified: <修改原因描述>
	 */
	public static Long setNxStr(String key,String value){

		Long result = null;
		boolean broken = false;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			result = jedis.setnx(key,value);
		}catch(Exception e){
//			log.error("cliString SETNX failed",e);
			broken = true;
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * MSET命令
	 * @Title:  msetStr
	 * @Description: (同时设置一个或多个key-value对。当发现同名的key存在时，MSET会用新值覆盖旧值，如果你不希望覆盖同名key，请使用MSETNX命令。
	 * MSET是一个原子性(atomic)操作，所有给定key都在同一时间内被设置)
	 * @author: ziyu.zhang
	 * @date:   2015年8月24日 下午3:15:59
	 * @update: 2015年8月24日 下午3:15:59
	 * @param keysvalues key value 数组
	 * @return
	 * @return: 总是返回OK(因为MSET不可能失败)
	 * @throws:
	 * Why & What is modified: <修改原因描述>
	 */
	public static String mset(String... keysvalues){

		String result = null;
		boolean broken = false;
		Jedis jedis = null;
		try {
			jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
			result = jedis.mset(keysvalues);
		}catch(Exception e){
//			log.error("cliString MSET failed",e);
			broken = true;
		} finally {
			returnResource(jedis);
		}
		return result;
	}

}
