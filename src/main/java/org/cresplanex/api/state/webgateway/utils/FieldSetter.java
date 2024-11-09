package org.cresplanex.api.state.webgateway.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class FieldSetter {

    /**
     * 複数のフィールドをsourceからtargetにnullチェック付きで設定します。
     *
     * @param source データを持つソースオブジェクト
     * @param target データを設定するターゲットオブジェクト
     * @param fieldNames 設定するフィールド名の可変引数
     */
    public static <T, R> void setIfNotNull(T source, R target, String... fieldNames) {
        Arrays.stream(fieldNames).forEach(fieldName -> {
            try {
                // ソースのgetterメソッドを取得して値を取得
                Method getter = source.getClass().getMethod("get" + fieldName);
                Object value = getter.invoke(source);

                // 値がnullでなければ、ターゲットのsetterメソッドを呼び出して値を設定
                if (value != null) {
                    Method setter = target.getClass().getMethod("set" + fieldName, getter.getReturnType());
                    setter.invoke(target, value);
                }
            } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                throw new RuntimeException("Failed to set field: " + fieldName, e);
            }
        });
    }
}
