package com.github.obase.webc.support;

import java.io.IOException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

public class BaseAsyncListener implements AsyncListener {

	@Override
	public void onComplete(AsyncEvent event) throws IOException {

	}

	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		event.getAsyncContext().getResponse().flushBuffer(); // commit response
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		event.getAsyncContext().getResponse().flushBuffer(); // commit response
	}

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {

	}

}
