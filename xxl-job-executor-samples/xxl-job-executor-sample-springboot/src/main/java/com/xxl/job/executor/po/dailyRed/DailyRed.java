package com.xxl.job.executor.po.dailyRed;

import java.util.List;
import lombok.Data;

public @Data class DailyRed{
	private String shareCodeAddOn;
	private List<Object> userFriendInfoDtos;
	private String code;
	private int helpedGotLotteryTimes;
	private int masterHelpTimes;
	private List<TurntableInfosItem> turntableInfos;
	private int addHelpUsers;
	private boolean timingGotStatus;
	private long timingLastSysTime;
	private Object message;
	private boolean firstEnter;
	private List<TurntableBrowserAdsItem> turntableBrowserAds;
	private Object statisticsTimes;
	private int timingIntervalHours;
	private int callUserGotLotteryTimes;
	private int callUserAddWater;
	private long sysTime;
	private int timingLotteryTimes;
	private int helpedTimesByOther;
	private List<Object> masterHelpUsers;
	private int remainLotteryTimes;
	private String callUserSendFruit;
	private boolean callUser;
}