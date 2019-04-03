package com.shark.job.job;

import org.quartz.*;

/**
 * A implement class of Job,it could be stop,pause,resume,mean that the job can be stop schedule and stop running,resume,pause.
 * @Author: SuLiang
 * @Date: 2018/9/6 0006
 */
public interface ScheduleJob extends Job {
	JobDetail getJobDetail();
	Trigger getTrigger();
	JobKey getJobKey();
	TriggerKey getTriggerKey();
	void fireFinish();
	void fireStop();
	void firePause();
	void fireResume();
	void fireRunning();
}
