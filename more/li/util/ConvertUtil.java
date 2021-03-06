package li.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import li.model.Field;

/**
 * 类型转换的工具类
 * 
 * @author li (limingwei@mail.com)
 * @version 0.1.7 (2012-05-08)
 */
public class ConvertUtil extends Convert {
    /**
     * 将数据对象或对象的集合或数组转换为json
     */
    public static String toJson(Object target) {
        if (null == target) {
            return "";
        }
        if (target instanceof Collection) {// 如果是集合,转换成数组处理
            return toJson(((Collection) target).toArray());
        }
        if (target.getClass().isArray()) {// 如果是数组
            String json = "[";
            for (Object one : (Object[]) target) {// 处理每个对象
                json += toJson(one) + ",";
            }
            return json.substring(0, json.length() - 1) + "]";// 返回
        }
        String json = "{";// 处理单个对象
        if (Verify.basicType(target.getClass())) {// 单个基本类型的数据
            json += "\"" + target.getClass().getName() + "\":\"" + target + "\",";
        } else if (Map.class.isAssignableFrom(target.getClass())) {// 如果是Map
            Set<Entry> entries = ((Map) target).entrySet();
            for (Entry<String, Object> entry : entries) {// Map的每个属性
                json += "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",";
            }
        } else {// 不是Map，按照POJO处理
            List<Field> fields = Field.list(target.getClass(), true);
            for (Field field : fields) {// POJO的每个属性
                json += "\"" + field.name + "\":\"" + Reflect.get(target, field.name) + "\",";
            }
        }
        return json.substring(0, json.length() - 1) + "}";
    }

    /**
     * 将json转换为数据对象的List
     */
    public static <T> List<T> fromJson(Class<T> type, String json) {
        if (Verify.isEmpty(json)) {
            return Collections.EMPTY_LIST;
        }
        final String JSON_REGEX = "^.*}[]]{0,1},[\\[]{0,1}\\{.*$", JSON_SPLIT = "}[]]{0,1},[\\[]{0,1}\\{";
        List<T> list = new ArrayList<T>();
        if (Verify.regex(json, JSON_REGEX)) {// 处理多个对象
            String[] array = json.split(JSON_SPLIT);
            for (String one : array) {
                list.addAll(fromJson(type, one)); // 这里递归调用
            }
            return (List<T>) list;// 返回
        }
        T one = Reflect.born(type);// 处理单个对象
        String[] array = json.split(",");
        for (String field : array) {
            String[] strs = field.split(":");
            String key = strs[0].substring(strs[0].indexOf('"') + 1, strs[0].lastIndexOf('"'));
            String value = strs[1].substring(strs[1].indexOf('"') + 1, strs[1].lastIndexOf('"'));
            Reflect.set(one, key, value);// 是否要验证空字符串和null
        }
        list.add(one);
        return list;
    }

    /**
     * 将一个字符串出现的HMTL元素进行转义
     */
    public static String escapeHtml(CharSequence cs) {
        if (null == cs) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        char[] cas = cs.toString().toCharArray();
        for (char c : cas) {
            if ('&' == c) {
                sb.append("&amp;");
            } else if ('<' == c) {
                sb.append("&lt;");
            } else if ('>' == c) {
                sb.append("&gt;");
            } else if ('\'' == c) {
                sb.append("&#x27;");
            } else if ('"' == c) {
                sb.append("&quot;");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}