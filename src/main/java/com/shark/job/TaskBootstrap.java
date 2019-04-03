package com.shark.job;

import com.shark.container.Container;
import com.shark.container.boot.Boot;
import com.shark.container.boot.Bootstrap;
import com.shark.job.container.DefaultTaskContainer;

/**
 * @Author: sharkchili
 * @Date: 2018/11/15 0015
 */
public class TaskBootstrap extends Bootstrap {

	@Override
	public Container provide() {
		return new DefaultTaskContainer();
	}

	/**
	 * Get a singleton
	 * @return a single instance of the TaskBootstrap
	 */
	public static TaskBootstrap get() {
		return (TaskBootstrap) InstanceHolder.BOOT;
	}

	/**
	 * Inner class for hold external class instance
	 */
	private static class InstanceHolder {

		private static final Boot BOOT=new TaskBootstrap();

		private void InvocationHandler() {
		}
	}

	public static void main(String[] args) {
		TaskBootstrap.get().launch();
	}
}
