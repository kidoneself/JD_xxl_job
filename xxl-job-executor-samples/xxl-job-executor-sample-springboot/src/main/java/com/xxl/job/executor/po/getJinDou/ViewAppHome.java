package com.xxl.job.executor.po.getJinDou;

import lombok.Data;

public @Data class ViewAppHome{
	private boolean takenTask;
	private boolean doneTask;
	private String subTitle;
	private String mainTitle;
	private String icon;
	private String popImg;
	private int addGrowth;
}