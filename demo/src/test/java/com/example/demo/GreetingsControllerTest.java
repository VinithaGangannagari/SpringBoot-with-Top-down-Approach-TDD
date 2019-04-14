package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(GreetingsController.class)
public class GreetingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GreetingService greetingService;

    @Test
    public void maleGreetingText200Ok()throws Exception{
        String name ="Rajan";
        String gender = "male";
            given(greetingService.getGreetingByGender(gender)).willReturn("Mr.");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/greeting")
                .param("name",name)
        .param("gender",gender))
                .andExpect(status().isOk())
        .andExpect(
                content().string(containsString(
                        String.format("Hello Mr. %s. How are you?",name)
                ))
        );
    verify(greetingService).getGreetingByGender(anyString());
    }

    @Test
    public void femaleGreetingText200Ok()throws Exception{
        String name= "Rajani";
        String gender ="Mrs.";
        given(greetingService.getGreetingByGender(gender)).willReturn("Mrs.");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/greeting")
                    .param("name",name)
        .param("gender",gender))
                .andExpect(status().isOk())
                .andExpect(
                        content().string(containsString(
                                String.format("Hello Mrs. %s. How are you?", name)
                        ))
                );
        verify(greetingService).getGreetingByGender(anyString());
    }
}
