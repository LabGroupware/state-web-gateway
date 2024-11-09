package org.cresplanex.api.state.webgateway.dto;

public class ErrorResponseDto extends ResponseDto<Object> {

    public ErrorResponseDto(String code, String caption, ErrorAttributeDto errorAttributes) {
        this.setSuccess(false); // 常にエラー状態
        this.setData(null); // エラー時にはデータが null
        this.setCode(code);
        this.setCaption(caption);
        this.setErrorAttributes(errorAttributes);
    }

    // エラー用の静的ファクトリメソッド
    public static ErrorResponseDto create(String code, String caption, ErrorAttributeDto errorAttributes) {
        return new ErrorResponseDto(code, caption, errorAttributes);
    }
}