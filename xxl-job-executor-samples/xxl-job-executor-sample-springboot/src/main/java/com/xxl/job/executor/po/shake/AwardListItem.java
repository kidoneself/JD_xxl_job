package com.xxl.job.executor.po.shake;

import lombok.Data;

public @Data class AwardListItem{
	private int signMaxBean;
	private boolean current;
	private int min;
	private int max;
	private int shakingTimesPerDay;
}