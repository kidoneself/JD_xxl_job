package com.xxl.job.executor.po.shake;

import lombok.Data;

public @Data class ShakingBoxInfo{
	private int dayBeanAmount;
	private int dayFreeTimes;
	private Object totalBeanAmount;
	private boolean dayFreeTimesTakeFlag;
	private int remainLotteryTimes;
}