package com.aidijing;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;



public class BodyReaderWrapper extends HttpServletRequestWrapper {

	private final byte[] body;

	public BodyReaderWrapper ( HttpServletRequest request ) throws IOException {
		super( request );
		if ( this.isApplicationJsonHeader( request ) ) {
			body = StreamUtils.copyToByteArray( request.getInputStream() );
		} else {
			body = null;
		}
	}

	@Override
	public BufferedReader getReader () throws IOException {
		return new BufferedReader( new InputStreamReader( this.getInputStream() ) );
	}

	@Override
	public ServletInputStream getInputStream () throws IOException {
		if ( null == body ) {
			return super.getInputStream();
		}
		final ByteArrayInputStream inputStream = new ByteArrayInputStream( body );
		return new ServletInputStream() {
			@Override
			public boolean isFinished () {
				return false;
			}

			@Override
			public boolean isReady () {
				return false;
			}

			@Override
			public void setReadListener ( ReadListener readListener ) {

			}

			@Override
			public int read () throws IOException {
				return inputStream.read();
			}
		};
	}

	private boolean isApplicationJsonHeader ( HttpServletRequest request ) {
		String contentType = request.getHeader( HttpHeaders.CONTENT_TYPE );
		return contentType != null && StringUtils.replaceAll(
			contentType.trim() ,
			StringUtils.SPACE ,
			StringUtils.EMPTY
		).contains( MediaType.APPLICATION_JSON_VALUE );
	}

}
