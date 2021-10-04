package com.xxl.job.executor.service.JDhandler.JDBeanUtils;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.executor.core.GetMethodIns;
import com.xxl.job.executor.core.UserAgentUtil;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import com.xxl.job.executor.service.CommonDo.CommonHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@JobHandler(value = "JD_SendBeans")
@Component
@Slf4j
public class JDSendBeans extends IJobHandler {

    @Resource
    private CommonHandler commonHandler;
    GetMethodIns getIns;
    HashMap<String, String> msg = UserAgentUtil.randomUserAgentMsg();
    Env env;
    List<Env> envs;
    HashMap<String, String> headerMap = new HashMap<>();
    JDUser userInfo;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long lkt = System.currentTimeMillis();
        String lks = SecureUtil.md5("'JL1VTNRadM68cIMQ'" + lkt);
        HashMap<String, String> header = new HashMap<>();
        header.put("Host", "sendbeans.jd.com");
        header.put("Origin", "https://sendbeans.jd.com");
        header.put("Cookie", "pt_key=AAJhRyK1ADCfaMLMkUA96laOm1_845DZqAuxdaP7mSbEeNfmuQoM2kItc-La3dm18Mb9e37nJ1w;pt_pin=wdlLxrYZBojiba;");
        header.put("Connection", "keep-alive");
        header.put("Accept", "application/json, text/plain, */*");
        header.put("User-Agent", "");
        header.put("Accept-Language", "zh-cn");
        header.put("Referer", "https://sendbeans.jd.com/dist/index.html");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("openId", "");
        header.put("lkt", Long.toString(lkt));
        header.put("lks", lks);
        String url = "https://sendbeans.jd.com/common/api/bean/activity/get/entry/list/by/channel?channelId=14&channelType=H5&sendType=0&singleActivity=false&invokeKey=JL1VTNRadM68cIMQ";
        String body = HttpRequest.post(url).headerMap(header, false).execute().body();
        System.out.println(body);
        return null;

    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
