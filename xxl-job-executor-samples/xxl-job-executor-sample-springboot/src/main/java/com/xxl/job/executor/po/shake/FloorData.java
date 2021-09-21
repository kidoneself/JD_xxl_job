package com.xxl.job.executor.po.shake;

import java.util.List;
import lombok.Data;

public @Data class FloorData{
	private RetainPopAd retainPopAd;
	private UserScoreModule userScoreModule;
	private SignCocoonCardAd signCocoonCardAd;
	private List<ShakingAdFloatItem> shakingAdFloat;
	private UserBaseInfo userBaseInfo;
	private SignActInfo signActInfo;
	private ShakingBoxInfo shakingBoxInfo;
}