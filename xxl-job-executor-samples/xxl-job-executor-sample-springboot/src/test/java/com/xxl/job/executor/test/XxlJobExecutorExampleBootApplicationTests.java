package com.xxl.job.executor.test;

import com.alibaba.fastjson.JSONArray;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class XxlJobExecutorExampleBootApplicationTests {

    @Test
    public void test() throws UnirestException {
        Date date = new Date();
        String formatStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sssss'.Z'").format(date);
        System.err.println(formatStr);

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get(/*"https://wqsd.jd.com/pingou/dream_factory/index.html?" + */"https://wqsd.jd.com/pingou/dream_factory/index.html?Fri Sep 17 2021 02:28:28 GMT+0800 (中国标准时间)")
                .header("user-agent", "jdpingou;android;5.5.0;10;193f8fbac9948c74;network/UNKNOWN;model/JEF-AN00;appBuild/18299;partner/huawei01;;session/73;aid/193f8fbac9948c74;oaid/1cad52a0-a01e-4a4b-96ac-4a734a49e3e6;pap/JA2019_3111789;brand/HUAWEI;eu/1393336683662616;fv/3693934383367343;Mozilla/5.0 (Linux; Android 10; JEF-AN00 Build/HUAWEIJEF-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.106 Mobile Safari/537.36")
                .asString();


        InputStream rawBody = response.getRawBody();
        String body = response.getBody();
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

}