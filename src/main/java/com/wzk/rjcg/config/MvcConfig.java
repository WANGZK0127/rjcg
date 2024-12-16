package com.wzk.rjcg.config;

import com.wzk.rjcg.util.LoginInterceptor;
import com.wzk.rjcg.util.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 2024/3/25
 *
 * @author wzk
 * @version 1.0
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor())
				.excludePathPatterns("/shop/**",
						"/voucher/**",
						"/shopType/**",
						"/upload/**",
						"/blog/hot",
						"/user/login").order(1);
		registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).order(0);
	}
}
