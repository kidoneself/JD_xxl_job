package com.xxl.job.executor.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.executor.po.Env;
import org.jeecg.modules.tort.entity.FbmUserInfo;
import org.springframework.web.bind.annotation.Mapping;

import java.util.List;
import java.util.Map;

/**
 * @Description: 用户作者
 * @Author: jeecg-boot
 * @Date: 2021-09-06
 * @Version: V1.0
 */
@Mapping
public interface EvnMapper extends BaseMapper<Env> {

    Map<Integer, FbmUserInfo> getByUserIdListToMap(List<Integer> createUserIdList);
}
