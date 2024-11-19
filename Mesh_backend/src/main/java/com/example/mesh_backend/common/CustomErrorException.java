package com.example.mesh_backend.common;

import com.example.mesh_backend.message.ErrorResponse;
import lombok.Getter;

@Getter
public class CustomErrorException extends RuntimeException{

    private ErrorResponse errorResponse;
    public CustomErrorException(ErrorResponse errorResponse){
        this.errorResponse = errorResponse;
    }
}