package com.xxl.job.executor.po.shake;

import java.util.List;
import lombok.Data;

public @Data class SignActInfo{
	private Object requestTime;
	private CursorBeanNum cursorBeanNum;
	private String instanceId;
	private int browseTaskTotalTimes;
	private int currSignCursor;
	private int currDraw;
	private boolean identLimit;
	private int browseTaskCurTimes;
	private MaxBeanNum maxBeanNum;
	private List<SignActCyclesItem> signActCycles;
}