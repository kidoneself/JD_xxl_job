package com.xxl.job.executor.service.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.executor.core.HttpInstanceFactory;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 跨平台Http任务
 *
 * @author xuxueli 2018-09-16 03:48:34
 */
@JobHandler(value = "httpJobHandler")
@Component
public class HttpJobHandler extends IJobHandler {

    private List<Env> envs;




    @Override
    public ReturnT<String> execute(String param) throws Exception {
        Header[] selfHeaders = {
                // new BasicHeader("cookie", "pt_key=AAJhMQSFAEDpKABMNbc_OnIYLPXvxZJ742n5QPFXRtkm0c2Hi0DYHFN7VXDJX1IHNIITB-viic0sL5pk_UN3OUShqTOzCDxb;pt_pin=%E6%80%AA%E7%9B%97%E5%9F%BA%E5%BE%B78768611;"),
                // new BasicHeader("cookie", "pt_key=AAJhMjvJADCPtNcnXO-PewkiuqdAU6rTZrshmCYTPOZeX7dNmqCZZ54otfR0STiU2O_vjs9MiDk;pt_pin=jd_511851479ac61;"),
                // new BasicHeader("cookie", "pt_key=AAJhMZy6ADB2W7xRfeTHfOY3vM9J7q7VzgdZYObxTGDYsBxR7YV2A8RzJrwnGRwCGnH1eK9W9oA;pt_pin=jd_PUDaNSWCqtZp;"),
                new BasicHeader("cookie", "pt_key=AAJhMoazADDzGgTvjoaytT-Ibu0KB4X58qdZwuAZo44PygrREezoKubrGzKMXlm7OS1-VnAbuhM;pt_pin=jd_trbybgrVjMqE;"),
                new BasicHeader("User-Agent",
                        "jdapp;android;10.0.2;10;network/wifi;Mozilla/5.0 (Linux; Android 10; GM1910 Build/QKQ1.190716.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36")
        };
        HttpInstanceFactory.HttpInstance instance = HttpInstanceFactory.getInstance();
        instance.setSelfHeaders(selfHeaders);
        JDUser userInfo = instance.getUserInfo();
        System.out.println(userInfo);
        return SUCCESS;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

}
