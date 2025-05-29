package org.egov.finance.master.config.Filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

	 private final byte[] cachedBody;

	    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
	        super(request);
	        cachedBody = request.getInputStream().readAllBytes(); // Java 9+
	    }

	    @Override
	    public ServletInputStream getInputStream() {
	        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
	        return new ServletInputStream() {
	            @Override
	            public int read() {
	                return byteArrayInputStream.read();
	            }
	            @Override
	            public boolean isFinished() {
	                return byteArrayInputStream.available() == 0;
	            }
	            @Override
	            public boolean isReady() {
	                return true;
	            }
	            @Override
	            public void setReadListener(ReadListener readListener) {}
	        };
	    }

	    @Override
	    public BufferedReader getReader() {
	        return new BufferedReader(new InputStreamReader(getInputStream()));
	    }

	    public String getCachedBodyAsString() {
	        return new String(cachedBody);
	    }
}
