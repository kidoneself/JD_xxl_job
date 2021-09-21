package com.xxl.job.executor.po.shake;

import lombok.Data;

public @Data class UserBaseInfo{
	private int userLevel;
	private Object gender;
	private boolean newUser;
	private String nickname;
	private int plusUser;
	private int jxScore;
	private int registrationDays;
}