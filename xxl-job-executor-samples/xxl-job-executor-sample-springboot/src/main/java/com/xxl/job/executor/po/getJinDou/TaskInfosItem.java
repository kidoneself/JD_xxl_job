package com.xxl.job.executor.po.getJinDou;

import java.util.List;
import lombok.Data;

public @Data class TaskInfosItem{
	private String subTitleName;
	private String score;
	private int maxTimes;
	private int taskType;
	private String process;
	private int waitDuration;
	private int times;
	private String icon;
	private String taskName;
	private List<SubTaskVOSItem> subTaskVOS;
	private int taskId;
	private int status;
}