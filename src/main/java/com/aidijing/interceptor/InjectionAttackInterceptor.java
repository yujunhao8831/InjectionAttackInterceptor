package com.aidijing.interceptor;

import com.aidijing.annotation.PassInjectionAttackIntercept;
import com.aidijing.handler.DefaultInjectionAttackHandler;
import com.aidijing.handler.InjectionAttackHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * 注入攻击拦截器
 *
 * @author : 披荆斩棘
 * @date : 2017/8/29
 * @see PassInjectionAttackIntercept
 */
@Getter
@Setter
@Slf4j
public class InjectionAttackInterceptor extends HandlerInterceptorAdapter {


	private InjectionAttackHandler injectionAttackHandler = DefaultInjectionAttackHandler.getInstance();

	@Override
	public boolean preHandle ( HttpServletRequest request , HttpServletResponse response , Object handler ) throws
																											Exception {
		if ( ! ( handler instanceof HandlerMethod ) ) {
			return false;
		}

		HandlerMethod handlerMethod = ( HandlerMethod ) handler;

		final PassInjectionAttackIntercept passInjectionAttackIntercept =
			this.getHandlerAnnotation( handlerMethod , PassInjectionAttackIntercept.class );


		String[] ignoreStrings = null;
		if ( Objects.nonNull( passInjectionAttackIntercept ) ) {
			if ( ArrayUtils.isEmpty( passInjectionAttackIntercept.value() ) ) {
				log.debug( "pass,不需要注入攻击拦截" );
				return true;
			}
			ignoreStrings = passInjectionAttackIntercept.value();
		}


		final String parameters = this.getRequestParameters( request );
		log.debug( "请求参数 : {} " , parameters );
		log.debug( "ignoreStrings : {} " , Arrays.toString( ignoreStrings ) );


		// 参数注入攻击处理
		if ( this.injectionAttackHandler.isInjectionAttack( parameters , ignoreStrings ) ) {
			log.debug( "参数 {} 被判断为注入攻击" , parameters );
			this.injectionAttackHandler.attackHandle( request , response , parameters );
			return false;
		}

		final Map< String, String > decodedUriVariables = ( Map< String, String > ) request.getAttribute( HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE );

		if ( MapUtils.isEmpty( decodedUriVariables ) ) {
			return true;
		}

		// URI PATH 注入攻击处理
		for ( String decodedUriVariable : decodedUriVariables.values() ) {
			if ( this.injectionAttackHandler.isInjectionAttack( decodedUriVariable , ignoreStrings ) ) {

				log.debug( "URI {} 被判断为注入攻击" , parameters );
				this.injectionAttackHandler.attackHandle( request , response , decodedUriVariable );
				return false;
			}
		}
		return true;
	}

	private < T extends Annotation > T getHandlerAnnotation ( HandlerMethod handlerMethod ,
															  Class< T > clazz ) {
		T annotation = handlerMethod.getMethodAnnotation( clazz );
		if ( Objects.nonNull( annotation ) ) {
			return annotation;
		}
		return handlerMethod.getBean().getClass().getAnnotation( clazz );
	}


	/**
	 * 是否是 Content-Type=application/json; json传输
	 *
	 * @param request
	 * @return
	 */
	private boolean isApplicationJsonHeader ( HttpServletRequest request ) {
		String contentType = request.getHeader( HttpHeaders.CONTENT_TYPE );
		return contentType != null && StringUtils.replaceAll(
			contentType.trim() ,
			StringUtils.SPACE ,
			StringUtils.EMPTY
		).contains( MediaType.APPLICATION_JSON_VALUE );
	}

	/**
	 * 得到请求参数 username=[披荆斩棘]
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private String getRequestParameters ( HttpServletRequest request ) throws IOException {
		StringBuilder parameters = new StringBuilder();
		if ( this.isApplicationJsonHeader( request ) ) {
			parameters.append( StreamUtils.copyToString( request.getInputStream() , StandardCharsets.UTF_8 ) );
		} else {
			request.getParameterMap().forEach(
				( String key , String[] values ) -> parameters.append( key )
															  .append( "=" )
															  .append( Arrays.toString( values ) )
															  .append( "\t" )
			);
		}
		return parameters.toString();
	}
}
