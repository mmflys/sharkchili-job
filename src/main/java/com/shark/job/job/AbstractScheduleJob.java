package com.shark.job.job;

import com.shark.job.constans.JobStatus;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

/**
 * The abstract class of ScheduleJob
 * @Author: SuLiang
 * @Date: 2018/9/6 0006
 */
public abstract class AbstractScheduleJob implements ScheduleJob {
	private Trigger trigger;
	private JobDetail jobDetail;
	private JobKey JOB_KEY;
	private TriggerKey TRIGGER_KEY;
	private JobStatus jobStatus;

	public AbstractScheduleJob() {
		jobStatus = JobStatus.INIT;
	}

	@Override
	public JobDetail getJobDetail() {
		return jobDetail;
	}

	@Override
	public Trigger getTrigger() {
		return trigger;
	}

	@Override
	public JobKey getJobKey() {
		return JOB_KEY;
	}

	@Override
	public TriggerKey getTriggerKey() {
		return TRIGGER_KEY;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	public void setJOB_KEY(JobKey JOB_KEY) {
		this.JOB_KEY = JOB_KEY;
	}

	public void setTRIGGER_KEY(TriggerKey TRIGGER_KEY) {
		this.TRIGGER_KEY = TRIGGER_KEY;
	}

	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public ScheduleJob setStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
		return this;
	}

	public void setParameters(Trigger trigger, JobDetail jobDetail, JobKey JOB_KEY, TriggerKey TRIGGER_KEY){
		this.trigger = trigger;
		this.jobDetail = jobDetail;
		this.JOB_KEY = JOB_KEY;
		this.TRIGGER_KEY = TRIGGER_KEY;
	}

	@Override
	public void fireFinish() {
		setStatus(JobStatus.FINISH);
	}

	@Override
	public void fireStop() {
		setStatus(JobStatus.STOP);
	}

	@Override
	public void firePause() {
		setStatus(JobStatus.PAUSE);
	}

	@Override
	public void fireResume() {
		setStatus(JobStatus.EXECUTE_RESUME);
	}

	@Override
	public void fireRunning() {
		setStatus(JobStatus.RUNNING);
	}
}
