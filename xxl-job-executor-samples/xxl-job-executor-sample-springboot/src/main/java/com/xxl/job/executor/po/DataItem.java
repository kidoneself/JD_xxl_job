package com.xxl.job.executor.po;

import java.util.List;
import lombok.Data;

public @Data class DataItem{
	private int totalPrizeTimes;
	private int eachPrizeTimes;
	private List<TaskItemsItem> taskItems;
	private String taskName;
	private int currentFinishTimes;
}