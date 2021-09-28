package com.xxl.job.executor.po.getJinDou;

import java.util.List;
import lombok.Data;

public @Data class TaskInfosItem{
	private String subTitleName;
	private String score;
	private Integer maxTimes;
	private Integer taskType;
	private String process;
	private Integer waitDuration;
	private Integer times;
	private String icon;
	private String taskName;
	private List<SubTaskVOSItem> subTaskVOS;
	private Integer taskId;
	private Integer status;
}