package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CarNotFoundException extends RuntimeException {

//    public CarNotFoundException(Throwable e) {
//        super(e);
//    }

    private static final long serialVersionUID = 1L;
    public CarNotFoundException(){
        super();
    }


    public CarNotFoundException(String message){
        super(message);
    }
}
