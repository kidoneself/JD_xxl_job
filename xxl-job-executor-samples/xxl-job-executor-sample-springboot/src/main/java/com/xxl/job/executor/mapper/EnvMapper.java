package com.xxl.job.executor.mapper;


import com.xxl.job.executor.po.Env;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EnvMapper {

   List<Env> getAllCookie();

}
