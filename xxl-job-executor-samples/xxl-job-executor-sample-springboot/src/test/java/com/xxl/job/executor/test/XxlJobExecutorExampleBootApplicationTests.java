package com.xxl.job.executor.test;

import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XxlJobExecutorExampleBootApplicationTests {

    @Test
    public void test() throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get(
                "https://m.jingxi.com/dreamfactory/userinfo/GetUserInfo" +
                        "?zone=dream_factory" +
                        "&pin=" +
                        "&sharePin=" +
                        "&shareType=" +
                        "&materialTuanPin=" +
                        "&materialTuanId=" +
                        "&needPickSiteInfo=0" +
                        "&source=" +
                        "&_time=1631769429166&_stk=_time%2CmaterialTuanId%2CmaterialTuanPin%2CneedPickSiteInfo%2Cpin%2CsharePin%2CshareType%2Csource%2Czone&_ste=1&h5st=20210916131709170%3B3221261978731163%3B10001%3Btk01w97d11bd930nKRQ%2Bwp2sUsRBOcFaE6aHsVB5MsLSRRpxtKJTrK3OuSg3nvXX4%2FMaRdo0AUfCRn8nQR4x2l5p8uch%3Bc1ccbbade58440f8942fd770968591894dc92bb3dfce6a8d287a7b75f1db6f8f" +
                        "&_=1631769429178&sceneval=2" +
                        "&g_login_type=1" +
                        "&callback=jsonpCBKB" +
                        "&g_ty=ls")
                .header("Host", "m.jingxi.com")
                .header("user-agent", "jdpingou;android;5.5.0;10;193f8fbac9948c74;network/UNKNOWN;model/JEF-AN00;appBuild/18299;partner/huawei01;;session/73;aid/193f8fbac9948c74;oaid/1cad52a0-a01e-4a4b-96ac-4a734a49e3e6;pap/JA2019_3111789;brand/HUAWEI;eu/1393336683662616;fv/3693934383367343;Mozilla/5.0 (Linux; Android 10; JEF-AN00 Build/HUAWEIJEF-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.106 Mobile Safari/537.36")
                .header("accept", "*/*")
                .header("x-requested-with", "com.jd.pingou")
                .header("sec-fetch-site", "same-site")
                .header("sec-fetch-mode", "no-cors")
                .header("sec-fetch-dest", "script")
                .header("referer", "https://st.jingxi.com/pingou/dream_factory/index.html?sceneval=2&ptag=7155.9.46")
                .header("accept-encoding", "gzip, deflate")
                .header("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("cookie", "pt_key=AAJhMQSFAEDpKABMNbc_OnIYLPXvxZJ742n5QPFXRtkm0c2Hi0DYHFN7VXDJX1IHNIITB-viic0sL5pk_UN3OUShqTOzCDxb;pt_pin=%E6%80%AA%E7%9B%97%E5%9F%BA%E5%BE%B78768611;")
                .asString();

        InputStream rawBody = response.getRawBody();
        String body = response.getBody();
        int i = body.indexOf("(");
        int i1 = body.indexOf(")");
        String substring = body.substring(i+1, i1);
        Object parse = JSONObject.parseObject(substring);

        System.out.println(response);
    }

}