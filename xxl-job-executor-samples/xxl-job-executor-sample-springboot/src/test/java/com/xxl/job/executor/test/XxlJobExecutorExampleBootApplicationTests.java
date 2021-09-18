package com.xxl.job.executor.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.xxl.job.executor.po.JxFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XxlJobExecutorExampleBootApplicationTests {

    @Test
    public void test() throws UnirestException {
        Date date = new Date();
        String formatStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sssss'.Z'").format(date);
        System.err.println(formatStr);

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("https://wq.jd.com/user/info/QueryJDUserInfo?sceneval=2")
                .header("user-agent", "jdpingou;android;5.5.0;10;193f8fbac9948c74;network/UNKNOWN;model/JEF-AN00;appBuild/18299;partner/huawei01;;session/73;aid/193f8fbac9948c74;oaid/1cad52a0-a01e-4a4b-96ac-4a734a49e3e6;pap/JA2019_3111789;brand/HUAWEI;eu/1393336683662616;fv/3693934383367343;Mozilla/5.0 (Linux; Android 10; JEF-AN00 Build/HUAWEIJEF-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.106 Mobile Safari/537.36")
                .header("Accept","application/json,text/plain, */*" )
                .header("Content-Type","application/x-www-form-urlencoded" )
                .header("Accept-Encoding","gzip, deflate, br" )
                .header("Accept-Language","zh-cn" )
                .header("Connection","keep-alive" )
                .header("Cookie","pt_key=AAJhQEfuADCnjcl3yHkyhCTSylvkHv4JlA62JrG3uOLxHgcIj6OCUgtfuH7dgZHPuDwuso3blO0;pt_pin=jd_6784f6c82972a;" )
                .header("Referer","https://wqs.jd.com/my/jingdou/my.shtml?sceneval=2" )
                .asString();

        InputStream rawBody = response.getRawBody();
        String body = response.getBody();
        JSONObject parse1 = JSONObject.parseObject(body);
        int i = body.indexOf("window._CONFIG = ");
        int i2 = body.indexOf(" ;var __getImgUrl");
        String substring = body.substring(i + "window._CONFIG = ".length(), i2);
        JSONArray parse = JSONArray.parseArray(substring);

        JSONArray skinConfig = parse.getJSONObject(0).getJSONArray("skinConfig");
        List<JxFactory> jxFactories = skinConfig.toJavaList(JxFactory.class);
        jxFactories.forEach(jxFactory -> {
            if (jxFactory.getLink() != null/* && jxFactory.getStart() != null && jxFactory.getEnd() != null*/) {
                System.out.println(jxFactory.getActiveId());
                System.out.println(jxFactory.getLink());
                System.out.println(jxFactory.getEnd());
            }
        });


    }

    @Test
    public void extracted() {
        System.out.println(generateFp());
    }

    private long generateFp() {
        while (true) {
            long numb = (long) (Math.random() * 100000000 * 1000000); // had to use this as int's are to small for a 13 digit number.
            if (String.valueOf(numb).length() == 13) {
                System.out.println(numb);
                return numb;
            }
        }
    }
}