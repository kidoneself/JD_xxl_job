package com.xxl.job.executor.po.shake;

import java.util.List;
import lombok.Data;

public @Data class UserScoreModule{
	private List<String> rollingText;
	private boolean genUserRollText;
	private List<AwardListItem> awardList;
	private String scoreUpLinkUrl;
}