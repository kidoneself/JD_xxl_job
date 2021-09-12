package com.xxl.job.executor.core;

import com.alibaba.fastjson.JSONObject;

import java.util.function.BiConsumer;

/**
 * Jackson util
 * <p>
 * 1、obj need private and set/get；
 * 2、do not support inner class；
 *
 * @author xuxueli 2015-9-25 18:02:56
 */
public class JacksonUtil {


    /**
     * bean、array、List、Map --> json
     *
     * @param obj
     * @return json string
     * @throws Exception
     */
    public static void writeValueAsString(JSONObject obj) {
        obj.forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String s, Object o) {
                if (o != null) {
                    if (o instanceof JSONObject) {
                        ((JSONObject) o).forEach(new BiConsumer<String, Object>() {
                            @Override
                            public void accept(String s, Object o) {
                                System.out.println("---- private " + o.getClass() + " " + s + ";");
                            }
                        });
                    }
                    System.out.println("private " + o.getClass() + " " + s + ";");
                } else {
                    System.out.println("private Object" + " " + s + ";");
                }

            }
        });


    }


}
