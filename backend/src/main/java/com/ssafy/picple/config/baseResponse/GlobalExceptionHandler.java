package com.ssafy.picple.config.baseResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException ex) {
        return new BaseResponse<>(ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<String> handleGeneralException(Exception ex) {
        return new BaseResponse<>(BaseResponseStatus.SERVER_ERROR);
    }
}
