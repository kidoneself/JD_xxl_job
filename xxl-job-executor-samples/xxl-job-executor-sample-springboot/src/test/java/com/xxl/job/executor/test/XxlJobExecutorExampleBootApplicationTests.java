package com.xxl.job.executor.test;

import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.xxl.job.executor.core.HeaderUtil;
import com.xxl.job.executor.core.JDBodyParam;
import com.xxl.job.executor.po.ShakeList;
import com.xxl.job.executor.po.TaskItemsItem;
import com.xxl.job.executor.po.joy.CrazyJoyUserInfo;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Cookie;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XxlJobExecutorExampleBootApplicationTests {

    @Test
    public void test() throws UnirestException, URISyntaxException {
//
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Host", "api.m.jd.com");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Pragma", "no-cache");
        headerMap.put("Cache-Control", "no-cache");
        headerMap.put("Sec-Fetch-Site", "same-site");
        headerMap.put("Origin", "https://spa.jd.com");
        headerMap.put("Sec-Fetch-Mode", "cors");
        headerMap.put("Sec-Fetch-Dest", "empty");
        headerMap.put("Accept", "application/json");
        headerMap.put("Referer", "https://spa.jd.com/");
        headerMap.put("Accept-Encoding", "gzip, deflate, br");
        headerMap.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        headerMap.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
        headerMap.put("cookie", "pt_key=AAJhRyK1ADCfaMLMkUA96laOm1_845DZqAuxdaP7mSbEeNfmuQoM2kItc-La3dm18Mb9e37nJ1w;pt_pin=wdlLxrYZBojiba;");

        //1.??????httpclient????????????????????????????????????
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            URI uri = new URI("https://api.m.jd.com/?t=1632064755196&appid=vip_h5&functionId=vvipclub_lotteryTask&body=%7B%22info%22:%22browseTask%22,%22withItem%22:true%7D");
            HttpGet request = new HttpGet(uri);
            Header[] headers = HeaderUtil.convertHeader(headerMap);
            request.setHeaders(headers);
            response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            String shakeListRes = EntityUtils.toString(httpEntity, "utf-8");
            JSONObject shakeListObj = JSONObject.parseObject(shakeListRes);
            ShakeList shakeList1 = JSONObject.toJavaObject(shakeListObj, ShakeList.class);
            List<TaskItemsItem> sharkLists = shakeList1.getData().get(0).getTaskItems();

            sharkLists.forEach(sharkList -> {
                if (!sharkList.isFinish()) {
                    String body = new JDBodyParam()
                            .keyMark("taskName").valueMark("browseTask")
                            .keyMark("taskItemId").value(sharkList.getId()).
                            buildBody();


                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //6.??????
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }

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


    public String doGet(String url, Map<String, String> headersMap) {
        //1.??????httpclient????????????????????????????????????
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpGet request = new HttpGet(url);
            Header[] headers = HeaderUtil.convertHeader(headersMap);
            request.setHeaders(headers);
            response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            return EntityUtils.toString(httpEntity, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //6.??????
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return null;
    }

    @Test
    public void buildUrl() throws InterruptedException, ScriptException {
        String str = "function hashCode(pin) {" +
                "    let i = 0x0;" +
                "    let k = (pin += '').length;" +
                "    let x = 0x0;" +
                "    let y = 0x0;" +
                "    for (; y < k; y++)" +
                "        (0x7fffffff < (i = 0x1f * i + pin.charCodeAt(x++)) || i < 0x80000000) && (i &= 0xffffffff);" +
                "    return Math.abs(i);" +
                "}";

        ScriptEngineManager manager = new ScriptEngineManager();
        // 1.????????????JavaScript ????????????
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(str);
        if (engine instanceof Invocable) {
            Invocable invocable = (Invocable) engine;
//            // 5.????????????????????????JavaScriptInterface????????????
//            JavaScriptInterface executeMethod = invocable
//                    .getInterface(JavaScriptInterface.class);
//            // 6.????????????js??????(????????????js??????????????????????????????)
//            String info = executeMethod.sayHello(param);
//            System.out.println(info);
        }
    }


    static Cookie[] resolveCookies(String s) {
        ArrayList<Cookie> cookies = new ArrayList<Cookie>();
        while (s.length() != 0) {
            int index = s.indexOf('=');
            if (index == -1)
                break;
            String cookieName = s.substring(0, index).trim();
            int endIndex = s.indexOf(';');
            if (endIndex == -1) {
                endIndex = s.length();
                String cookieValue = s.substring(index + 1, endIndex).trim();
                cookies.add(new Cookie(cookieName, cookieValue));
                break;
            }
            String cookieValue = s.substring(index + 1, endIndex).trim();
            cookies.add(new Cookie(cookieName, cookieValue));
            s = s.substring(endIndex + 1);
        }
        return cookies.toArray(new Cookie[0]);
    }

    @Test
    public void aaa() throws InterruptedException {
        HashMap<String, String> header = new HashMap<>();

        header.put("Accept", "application/json, text/plain, */*");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Accept-language", "en,en-US;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        header.put("cache-control", "no-cache");
        header.put("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        header.put("origin", "https://crazy-joy.jd.com");
        header.put("pragma", "no-cache");
        header.put("referer", "https://crazy-joy.jd.com/");
        header.put("sec-fetch-dest", "empty");
        header.put("sec-fetch-mode", "cors");
        header.put("sec-fetch-site", "same-site");
        header.put("user-agent", "jdapp;iPhone;9.3.2;15.0;f061695d1c03fef9fdd71adf54dbf28ee19a0d61;network/wifi;supportApplePay/0;hasUPPay/0;hasOCPay/0;model/iPhone8,2;addressid/6484220720;supportBestPay/0;appBuild/167490;pushNoticeIsOpen/0;jdSupportDarkMode/0;pv/11.26;apprpd/JingDou_Detail;ref/JingDou_Detail_Contrller;psq/9;ads/;psn/f061695d1c03fef9fdd71adf54dbf28ee19a0d61|99;jdv/0|direct|-|none|-|1632830119936|1633350768;adk/;app_device/IOS;pap/JA2015_311210|9.3.2|IOS 15.0;Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1");
        header.put("Cookie", "pt_key=AAJhaTS-ADCliNk7pJWSLb6cR_pgOL8pEuidcBMgH9dNRV4vIqLu8tGCBopz4fJQrimmJ7yyA3c;pt_pin=xly8380;");
        long t = System.currentTimeMillis();
        String url = String.format("https://api.m.jd.com/?uts=139619872204153067%s&appid=crazy_joy&functionId=crazyJoy_joy_produce&t=%s", t, t);
        String body = HttpRequest.get(url).addHeaders(header).execute().body();
        JSONObject data = JSONObject.parseObject(body);
        Console.log(data);
        String string = data.getJSONObject("data").getString("luckyBoxRecordId");
        if (string != null) {
            String body1 = new JDBodyParam().keyMark("eventType").valueMark("LUCKY_BOX_DROP")
                    .keyMark("eventRecordId").valueMark(string)
                    .keyMark("eventPopupClick").value(0).buildBody();
            long t2 = System.currentTimeMillis();
            String look = String.format("https://api.m.jd.com/?body=%s&uts=139619872204153067%s&appid=crazy_joy&functionId=crazyJoy_event_getVideoAdvert&t=%s", body1, t2, t2);
            String body2 = HttpRequest.get(look).addHeaders(header).execute().body();
            JSONObject data2 = JSONObject.parseObject(body2);
            Console.log(data2);
            if (data2.getBoolean("success")) {
                Thread.sleep(35000);
                String body3 = new JDBodyParam().keyMark("eventType").valueMark("LUCKY_BOX_DROP")
                        .keyMark("eventRecordId").valueMark(string)
                        .buildBody();
                long t3 = System.currentTimeMillis();
                String url3 = String.format("https://api.m.jd.com/?body=%s&uts=139619872204153067%s&appid=crazy_joy&functionId=crazyJoy_event_obtainAward&t=%s", body3, t3, t3);
                String body4 = HttpRequest.get(url3).addHeaders(header).execute().body();
                JSONObject data4 = JSONObject.parseObject(body4);
                Console.log(data4);
            }
        }
    }

    @Test
    public void bbb() throws InterruptedException {
        //https://api.m.jd.com/?body=%7B%22operateType%22:%22MERGE%22,%22fromBoxIndex%22:6,%22targetBoxIndex%22:7%7D&uts=4747238722041530671634294770988&appid=crazy_joy&functionId=crazyJoy_joy_moveOrMerge&t=1634294770988
        // ?????????
        HashMap<String, String> header = new HashMap<>();
        header.put("Accept", "application/json, text/plain, */*");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Accept-language", "en,en-US;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        header.put("cache-control", "no-cache");
        header.put("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        header.put("origin", "https://crazy-joy.jd.com");
        header.put("pragma", "no-cache");
        header.put("referer", "https://crazy-joy.jd.com/");
        header.put("sec-fetch-dest", "empty");
        header.put("sec-fetch-mode", "cors");
        header.put("sec-fetch-site", "same-site");
        header.put("user-agent", "jdapp;iPhone;9.3.2;15.0;f061695d1c03fef9fdd71adf54dbf28ee19a0d61;network/wifi;supportApplePay/0;hasUPPay/0;hasOCPay/0;model/iPhone8,2;addressid/6484220720;supportBestPay/0;appBuild/167490;pushNoticeIsOpen/0;jdSupportDarkMode/0;pv/11.26;apprpd/JingDou_Detail;ref/JingDou_Detail_Contrller;psq/9;ads/;psn/f061695d1c03fef9fdd71adf54dbf28ee19a0d61|99;jdv/0|direct|-|none|-|1632830119936|1633350768;adk/;app_device/IOS;pap/JA2015_311210|9.3.2|IOS 15.0;Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1");
        header.put("Cookie", "pt_key=AAJhaTS-ADCliNk7pJWSLb6cR_pgOL8pEuidcBMgH9dNRV4vIqLu8tGCBopz4fJQrimmJ7yyA3c;pt_pin=xly8380;");
        long t = System.currentTimeMillis();
        //MERGE??????  MOVE?????? fromBoxIndex?????????  targetBoxIndex????????????
        String body1 = new JDBodyParam().keyMark("operateType").valueMark("MOVE")
                .keyMark("fromBoxIndex").value(11)
                .keyMark("targetBoxIndex").value(0).buildBody();
        String url = String.format("https://api.m.jd.com/?body=%s&uts=033143662204153067%s&appid=crazy_joy&functionId=crazyJoy_joy_moveOrMerge&t=%s", body1, t, t);
        String body = HttpRequest.get(url).addHeaders(header).execute().body();
        JSONObject data = JSONObject.parseObject(body);
        Console.log(data);
    }

    @Test
    public void crazyJoy() {
        // ?????????
        HashMap<String, String> header = new HashMap<>();
        String cookie = "pt_key=AAJhaso1ADBvKlGz7jRBQuEE_rXksIqf2w1JW9cXQ4yY_FUhcwXDApdkSMpQrULVoWlnJU3Z0qo;pt_pin=jd_sTaLJiRwioAy;";
        String[] split = cookie.split(";");
        BigInteger userHash = userHash(split[1].split("=")[1]);
        header.put("Accept", "application/json, text/plain, */*");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Accept-language", "en,en-US;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        header.put("cache-control", "no-cache");
        header.put("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        header.put("origin", "https://crazy-joy.jd.com");
        header.put("pragma", "no-cache");
        header.put("referer", "https://crazy-joy.jd.com/");
        header.put("sec-fetch-dest", "empty");
        header.put("sec-fetch-mode", "cors");
        header.put("sec-fetch-site", "same-site");
        header.put("user-agent", "jdapp;iPhone;9.3.2;15.0;f061695d1c03fef9fdd71adf54dbf28ee19a0d61;network/wifi;supportApplePay/0;hasUPPay/0;hasOCPay/0;model/iPhone8,2;addressid/6484220720;supportBestPay/0;appBuild/167490;pushNoticeIsOpen/0;jdSupportDarkMode/0;pv/11.26;apprpd/JingDou_Detail;ref/JingDou_Detail_Contrller;psq/9;ads/;psn/f061695d1c03fef9fdd71adf54dbf28ee19a0d61|99;jdv/0|direct|-|none|-|1632830119936|1633350768;adk/;app_device/IOS;pap/JA2015_311210|9.3.2|IOS 15.0;Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1");
        header.put("Cookie", cookie);
        // ????????????joy??????
        // ?????????????????? {"paramData":{"inviter":"TgIbftRzDvE3dqENU9J5-sUekYnhBBIg"}}
        String paramData = new JDBodyParam().keyMark("inviter").valueMark("TgIbftRzDvE3dqENU9J5-sUekYnhBBIg").buildBody();
        String userInfoBody = new JDBodyParam().keyMark("paramData").valueMark(paramData).buildBody();
        long t = System.currentTimeMillis();
        String userInfoUrl = String.format("https://api.m.jd.com/?body=%s&uts=01222367%s%s&appid=crazy_joy&functionId=crazyJoy_user_gameState&t=%s", userInfoBody, userHash, t, t);
        String res = HttpRequest.get(userInfoUrl).addHeaders(header).execute().body();
        JSONObject userInfo = JSONObject.parseObject(res);
        Console.log(userInfo);
        if (!userInfo.getBoolean("success")) {
            Console.log(userInfo.getString("officialDocument"));
        }
        CrazyJoyUserInfo joyUserInfo = userInfo.getJSONObject("data").toJavaObject(CrazyJoyUserInfo.class);
        //https://api.m.jd.com/?uts=8311312122041530671634371656251&appid=crazy_joy&functionId=crazyJoy_joy_produce&t=1634371656251

    }


    private BigInteger userHash(String pin) {
        String key = "7!^h&bj#cf$fe&%$8aj6";
        return hashCode(pin).add(hashCode(key));
    }

    public BigInteger hashCode(String value) {
        int sum = 0;
        char[] chars = value.toCharArray();
        for (char aChar : chars) {
            sum = sum * 31 + (int) aChar;
            sum &= 0xffffffff;
        }
        return BigInteger.valueOf(Math.abs(sum));
    }
}