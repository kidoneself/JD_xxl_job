package com.xxl.job.executor.po;

import java.util.List;
import lombok.Data;

/**
 * 摇京豆
 */
public @Data class ShakeList {
	private List<DataItem> data;
	private boolean success;
}