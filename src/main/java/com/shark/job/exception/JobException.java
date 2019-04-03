package com.shark.job.exception;

/**
 * Job exception
 * @Author: sharkchili
 * @Date: 2018/11/14 0014
 */
public class JobException extends JobRuntimeException{
	public JobException() {
		super();
	}

	public JobException(String message, Object... args) {
		super(message, args);
	}

	public JobException(String message, Throwable cause, Object... args) {
		super(message, cause, args);
	}

	public JobException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... args) {
		super(message, cause, enableSuppression, writableStackTrace, args);
	}
}
