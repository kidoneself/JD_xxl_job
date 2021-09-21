package com.xxl.job.executor.po.shake;

import java.util.List;
import lombok.Data;

public @Data class SignActCyclesItem{
	private int jingBean;
	private int signCursor;
	private int signStatus;
	private List<SignItemsItem> signItems;
}