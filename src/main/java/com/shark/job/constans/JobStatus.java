package com.shark.job.constans;

/**
 * Job status
 * @Author: SuLiang
 * @Date: 2018/9/22 0022
 */
public enum JobStatus {
	/**original status,not any operation*/
	INIT,
	/**job is running*/
	RUNNING,
	/**job is pausing*/
	PAUSE,
	/**job is executing resume*/
	EXECUTE_RESUME,
	/**job had finished and stopped*/
	FINISH,STOP
}
