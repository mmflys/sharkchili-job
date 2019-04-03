package com.shark.job.factory;

import com.shark.job.exception.JobException;
import com.shark.job.job.TemplateScheduleJob;
import com.shark.job.job.ScheduleJob;
import org.quartz.Job;
import org.quartz.Trigger;

import java.util.Date;

/**
 * A factory to create a {@link TemplateScheduleJob}
 * @Author: SuLiang
 * @Date: 2018/9/21 0021
 */
public class JobFactory {

	public static ScheduleJob cyclePeriodTask(long initDelay, long periodTime, int executeTime, Job runnable) {
		return new TemplateScheduleJob(initDelay, periodTime, executeTime, runnable, "cyclePeriodTask");
	}

	public static ScheduleJob cyclePeriodTask(long initDelay, long periodTime, int executeTime, Job runnable, String jobName) {
		return new TemplateScheduleJob(initDelay, periodTime, executeTime, runnable, jobName);
	}

	public static ScheduleJob onceTask(long initDelay, Job runnable) {
		return new TemplateScheduleJob(initDelay, 1, 1, runnable, "onceTask");
	}

	public static ScheduleJob onceTask(long initDelay, Job runnable, String jobName) {
		return new TemplateScheduleJob(initDelay, initDelay, 1, runnable, jobName);
	}

	public static ScheduleJob onceImmediatelyTask(Job runnable) {
		return new TemplateScheduleJob(0, 0, 1, runnable, "onceImmediatelyTask");
	}

	public static ScheduleJob onceImmediatelyTask(Job runnable, String jobName) {
		return new TemplateScheduleJob(0, 0, 1, runnable, jobName);
	}

	public static ScheduleJob startAtDate(Date start, Date end, long period, Job job, String jobName){
		if (validateDate(start, end)){
			return new TemplateScheduleJob(job,start,end,period,jobName);
		}else {
			throw new JobException("Date is illegal,must to satisfy start date >= now and query date > start");
		}
	}

	public static ScheduleJob startAtDate(Date start, long period, int executeTime, Job job, String jobName){
		if (validateDate(start)){
			return new TemplateScheduleJob(start.getTime()-System.currentTimeMillis(),period,executeTime,job,jobName);
		}else {
			throw new JobException("Date is illegal,must to satisfy start date >= now");
		}
	}

	public static ScheduleJob triggerJob(Trigger trigger, Job job, String name){
		return new TemplateScheduleJob(job,trigger,name);
	}

	/**
	 * Validate date.
	 * @param start the date of job start
	 * @param end the date of job end
	 * @return whether start is bigger than now is or not an end is bigger than start
	 */
	private static boolean validateDate(Date start,Date end){
		boolean valid=validateDate(start);
		if (end!=null){
			return valid&&end.getTime()>start.getTime();
		}
		return valid;
	}

	/**
	 * Validate date.
	 * @param start the date of job start
	 * @return whether start is bigger than now is or not
	 */
	private static boolean validateDate(Date start){
		return start.getTime()>=new Date().getTime();
	}
}
