package com.xxl.job.executor.test;

import com.alibaba.fastjson.JSONArray;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XxlJobExecutorExampleBootApplicationTests {

    @Test
    public void test() throws UnirestException {
        Date date = new Date();
        String formatStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sssss'.Z'").format(date);
        System.err.println(formatStr);

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("https://pro.m.jd.com/mall/active/2kmaPNrGDNYo1LKwYtRoaSmsgbj6/index.html?babelChannel=dongdongnongchang")
                .header("user-agent", "jdapp;android;10.1.2;10;1393336683662616-3693934383367343;network/4g;model/JEF-AN00;addressid/4168482853;aid/193f8fbac9948c74;oaid/1cad52a0-a01e-4a4b-96ac-4a734a49e3e6;osVer/29;appBuild/89760;partner/huaweiharmony;eufv/1;jdSupportDarkMode/0;Mozilla/5.0 (Linux; Android 10; JEF-AN00 Build/HUAWEIJEF-AN00; wv) AppleWebKit/537.36 (KHTML")
                .header("accept", "*/*")
                .header("Host", "api.m.jd.com")
                .header("sec-fetch-site", "same-site")
                .header("sec-fetch-mode", "no-cors")
                .header("sec-fetch-dest", "script")
                .header("referer", "https://carry.m.jd.com/babelDiy/Zeus/3KSjXqQabiTuD1cJ28QskrpWoBKT/index.html?babelChannel=121&lng=121.463611&lat=31.021696&sid=5ff1f498bb1025bac5c96263ecafc15w&un_area=2_2813_61130_0")
                .header("accept-encoding", "gzip, deflate")
                .header("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("cookie", "pt_key=AAJhMQSFAEDpKABMNbc_OnIYLPXvxZJ742n5QPFXRtkm0c2Hi0DYHFN7VXDJX1IHNIITB-viic0sL5pk_UN3OUShqTOzCDxb;pt_pin=%E6%80%AA%E7%9B%97%E5%9F%BA%E5%BE%B78768611;")
                .asString();

        String body = response.getBody();

        System.out.println(response);
    }

}