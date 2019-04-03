package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CarController.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    public void getCar_ShouldReturnCar() throws Exception{

        given(carService.getCarDetails(anyString())).willReturn(new Car("pirus","hybrid"));
        mockMvc.perform(MockMvcRequestBuilders.get("/cars/pirus"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("name").value("pirus"))
        .andExpect(jsonPath("type").value("hybrid"));
    }

    @Test
    public void getCar_notFound() throws Exception{
        given(carService.getCarDetails(anyString())).willThrow(new CarNotFoundException());
        mockMvc.perform(MockMvcRequestBuilders.get("/cars/pirus"))
                .andExpect(status().isNotFound());
    }
}
