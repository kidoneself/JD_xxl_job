package com.xxl.job.executor.core;

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JSONTree {

    public static JSONNode createJSONTree(Object nodeData, String nodeName, String nodePath, int level) {
        JSONNode node = new JSONNode();
        node.setNodeName(nodeName);
        node.setNodePath(nodePath);
        node.setLevel(level);
        node.setData(nodeData);

        if (nodeData == null) {
            node.setDataType(null);
            return node;
        }

        List<JSONNode> childrenList = new LinkedList<>();

        if (nodeData instanceof JSONObject) {
            node.setDataType("Object");


            JSONObject jsonObject = (JSONObject) nodeData;
            Set<String> keySet = jsonObject.keySet();

            level++;
            for (String key : keySet) {
                JSONNode childNode = createJSONTree(jsonObject.get(key), key, nodePath + "/" + key, level);
                childrenList.add(childNode);
            }

            node.setChildren(childrenList);
        } else if (nodeData instanceof JSONArray) {
            node.setDataType("Array");

            JSONArray jsonArray = (JSONArray) nodeData;

            for (int index = 0, size = jsonArray.size(); index < size; index++) {
                // Array 元素，不是单个节点；所以将元素下一级的孩子链表整个作为 Array 的孩子链表
                JSONNode childNode = createJSONTree(jsonArray.get(index), nodeName, nodePath + "[" + index + "]", level);
                if (childNode.getChildren() != null) {
                    childrenList.addAll(childNode.getChildren());
                }
            }

            node.setChildren(childrenList);
        } else {
            node.setChildren(null);
            node.setDataType(nodeData.getClass().getName());
        }

        return node;
    }

    public static List<JSONNode> levelTraversal(JSONNode rootNode) {
        if (rootNode == null) {
            return null;
        }

        Queue<JSONNode> queue = new ConcurrentLinkedQueue<>();
        queue.add(rootNode);

        List<JSONNode> nodeList = new LinkedList<>();

        while (!queue.isEmpty()) {
            JSONNode node = queue.poll();
            nodeList.add(node);

            if (node != null) {
                if (node.getChildren() != null) {
                    queue.addAll(node.getChildren());
                }
            }
        }

        return nodeList;
    }

    public static List<JSONNode> depthFirstTraversal(JSONNode rootNode) {
        if (rootNode == null) {
            return null;
        }

        Stack<JSONNode> stack = new Stack<>();
        stack.push(rootNode);

        List<JSONNode> nodeList = new LinkedList<>();

        while (!stack.isEmpty()) {
            JSONNode node = stack.pop();
            nodeList.add(node);

            if (node == null || node.getChildren() == null) {
                continue;
            }

            List<JSONNode> children = node.getChildren();

            for (int index = children.size() - 1; index >= 0; index--) {
                stack.push(children.get(index));
            }
        }

        return nodeList;
    }

    public static Multimap<String, Object> jsonToMap(String data) {
        JSONObject jsonObject = JSONObject.parseObject(data, JSONObject.class, Feature.OrderedField);
        JSONNode root = JSONTree.createJSONTree(jsonObject, "root", "#", 0);
        List<JSONNode> list = JSONTree.depthFirstTraversal(root);
        Multimap<String, Object> map = ArrayListMultimap.create();

        for (JSONNode jsonNode : list) {
//            System.out.printf("%" + (jsonNode.getLevel() * 4 + 1) + "s" + "%1$s%2$s%n", " ", jsonNode.getLevel() + "--" + jsonNode.getNodePath());
            if (jsonNode.getChildren() == null) {
                map.put(jsonNode.getNodeName(), jsonNode.getData());
            }
        }
        return map;
    }
    public static Multimap<String, Object> jsonToMap(JSONObject jsonObject) {
        JSONNode root = JSONTree.createJSONTree(jsonObject, "root", "#", 0);
        List<JSONNode> list = JSONTree.depthFirstTraversal(root);
        Multimap<String, Object> map = ArrayListMultimap.create();

        for (JSONNode jsonNode : list) {
//            System.out.printf("%" + (jsonNode.getLevel() * 4 + 1) + "s" + "%1$s%2$s%n", " ", jsonNode.getLevel() + "--" + jsonNode.getNodePath());
            if (jsonNode.getChildren() == null) {
                map.put(jsonNode.getNodeName(), jsonNode.getData());
            }
        }
        return map;
    }

    public static HashMap<String, Object> jsonToHashMap(JSONObject jsonObject) {
        JSONNode root = JSONTree.createJSONTree(jsonObject, "root", "#", 0);
        List<JSONNode> list = JSONTree.depthFirstTraversal(root);
        HashMap<String, Object> map = new HashMap<>();

        for (JSONNode jsonNode : list) {
//            System.out.printf("%" + (jsonNode.getLevel() * 4 + 1) + "s" + "%1$s%2$s%n", " ", jsonNode.getLevel() + "--" + jsonNode.getNodePath());
            if (jsonNode.getChildren() == null) {
                map.put(jsonNode.getNodeName(), jsonNode.getData());
            }
        }
        return map;
    }



    public static void main(String[] args) {
        String data = "{\"code\":\"0\",\"data\":{\"code\":\"0\",\"score\":\"5\",\"maxTimes\":4,\"times\":2,\"bizMsg\":\"完成任务，获得+5成长值\",\"bizCode\":\"0\",\"taskStatus\":1,\"growthResult\":{\"sceneLevelConfig\":{\"beanNum\":2,\"growthEnd\":200,\"role\":\"https://storage.360buyimg.com/ljd-source/ljd_lottie/IP_21_xizhuang.json\",\"roleStatic\":\"https://m.360buyimg.com/babel/jfs/t1/163024/15/4151/28158/6010e5daE0385c312/a04003a82974742d.png\",\"backgroundMain\":\"https://m.360buyimg.com/babel/jfs/t1/161489/12/4359/445796/600eb056E8ceb151d/78b5a8b1c504f927.png\",\"backgroundShowcase\":\"https://m.360buyimg.com/babel/jfs/t1/152118/24/16667/251390/6013d6dcEa888879a/1aaa8d3a99a6ffa9.png\",\"farmImg\":\"https://m.360buyimg.com/babel/jfs/t1/171121/23/5859/14784/601f8354E81806d9a/5d712e82d6876b88.png\",\"interaction\":\"https://m.360buyimg.com/babel/jfs/t1/171206/5/2165/11502/5ffbf9a7E8954a903/859b3b73e5dbc087.jpg\",\"growthStart\":50,\"signAddGrowth\":10,\"sketch\":\"https://m.360buyimg.com/babel/jfs/t1/161282/15/4900/45725/6013b849E5977cd07/b5ec9d2254671928.png\",\"backgroundMainNew\":\"https://m.360buyimg.com/babel/jfs/t1/185483/29/17521/98119/61091265E6d8e0e1f/3bd94139d06bca9a.jpg\"},\"level\":2,\"levelUp\":true,\"growth\":51,\"beanSent\":true,\"addedGrowth\":5}}}\n";
        jsonToMap(data).forEach((k, v) -> {
            System.out.println(k + "===" + v);


        });

    }
}