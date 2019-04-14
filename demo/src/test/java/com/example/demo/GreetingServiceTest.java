package com.example.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class GreetingServiceTest {

   @Configuration
   public static class Config{
       @Bean
       public GreetingService greetingService(){
           return new GreetingService();
       }
   }

   @Autowired
    GreetingService greetingService;

   @Test
    public void whenGenderMaleReturnMrTest(){
       String salute = greetingService.getGreetingByGender("male");
       Assert.assertEquals("Mr.",salute);
   }

   @Test
    public void whenGenderFemaleReturnMrsTest(){
       String salute = greetingService.getGreetingByGender("female");
       Assert.assertEquals("Mrs.",salute);
   }

   @Test(expected = RuntimeException.class)
    public void whenGenderThrowsException(){
    String salute = greetingService.getGreetingByGender("sfdf");
   }
}
