package com.ssafy.picple.config.baseresponse;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "유효성 검사 오류";

        BaseResponse<String> response = new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);

        return response;
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException ex) {
        return new BaseResponse<>(ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<String> handleGeneralException(Exception ex) {
        return new BaseResponse<>(BaseResponseStatus.SERVER_ERROR);
    }
}
