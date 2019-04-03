package com.shark.job.container;

import com.shark.container.Container;
import com.shark.job.job.ScheduleJob;
import com.shark.util.util.FileUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The abstract task class of Container
 * @Author: SuLiang
 * @Date: 2018/9/5 0005
 */
public abstract class TaskContainer implements Container {
	private static final Logger LOGGER= LoggerFactory.getLogger(TaskContainer.class);

	public static final String SCHEDULER_NAME_SYSTEM="systemScheduler";
	public static final String SCHEDULER_NAME_TOURISTS ="touristsScheduler";
	private static final String QUARTZ_PROPERTIES_FILENAME="sharkQuartz.properties";

	private StdSchedulerFactory stdSchedulerFactory;
	private ConcurrentHashMap<String, Scheduler> schedulers;

	public TaskContainer() {
		stdSchedulerFactory=new StdSchedulerFactory();
		schedulers=new ConcurrentHashMap<>();
	}

	@Override
	public Container init() {
		LOGGER.info(TaskContainer.class.getName()+" init");
		if (schedulers.size()!=0) return this;
		LOGGER.info(TaskContainer.class.getSimpleName()+" init");
		// 初始创建两个scheduler
		try {
			createScheduler(SCHEDULER_NAME_SYSTEM).start();
			createScheduler(SCHEDULER_NAME_TOURISTS).start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		containerInit();
		//程序结束时调用钩子线程
		Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
		return this;
	}

	@Override
	public Container start() {
		LOGGER.info(TaskContainer.class.getSimpleName()+" start");
		containerStart();
		return this;
	}

	@Override
	public Container stop() {
		LOGGER.info(TaskContainer.class.getSimpleName()+" stop");
		containerStop();
		return this;
	}

	public abstract void containerInit();
	public abstract void containerStart();
	public abstract void containerStop();

	/**
	 * Create a scheduler with a giving name
	 * @param name scheduler name
	 * @return a Scheduler
	 */
	private Scheduler createScheduler(String name){
		Properties properties= FileUtil.readProperties("/"+QUARTZ_PROPERTIES_FILENAME);
		properties.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME,name);
		return createScheduler(properties);
	}

	/**
	 * Create a scheduler according to the properties file
	 * @param properties the properties file to create scheduler
	 * @return a scheduler
	 */
	private Scheduler createScheduler(Properties properties){
		//判断是否有 org.quartz.scheduler.instanceName 属性
		String schedulerName=properties.getProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME);
		if (schedulerName==null||schedulerName.equals("")){
			properties.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, SCHEDULER_NAME_TOURISTS);
			schedulerName= SCHEDULER_NAME_TOURISTS;
		}
		Scheduler scheduler = null;
		try {
			stdSchedulerFactory.initialize(properties);
			scheduler=stdSchedulerFactory.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		if (scheduler!=null){
			schedulers.put(schedulerName,scheduler);
		}
		LOGGER.info("Create a scheduler {}",scheduler);
		return scheduler;
	}

	/**
	 * Get a scheduler,to create it if it is null.
	 * @param name scheduler name
	 * @return a scheduler
	 */
	public Scheduler getScheduler(String name){
		Scheduler scheduler=schedulers.get(name);
		if (scheduler==null){
			scheduler=createScheduler(name);
		}
		return scheduler;
	}

	/**
	 * Get a tourists scheduler
	 * @return a default scheduler (tourists)
	 */
	public Scheduler getScheduler(){
		return getScheduler(SCHEDULER_NAME_TOURISTS);
	}

	/**
	 * Schedule a com.shark.job.job
	 * @param job to be scheduled
	 */
	public void schedule(ScheduleJob job){
		schedule(job.getJobDetail(),job.getTrigger(),SCHEDULER_NAME_TOURISTS);
	}

	/**
	 * Schedule a com.shark.job.job
	 * @param job to be scheduled
	 * @param schedulerName scheduler name
	 */
	public void schedule(ScheduleJob job, String schedulerName){
		schedule(job.getJobDetail(),job.getTrigger(),schedulerName);
	}

	/**
	 * Schedule a com.shark.job.task
	 * @param trigger  Trigger
	 * @param schedulerName scheduler name
	 */
	public synchronized void schedule(JobDetail jobDetail,Trigger trigger,String schedulerName){
		try {
			LOGGER.info("Start to schedule com.shark.job.job: jobDetail {}, trigger {}",jobDetail,trigger);
			getScheduler(schedulerName).scheduleJob(jobDetail,trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Schedule a com.shark.job.task,default to use application scheduler
	 * @param jobDetail a JobDetail
	 * @param trigger a Trigger
	 */
	public void schedule(JobDetail jobDetail,Trigger trigger){
		schedule(jobDetail,trigger, SCHEDULER_NAME_TOURISTS);
	}

	/**
	 * Pause the <code>{@link JobDetail}</code> with the given
	 * key and given scheduler name
	 * - by pausing all of its current <code>Trigger</code>s.
	 * @param jobKey job key
	 * @param schedulerName scheduler name
	 */
	public void pauseJob(JobKey jobKey,String schedulerName){
		try {
			getScheduler(schedulerName).pauseJob(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * firePause com.shark.job.job with the given key and default scheduler name 'tourists'
	 * @param jobKey job key
	 */
	public void pauseJob(JobKey jobKey){
		pauseJob(jobKey,SCHEDULER_NAME_TOURISTS);
	}

	/**
	 * Pause the <code>{@link Trigger}</code> with the given key.
	 * @param triggerKey trigger key
	 * @param schedulerName scheduler name
	 */
	public void pauseTrigger(TriggerKey triggerKey,String schedulerName){
		try {
			getScheduler(schedulerName).pauseTrigger(triggerKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pause all triggers
	 * @param schedulerName scheduler name
	 */
	public void pauseAll(String schedulerName){
		try {
			getScheduler(schedulerName).pauseAll();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pause all triggers
	 */
	public void pauseAll(){
		pauseAll(SCHEDULER_NAME_TOURISTS);
	}

	/**
	 * Resume com.shark.job.job (un firePause)
	 * @param jobKey job key
	 * @param schedulerName scheduler name
	 */
	public void resumeJob(JobKey jobKey,String schedulerName){
		try {
			getScheduler(schedulerName).resumeJob(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resume com.shark.job.job (un firePause)
	 * @param jobKey job key
	 */
	public void resumeJob(JobKey jobKey){
		resumeJob(jobKey,SCHEDULER_NAME_TOURISTS);
	}

	/**
	 * Resume trigger
	 * @param triggerKey trigger key
	 * @param schedulerName scheduler name
	 */
	public void resumeTrigger(TriggerKey triggerKey,String schedulerName){
		try {
			getScheduler(schedulerName).resumeTrigger(triggerKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resume trigger,default scheduler name 'tourists'
	 * @param triggerKey trigger key
	 */
	public void resumeTrigger(TriggerKey triggerKey){
		resumeTrigger(triggerKey,SCHEDULER_NAME_TOURISTS);
	}

	/**
	 * Resume (un-firePause) all triggers
	 * @param schedulerName scheduler name
	 */
	public void resumeAll(String schedulerName){
		try {
			getScheduler(schedulerName).resumeAll();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resume (un-firePause) all triggers,default scheduler name 'tourists'
	 */
	public void resumeAll(){
		resumeAll(SCHEDULER_NAME_TOURISTS);
	}

	/**
	 * Stop all triggers from the com.shark.job.job
	 * firePause com.shark.job.job firstly,DELETE com.shark.job.job secondly.
	 * @param jobKey job key
	 * @param schedulerName scheduler name
	 */
	public void stopJob(JobKey jobKey,String schedulerName){
		try {
			getScheduler(schedulerName).pauseJob(jobKey);
			getScheduler(schedulerName).deleteJob(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop all triggers from the com.shark.job.job,default scheduler name 'tourists'
	 * firePause com.shark.job.job firstly,DELETE com.shark.job.job secondly.
	 * @param jobKey job key
	 */
	public void stopJob(JobKey jobKey){
		stopJob(jobKey,SCHEDULER_NAME_TOURISTS);
	}

	/**
	 * Stop trigger
	 * @param triggerKey trigger key
	 * @param schedulerName scheduler name
	 */
	public void stopTrigger(TriggerKey triggerKey,String schedulerName){
		try {
			pauseTrigger(triggerKey,schedulerName);
			getScheduler(schedulerName).unscheduleJob(triggerKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop trigger,default scheduler name 'tourists'
	 * @param triggerKey trigger key
	 */
	public void stopTrigger(TriggerKey triggerKey){
		stopTrigger(triggerKey,SCHEDULER_NAME_TOURISTS);
	}
}
