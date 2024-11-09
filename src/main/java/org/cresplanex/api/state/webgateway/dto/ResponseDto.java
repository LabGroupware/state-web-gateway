package org.cresplanex.api.state.webgateway.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ResponseDto<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean success;
    private T data;
    private String code;
    private String caption;
    private ErrorAttributeDto errorAttributes;

    public static class Builder<T> {

        private final boolean success;
        private T data;
        private String code;
        private String caption;
        private ErrorAttributeDto errorAttributes;

        // コンストラクタ
        private Builder(boolean success) {
            this.success = success;
        }

        private Builder(boolean success, ErrorAttributeDto errorAttributes) {
            this.success = success;
            this.errorAttributes = errorAttributes;
        }

        // 成功用のファクトリメソッド
        public static <T> Builder<T> withSuccess() {
            return new Builder<>(true);
        }

        // エラー用のファクトリメソッド
        public static <T> Builder<T> withError(ErrorAttributeDto errorAttributes) {
            return new Builder<>(false, errorAttributes);
        }

        // データ設定
        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        // エラーコード設定
        public Builder<T> code(String code) {
            this.code = code;
            return this;
        }

        // メッセージ（キャプション）設定
        public Builder<T> caption(String caption) {
            this.caption = caption;
            return this;
        }

        // ビルドメソッド
        public ResponseDto<T> build() {
            ResponseDto<T> response = new ResponseDto<>();
            response.setSuccess(success);
            response.setData(data);
            response.setCode(code);
            response.setCaption(caption);
            response.setErrorAttributes(errorAttributes);
            return response;
        }
    }
}
