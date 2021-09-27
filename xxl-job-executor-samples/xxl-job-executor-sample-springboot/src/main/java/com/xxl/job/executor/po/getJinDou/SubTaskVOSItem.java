package com.xxl.job.executor.po.getJinDou;

import java.util.List;
import lombok.Data;

public @Data class SubTaskVOSItem{
	private String mcInfo;
	private List<Object> comments;
	private String subtitle;
	private String icon;
	private String copy3;
	private String copy2;
	private String copy1;
	private String title;
	private String url;
	private int status;
	private String taskToken;
}