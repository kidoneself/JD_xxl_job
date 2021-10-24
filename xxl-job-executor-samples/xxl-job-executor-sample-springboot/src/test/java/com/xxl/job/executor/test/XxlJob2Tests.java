package com.xxl.job.executor.test;

import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.xxl.job.executor.core.HeaderUtil;
import com.xxl.job.executor.core.JDBodyParam;
import com.xxl.job.executor.po.ShakeList;
import com.xxl.job.executor.po.TaskItemsItem;
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
public class XxlJob2Tests {

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

        //1.生成httpclient，相当于该打开一个浏览器
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
            //6.关闭
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
        //1.生成httpclient，相当于该打开一个浏览器
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
            //6.关闭
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
        // 1.获取一个JavaScript 引擎实例
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(str);
        if (engine instanceof Invocable) {
            Invocable invocable = (Invocable) engine;
//            // 5.从脚本引擎中获取JavaScriptInterface接口对象
//            JavaScriptInterface executeMethod = invocable
//                    .getInterface(JavaScriptInterface.class);
//            // 6.调用这个js接口(会去调用js中的相匹配名称的函数)
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
        // 请求头
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
        //MERGE合并  MOVE移动 fromBoxIndex原位置  targetBoxIndex目标位置
        String body1 = new JDBodyParam().keyMark("operateType").valueMark("MOVE")
                .keyMark("fromBoxIndex").value(11)
                .keyMark("targetBoxIndex").value(0).buildBody();
        String url = String.format("https://api.m.jd.com/?body=%s&uts=033143662204153067%s&appid=crazy_joy&functionId=crazyJoy_joy_moveOrMerge&t=%s", body1, t, t);
        String body = HttpRequest.get(url).addHeaders(header).execute().body();
        JSONObject data = JSONObject.parseObject(body);
        Console.log(data);
    }

    @Test
    public void crazyJoy() throws InterruptedException {
        // 请求头
        HashMap<String, String> headerMap = new HashMap<>();
        String cookie = "pt_key=AAJhcBRFAEC1GZPhe_glM9aFvUXxfdA4YBT92fDfQO-rp79qeSjGc6nDJPvS3_QHJ7G67imhcYO_ERLFMaTGw6NbxLempAhz;pt_pin=%E6%80%AA%E7%9B%97%E5%9F%BA%E5%BE%B78768611;";
//        String cookie = "pt_key=AAJhRpxBADAG94bqeqBcUBvNcQBBN_MSHD4NVkj8L1MNAbTEUoXZFIsv_a51n0iHNSh8Hu8TTR0;pt_pin=jd_76a0e2fd23b87;";
//        String cookie = "pt_key=AAJhaP4rADAmnT3ilQLemA8lhDza2Zz2lLZ51hY-6FgH8YeZAQwObIq2393k0JWeCvv9L4jmhPo;pt_pin=jd_64e40afd28100;";
//        String cookie = "pt_key=AAJhYrKVADA44yyPAW5PePiNxVP7vn6R4hSKcK4t3roTVG2IlSdw7O-uxnyfHNDhLKxyyLxlPB8;pt_pin=jd_PUDaNSWCqtZp;";
//        String cookie = "pt_key=AAJhWvJqADAXfG6UICWRLAarp-yEan1m_zMDvle4C4XMGaRY4w0nqFfYPXG0S9vqeLCfJwM4vIs; pt_pin=jd_hzktNtNEmMeI;";

        headerMap.put("Host", "api.m.jd.com");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Content-Length", "325");
        headerMap.put("Cache-Control", "no-cache");
        headerMap.put("X-Requested-With", "com.jingdong.app.mall");
        headerMap.put("Sec-Fetch-Site", "same-site");
        headerMap.put("Origin", "https://wbbny.m.jd.com");
        headerMap.put("Sec-Fetch-Mode", "cors");
        headerMap.put("Sec-Fetch-Dest", "empty");
        headerMap.put("Accept", "application/json, text/plain, */*");
        headerMap.put("Referer", "https://wbbny.m.jd.com");
        headerMap.put("Accept-Encoding", "gzip, deflate, br");
        headerMap.put("User-Agent", "jdapp;android;10.0.8;11;5623365333831666-4653932693569383;network/wifi;model/M2011K2C;addressid/4168482853;aid/e2c538afd59b9e98;oaid/9915dd7d6bf00ab4;osVer/30;appBuild/89053;partner/xiaomi001;eufv/1;jdSupportDarkMode/0;Mozilla/5.0 (Linux; Android 11; M2011K2C Build/RKQ1.200928.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045713 Mobile Safari/537.36");
        headerMap.put("Cookie", cookie);
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        // 获取用户joy信息
        String taskUrl = "https://api.m.jd.com/client.action?functionId=travel_getTaskDetail&body=%7B%7D&client=wh5&clientVersion=1.0.0";
        String res = HttpRequest.post(taskUrl).addHeaders(headerMap).execute().body();
        JSONObject taskInfo = JSONObject.parseObject(res);
        Console.log(taskInfo);
        if (taskInfo.getInteger("code") != 0) {
            Console.log(taskInfo.getString("msg"));
        }
        JSONObject result = taskInfo.getJSONObject("data").getJSONObject("result");
        Console.log("互助：" + result.getString("inviteId"));
        Console.log("支线任务：" + result.getJSONArray("lotteryTaskVos"));
        Console.log("主任务：" + result.getJSONArray("taskVos"));
        Console.log("当前金币：" + result.getString("userScore"));
        JSONArray taskVos = result.getJSONArray("taskVos");
        //functionId=travel_collectScore&body={"ss":"{\"extraData\":{\"log\":\"-1\",\"sceneid\":\"HYGJZYh5\"},\"secretp\":\"n1Rk4yJykWoaKXpHOor_8CJTFU0UU-Q\",\"random\":\"63813569\"}"
        String extraData = new JDBodyParam().keyMark("log").valueMark("-1").keyMark("sceneid").valueMark("HYGJZYh5").keyMark("secretp").valueMark("n1Rk4yJykWoaKXpHOor_8CJTFU0UU").keyMark("random").valueMark("63813569").buildBody();
        String ss = new JDBodyParam().keyMark("ss").valueMark(extraData).keyMark("inviteId").valueMark("ZXASTT026aE3pl7W-IPtd9ZRznPAMfB38OQFjRWn6u7zB55awQ").buildBody();
        HashMap<String, String> headerMap2 = new HashMap<>();


        headerMap.put("Host", "api.m.jd.com");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Content-Length", "325");
        headerMap.put("Cache-Control", "no-cache");
        headerMap.put("X-Requested-With", "com.jingdong.app.mall");
        headerMap.put("Sec-Fetch-Site", "same-site");
        headerMap.put("Origin", "https://wbbny.m.jd.com");
        headerMap.put("Sec-Fetch-Mode", "cors");
        headerMap.put("Sec-Fetch-Dest", "empty");
        headerMap.put("Accept", "application/json, text/plain, */*");
        headerMap.put("Referer", "https://wbbny.m.jd.com/babelDiy/Zeus/2vVU4E7JLH9gKYfLQ5EVW6eN2P7B/index.html?babelChannel=jdappsyfc&shareType=taskHelp&inviteId=ZXASTT0205KkcGFRCsSqTXm-p6aJzFjRWn6u7zB55awQ&mpin=RnFnx29cOTXdz9RP--svF3ELW9rRogHyLN0&from=sc&lng=121.425217&lat=31.137725&sid=2a59a2b839c6f6997a174eebfde7474w&un_area=2_2813_61130_0");
        headerMap.put("Accept-Encoding", "gzip, deflate, br");
        headerMap.put("User-Agent", "jdapp;android;10.0.8;11;5623365333831666-4653932693569383;network/wifi;model/M2011K2C;addressid/4168482853;aid/e2c538afd59b9e98;oaid/9915dd7d6bf00ab4;osVer/30;appBuild/89053;partner/xiaomi001;eufv/1;jdSupportDarkMode/0;Mozilla/5.0 (Linux; Android 11; M2011K2C Build/RKQ1.200928.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045713 Mobile Safari/537.36");
        headerMap.put("Cookie", cookie);
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        String boo = "{\"ss\":\"{\\\"extraData\\\":{\\\"log\\\":\\\"-1\\\",\\\"sceneid\\\":\\\"HYGJZYh5\\\"},\\\"secretp\\\":\\\"n1Rk4yJykWoaKXpHOor_8CJTFU0UU-Q\\\",\\\"random\\\":\\\"63813569\\\"}\",\"inviteId\":\"ZXASTT026aE3pl7W-IPtd9ZRznPAMfB38OQFjRWn6u7zB55awQ\"}";
        String invite = String.format("https://api.m.jd.com/client.action?functionId=travel_collectScore&body=%s&client=wh5&clientVersion=1.0.0", boo);
        String inviteres = HttpRequest.post(invite).addHeaders(headerMap2).execute().body();
        JSONObject inviteInfo = JSONObject.parseObject(inviteres);
        Console.log(inviteInfo);
        for (Object taskVo : taskVos) {
            JSONObject task = (JSONObject) taskVo;
            Integer maxTimes = task.getInteger("maxTimes");
            Integer times = task.getInteger("times");
            if (times <= maxTimes) {
                Console.log(task.getString("taskFinishedCopy"));
            }
            Console.log("开始任务..." + task.getString("subTitleName"));
            JSONArray browseShopVos = task.getJSONArray("browseShopVo");
            if (browseShopVos==null) continue;

            for (Object browseShopVo : browseShopVos) {
                Thread.sleep(80000);
                JSONObject browseShop = (JSONObject) browseShopVo;
                String taskToken = browseShop.getString("taskToken");
                String shopId = browseShop.getString("shopId");
                //"functionId=followShop&body={"shopId":"10030672","follow":true,"type":"0"}&client=wh5&clientVersion=1.0.0
                String flowBody = new JDBodyParam().keyMark("shopId").valueMark(shopId).keyMark("follow").value(true).keyMark("type").valueMark(0).buildBody();
                String taskUrls = String.format("https://api.m.jd.com/client.action?functionId=followShop&body=%s&client=wh5&clientVersion=1.0.0", flowBody);
                String taskUrlsres = HttpRequest.post(taskUrls).addHeaders(headerMap).execute().body();
                JSONObject taskUrlsresInfo = JSONObject.parseObject(taskUrlsres);
                Console.log(taskUrlsresInfo);
//functionId=travel_collectScore&body={"ss":"{\"extraData\":{\"log\":\"-1\",\"sceneid\":\"HYGJZYh5\"},\"secretp\":\"n1Rk4yJykWoaKXpHOor_8CJTFU0UU-Q\",\"random\":\"63813569\"}"
// ,"inviteId":"ZXASTT0205KkcGFRCsSqTXm-p6aJzFjRWn6u7zB55awQ"}&client=wh5&clientVersion=1.0.0


            }

        }

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