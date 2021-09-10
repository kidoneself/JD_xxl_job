package com.xxl.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.HttpInstanceFactory;
import com.xxl.job.executor.core.config.XxlJobConfig;
import com.xxl.job.executor.mapper.EnvMapper;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 跨平台Http任务
 *
 * @author xuxueli 2018-09-16 03:48:34
 */
@JobHandler(value = "httpJobHandler")
@Component
@Slf4j
public class HttpJobHandler extends IJobHandler {

    @Resource
    private EnvMapper envMapper;
    private List<Env> envs;


    @Override
    public ReturnT<String> execute(String param) {
        String baseUrl = "https://api.m.jd.com/client.action";
        XxlJobLogger.log("\n\nXXXX开始");
        XxlJobLogger.log("共获取到" + envs.size() + "个账号");
        //获取Http实例
        HttpInstanceFactory.HttpInstance instance = HttpInstanceFactory.getInstance();
        // 开始执行所有ck
        envs.forEach(env -> {
            // 获取当前账号的ck
            String cookie = env.getEnvValue();
            JDUser userInfo = null;
            try {
                /*================= 获取用户信息 ================= */

                HashMap<String, String> loginMap = new HashMap<>();
                loginMap.put("cookie", cookie);
                loginMap.put("User-Agent", "jdapp;android;10.0.2;10;network/wifi;Mozilla/5.0 (Linux; Android 10; GM1910 Build/QKQ1.190716.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36");
                XxlJobLogger.log("获取用户信息");
                userInfo = instance.getUserInfo(loginMap);
                if (userInfo != null) {
                    /*================= 初始化农场 ================= */
                    XxlJobLogger.log("初始化农场");
                    Map<String, String> initMap = new HashMap<>();
                    initMap.put("Host", "api.m.jd.com");
                    initMap.put("sec-fetch-mode", "cors");
                    initMap.put("origin", "https://carry.m.jd.com");
                    initMap.put("user-agent", "jdapp;android;10.1.2;10;1393336683662616-3693934383367343;network/4g;model/JEF-AN00;addressid/4168482853;aid/193f8fbac9948c74;oaid/1cad52a0-a01e-4a4b-96ac-4a734a49e3e6;osVer/29;appBuild/89760;partner/huaweiharmony;eufv/1;jdSupportDarkMode/0;Mozilla/5.0 (Linux; Android 10; JEF-AN00 Build/HUAWEIJEF-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045709 Mobile Safari/537.36");
                    initMap.put("accept", "*/*");
                    initMap.put("x-requested-with", "com.jingdong.app.mall");
                    initMap.put("sec-fetch-site", "same-site");
                    initMap.put("referer", "https://carry.m.jd.com/babelDiy/Zeus/3KSjXqQabiTuD1cJ28QskrpWoBKT/index.html?babelChannel=121&lng=121.463611&lat=31.021696&sid=5ff1f498bb1025bac5c96263ecafc15w&un_area=2_2813_61130_0");
                    initMap.put("accept-encoding", "gzip, deflate, br");
                    initMap.put("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
                    initMap.put("cookie", cookie);
                    String initUrl = "?functionId=initForFarm&body=%7B%22babelChannel%22%3A%22121%22%2C%22lng%22%3A%22121.463611%22%2C%22lat%22%3A%2231.021696%22%2C%22sid%22%3A%225ff1f498bb1025bac5c96263ecafc15w%22%2C%22un_area%22%3A%222_2813_61130_0%22%2C%22version%22%3A14%2C%22channel%22%3A1%7D&appid=wh5";
                    String initRes = instance.doGet(baseUrl + initUrl, initMap);
                    JSONObject initObj = JSONObject.parseObject(initRes);
                    JSONObject farmUserPro = initObj.getJSONObject("farmUserPro");
                    Object shareCode = farmUserPro.get("shareCode");
                    XxlJobLogger.log(userInfo.getNickname() + "的助力码ShareCode：" + shareCode);

                    /*================= 开始农场助力 ================= */
                    String helpUrl = "?functionId=initForFarm&appid=wh5&body=%7B%22imageUrl%22%3A%22%22%2C%22nickName%22%3A%22%22%2C%22shareCode%22%3A%22" + "81f8c0f0ea554b2385d4f866d4b2203f" + "%22%2C%22babelChannel%22%3A%223%22%2C%22version%22%3A2%2C%22channel%22%3A1%7D";
                    HashMap<String, String> helpMap = new HashMap<>();
                    helpMap.put("cookie", cookie);
                    helpMap.put("user-agent", "jdapp;android;10.1.2;10;1393336683662616-3693934383367343;network/4g;model/JEF-AN00;addressid/4168482853;aid/193f8fbac9948c74;oaid/1cad52a0-a01e-4a4b-96ac-4a734a49e3e6;osVer/29;appBuild/89760;partner/huaweiharmony;eufv/1;jdSupportDarkMode/0;Mozilla/5.0 (Linux; Android 10; JEF-AN00 Build/HUAWEIJEF-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045709 Mobile Safari/537.36");
                    XxlJobLogger.log("开始助力");
                    String helpRes = instance.doGet(baseUrl + helpUrl, helpMap);
                    JSONObject helpObj = JSONObject.parseObject(helpRes);
                    Integer code = Integer.parseInt(helpObj.getJSONObject("helpResult").get("code").toString());
                    Object helpUser = helpObj.getJSONObject("helpResult").getJSONObject("masterUserInfo").get("nickName");
                    switch (code) {
                        case 0:
                            XxlJobLogger.log("【助力好友结果】: 已成功给【" + helpUser + "】助力");
                            break;
                        case 7:
                            XxlJobLogger.log("【不能为自己助力哦，跳过自己的shareCode】");
                            break;
                        case 8:
                            XxlJobLogger.log("【助力好友结果】: 助力【" + helpUser + "】失败，您今天助力次数已耗尽");
                            break;
                        case 9:
                            XxlJobLogger.log("【助力好友结果】: 今天已经给【" + helpUser + "】助力过了");
                            break;
                        case 10:
                            XxlJobLogger.log("【助力好友结果】: 好友【" + helpUser + "】已满五人助力");
                            break;
                    }
                    Object remainTimes = helpObj.getJSONObject("helpResult").get("remainTimes");
                    XxlJobLogger.log(userInfo.getNickname() + "剩余助力【" + remainTimes + "】次");


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return SUCCESS;
    }

    @Override
    public void init() {
        // 获取所有账号
        List<Env> envs = envMapper.getAllCookie();
        this.envs = envs;
    }

    @Override
    public void destroy() {

    }

}
