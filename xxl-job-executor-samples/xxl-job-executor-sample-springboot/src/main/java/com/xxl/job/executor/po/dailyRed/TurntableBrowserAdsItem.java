package com.xxl.job.executor.po.dailyRed;

import lombok.Data;

public @Data class TurntableBrowserAdsItem{
	private int lotteryTimes;
	private String sub;
	private String adId;
	private int totalTimes;
	private String icon;
	private int browserTimes;
	private String link;
	private String main;
	private boolean gotStatus;
	private boolean status;
}