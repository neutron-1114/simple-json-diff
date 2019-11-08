package utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author jinxLbj
 * @data 2019-07-19 18:27
 * @desc Json Diff工具
 **/

public class DiffJson {

    private static class DiffNode {
        private Object oldValue;
        private Object newValue;

        DiffNode(Object oldValue, Object newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public void setOldValue(Object oldValue) {
            this.oldValue = oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }

        public void setNewValue(Object newValue) {
            this.newValue = newValue;
        }

        @Override
        public String toString() {
            return oldValue + " -> " + newValue;
        }
    }

    public static Map<String, DiffNode> diff(String oldJson, String newJson) {
        JSONObject oldObj = JSONObject.parseObject(oldJson);
        JSONObject newObj = JSONObject.parseObject(newJson);
        Map<String, Object> oldJsonMap = new HashMap<>();
        Map<String, Object> newJsonMap = new HashMap<>();
        flatFormat2Map(oldObj, "", oldJsonMap);
        flatFormat2Map(newObj, "", newJsonMap);
        Map<String, DiffNode> diffNodeMap = compare(oldJsonMap, newJsonMap);
        return diffNodeMap;
    }

    private static Map<String, DiffNode> compare(Map<String, Object> oldMap, Map<String, Object> newMap) {
        Map<String, DiffNode> diff = new HashMap<>();
        List<String> keyset = new LinkedList<>();
        keyset.addAll(oldMap.keySet());
        keyset.addAll(newMap.keySet());
        keyset.forEach((x) -> {
            Object o = oldMap.get(x);
            Object o2 = newMap.get(x);
            if(o == null) {
                diff.put(x, new DiffNode(o, o2));
            }else {
                if(!o.equals(o2)) {
                    diff.put(x, new DiffNode(o, o2));
                }
            }
        });
        return diff;
    }

    public static String toStringDiffNode(Map<String, DiffNode> diffNodeMap) {
        if(diffNodeMap.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, DiffNode> nodes:
             diffNodeMap.entrySet()) {
            sb.append("参数 ");
            sb.append(nodes.getKey());
            sb.append(" 原值为 ");
            sb.append(nodes.getValue().getOldValue());
            sb.append(" 调整为 ");
            sb.append(nodes.getValue().getNewValue());
            sb.append("，");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    private static void flatFormat2Map(JSONObject object, String prefix, Map<String, Object> contain) {
        for(String key : object.keySet()) {
            Object o = object.get(key);
            if(o instanceof JSONObject) {
                flatFormat2Map((JSONObject) o, prefix + "." + key, contain);
            } else if (o instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) o;
                JSONObject jsonObject = new JSONObject();
                jsonArray2Object(jsonArray, key, jsonObject);
                flatFormat2Map(jsonObject, prefix, contain);
            } else {
                if(prefix.length() == 0) {
                    contain.put(key, o);
                }else {
                    if(prefix.substring(0, 1).equals(".")) {
                        String newprefix = prefix.substring(1);
                        contain.put(newprefix + "." + key, o);
                    }else {
                        contain.put(prefix + "." + key, o);
                    }
                }
            }

        }
    }

    private static void jsonArray2Object(JSONArray jsonArray, String prefix, JSONObject contain) {
        for (int i = 0; i < jsonArray.size(); i++) {
            Object o = jsonArray.get(i);
            if(o instanceof JSONObject) {
                contain.put(prefix + "[" + i + "]", o);
            } else if (o instanceof JSONArray) {
                jsonArray2Object((JSONArray) o, prefix + "[" + i + "]", contain);
            } else {
                contain.put(prefix + "[" + i + "]", o);
            }
        }
    }

    public static void main(String[] args) {
        DiffJson diffJson = new DiffJson();
        Map<String, DiffNode> diffNodeMap = diffJson.diff("{\"test\":3, \"haha\":4, \"tongzi\":[[[1,3],2,{\"hahaha\":33333333}],{\"22\":2},{\"33\":4}]}",
                "{\"test\":1.5, \"test2\":666, \"tongzi\":[[[1,4],2,{\"hahaha\":33333333}],{\"22\":2},{\"33\":3}]}");
        String result = diffJson.toStringDiffNode(diffNodeMap);
        System.out.println(result);
    }

}
