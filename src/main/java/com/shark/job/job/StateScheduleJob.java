package com.shark.job.job;

import com.google.common.collect.Maps;
import org.quartz.Job;

import java.util.Map;

/**
 * A state ScheduleJob extends AbstractScheduleJob,it can put variable and get variable
 * @Author: SuLiang
 * @Date: 2018/9/21 0021
 */
public abstract class StateScheduleJob extends AbstractScheduleJob {
	/**
	 * story job variable
	 */
	private Map<String,Object> variable;

	public StateScheduleJob() {
		this.variable = Maps.newHashMap();
	}

	public Map<String, Object> getVariable() {
		return variable;
	}

	/**
	 * Put a variable to this job
	 * @param key key
	 * @param variable variable
	 * @return the job
	 */
	public Job put(String key,Object variable){
		this.variable.put(key,variable);
		return this;
	}
}
