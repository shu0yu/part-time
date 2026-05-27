package com.example.bishe_demo.utils;

import java.util.Map;

/**
 * 用户ID工具类
 * 用于统一处理ThreadLocal中用户ID的获取和类型转换
 */
public class UserIdUtil {

    /**
     * 从ThreadLocal中获取当前登录用户的ID
     * @return 用户ID（Long类型），如果获取失败则返回null
     */
    public static Long getCurrentUserId() {
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        if (userInfo == null) {
            return null;
        }
        return getUserIdFromMap(userInfo);
    }

    /**
     * 从Map中获取用户ID并转换为Long类型
     * @param map 包含用户信息的Map
     * @return 用户ID（Long类型），如果获取失败则返回null
     */
    public static Long getUserIdFromMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        Object idObj = map.get("id");
        return convertToLong(idObj);
    }

    /**
     * 将对象转换为Long类型
     * @param obj 要转换的对象
     * @return Long类型的值，如果转换失败则返回null
     */
    public static Long convertToLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        } else if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        return null;
    }
}
