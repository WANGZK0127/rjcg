package com.wzk.rjcg.util;

/**
 * 2024/12/12
 *
 * @author wzk
 * @version 1.0
 */
public class RedisConstants {
	public static final String LOGIN_USER_KEY = "login:token:";
	
	public static final long LOGIN_USER_TTL = 3600L;
	public static final String BLOG_LIKED_KEY = "blog:liked:";
	public static final String FEED_KEY = "feed:";
	public static final String FOLLOW_COUNT_KEY = "follow:count:";
	
}
