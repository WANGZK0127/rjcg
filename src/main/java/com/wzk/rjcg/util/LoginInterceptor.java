package com.wzk.rjcg.util;

import com.wzk.rjcg.dto.UserDTO;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 2024/3/25
 * 登录拦截器
 * @author wzk
 * @version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			//拦截
			return false;
		}
		return true;
	}

}
