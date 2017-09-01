package com.aidijing.config;

import com.aidijing.BodyReaderWrapper;
import com.aidijing.interceptor.InjectionAttackInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author : 披荆斩棘
 * @date : 2017/9/1
 */
@Configuration
public class SpringConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors ( InterceptorRegistry registry ) {
		registry.addInterceptor( new InjectionAttackInterceptor() ).addPathPatterns( "/**" );
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean () {
		// 过滤器注册
		FilterRegistrationBean  registrationBean = new FilterRegistrationBean();
		CharacterEncodingFilter encodingFilter   = new CharacterEncodingFilter();
		encodingFilter.setEncoding( StandardCharsets.UTF_8.displayName() );
		encodingFilter.setForceEncoding( true );
		registrationBean.setFilter( encodingFilter );
		registrationBean.setFilter( new CommonsRequestLoggingFilter() );
		// 日志处理过滤器
		registrationBean.setFilter( new Filter() {
			@Override
			public void init ( FilterConfig filterConfig ) throws ServletException {
			}
			@Override
			public void doFilter ( ServletRequest servletRequest ,
								   ServletResponse servletResponse ,
								   FilterChain filterChain ) throws IOException, ServletException {
				final BodyReaderWrapper wrapper = new BodyReaderWrapper( ( HttpServletRequest ) servletRequest );
				filterChain.doFilter( wrapper , servletResponse );
			}

			@Override
			public void destroy () {

			}
		} );
		return registrationBean;
	}


}
