package com.shark.job.exception;


import com.shark.util.util.StringUtil;

/**
 * Job abstract exception class
 * @Author: SuLiang
 * @Date: 2018/10/31 0031
 */
public abstract class JobRuntimeException extends RuntimeException{
	JobRuntimeException() {
		super();
	}

	JobRuntimeException(String message, Object... args) {
		super(StringUtil.format(message,args));
	}

	JobRuntimeException(String message, Throwable cause, Object... args) {
		super(StringUtil.format(message,args), cause);
	}

	JobRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... args) {
		super(StringUtil.format(message,args), cause, enableSuppression, writableStackTrace);
	}
}
