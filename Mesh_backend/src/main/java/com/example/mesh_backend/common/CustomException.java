package com.example.mesh_backend.common;

import com.example.mesh_backend.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private ErrorCode errorCode;
    public CustomException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}