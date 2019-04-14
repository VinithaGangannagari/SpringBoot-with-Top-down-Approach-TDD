package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    public String getGreetingByGender(String gender) throws RuntimeException{
        if(gender.equals("male"))
            return "Mr.";
        else if(gender.equals("female"))
            return "Mrs.";
        else
            throw new RuntimeException();
        //return gender.equals("male")?"Mr.":"Mrs";
    }
}
