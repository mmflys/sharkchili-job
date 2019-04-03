package com.shark.job.job;

import org.quartz.*;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Job template to create a ScheduleJob
 */
public class TemplateScheduleJob extends AbstractScheduleJob {

	private static final String JOB_STR = "jobStr";
	private static final String JOB_KEY_STR = "jobKEY";
	private static final String TRIGGER_KEY_STR = "triggerKey";
	private static AtomicInteger sequence = new AtomicInteger(1);

	public TemplateScheduleJob() {}

	/**
	 * @param initDelay   init time delay
	 * @param periodTime  period time of job execute
	 * @param executeTime execute times
	 * @param job job
	 * @param name job name
	 */
	public TemplateScheduleJob(long initDelay, long periodTime, int executeTime, Job job, String name) {
		JobKey JOB_KEY = getDefaultJobKey(name);
		TriggerKey TRIGGER_KEY = getDefaultTriggerKey(name);
		//增加一次序号
		sequence.incrementAndGet();
		JobDetail jobDetail = JobBuilder.newJob(TemplateScheduleJob.class).withIdentity(JOB_KEY).build();
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
		// 是否需要周期执行
		if (periodTime != 0) {
			scheduleBuilder.withIntervalInMilliseconds(periodTime).withRepeatCount(executeTime);
		}
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(TRIGGER_KEY)
				.withSchedule(scheduleBuilder)
				.startAt(new Date(System.currentTimeMillis() + initDelay))
				.build();
		setParameters(trigger, jobDetail, JOB_KEY, TRIGGER_KEY);

		jobDetail.getJobDataMap().put(JOB_STR, job);
		jobDetail.getJobDataMap().put(JOB_KEY_STR, JOB_KEY);
		jobDetail.getJobDataMap().put(TRIGGER_KEY_STR, TRIGGER_KEY);
	}

	/**
	 * A cycle job start at point time and end at point time
	 * @param job job
	 * @param start start date of job execute
	 * @param end end date of job execute
	 * @param period period of job execute
	 * @param name job name
	 */
	public TemplateScheduleJob(Job job, Date start, Date end, long period, String name){
		JobKey JOB_KEY = getDefaultJobKey(name);
		TriggerKey TRIGGER_KEY = getDefaultTriggerKey(name);
		//增加一次序号
		sequence.incrementAndGet();
		JobDetail jobDetail = JobBuilder.newJob(TemplateScheduleJob.class).withIdentity(JOB_KEY).build();
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
		if (period!=0){
			scheduleBuilder.withIntervalInMilliseconds(period);
		}
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(TRIGGER_KEY)
				.withSchedule(scheduleBuilder)
				.startAt(start)
				.endAt(end)
				.build();
		setParameters(trigger, jobDetail, JOB_KEY, TRIGGER_KEY);

		jobDetail.getJobDataMap().put(JOB_STR, job);
		jobDetail.getJobDataMap().put(JOB_KEY_STR, JOB_KEY);
		jobDetail.getJobDataMap().put(TRIGGER_KEY_STR, TRIGGER_KEY);
	}

	/**
	 * A trigger job
	 * @param job job
	 * @param trigger {@link Trigger}
	 * @param name job name
	 */
	public TemplateScheduleJob(Job job, Trigger trigger, String name){
		JobKey JOB_KEY = getDefaultJobKey(name);
		TriggerKey TRIGGER_KEY = getDefaultTriggerKey(name);
		if (trigger.getKey()!=null){
			TRIGGER_KEY=trigger.getKey();
		}
		//增加一次序号
		sequence.incrementAndGet();
		JobDetail jobDetail = JobBuilder.newJob(TemplateScheduleJob.class).withIdentity(JOB_KEY).build();
		setParameters(trigger, jobDetail, JOB_KEY, TRIGGER_KEY);
		jobDetail.getJobDataMap().put(JOB_STR, job);
		jobDetail.getJobDataMap().put(JOB_KEY_STR, JOB_KEY);
		jobDetail.getJobDataMap().put(TRIGGER_KEY_STR, TRIGGER_KEY);
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobKey jobKey = (JobKey) context.getJobDetail().getJobDataMap().get(JOB_KEY_STR);
		try {
			switch (getJobStatus()) {
				case INIT: {
					Job job = (Job) context.getJobDetail().getJobDataMap().get(JOB_STR);
					job.execute(context);
					fireRunning();
					break;
				}
				case RUNNING: {
					Job job = (Job) context.getJobDetail().getJobDataMap().get(JOB_STR);
					job.execute(context);
					break;
				}
				case PAUSE: {
					context.getScheduler().pauseJob(jobKey);
					break;
				}
				case EXECUTE_RESUME: {
					context.getScheduler().resumeJob(jobKey);
					break;
				}
				case FINISH: {
					context.getScheduler().pauseJob(jobKey);
					context.getScheduler().deleteJob(jobKey);
					break;
				}
				case STOP: {
					context.getScheduler().pauseJob(jobKey);
					context.getScheduler().deleteJob(jobKey);
					break;
				}
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	private static TriggerKey getDefaultTriggerKey(String name) {
		return TriggerKey.triggerKey(TemplateScheduleJob.class.getSimpleName() + "-trigger-"+ sequence.incrementAndGet()+"-"+name, "defaultTriggerKey");
	}

	private static JobKey getDefaultJobKey(String name) {
		return JobKey.jobKey(TemplateScheduleJob.class.getSimpleName() + "-com.shark.job.job-" + sequence.incrementAndGet()+"-"+name, "defaultJobKey");
	}
}
