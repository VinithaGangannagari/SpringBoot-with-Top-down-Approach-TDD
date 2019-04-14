package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@RunWith(SpringRunner.class)
@WebMvcTest(CarController.class)
@WebAppConfiguration
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    public void getCar_ShouldReturnCar() throws Exception{

        given(carService.getCarDetails(anyString())).willReturn(new Car("pirus","hybrid","hybrid car"));
        mockMvc.perform(MockMvcRequestBuilders.get("/cars/name/pir`"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("name").value("pirus"))
        .andExpect(jsonPath("type").value("hybrid"));
    }

    @Test
    public void getCar_notFound() throws Exception{
        given(carService.getCarDetails(anyString())).willThrow(new CarNotFoundException("Car is not found"));
        mockMvc.perform(MockMvcRequestBuilders.get("/cars/pir"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCarByType_ShouldReturnCar() throws Exception{
        Car car = new Car("pirus","hybrid", "hybrid car");
        given(carService.getCarDetailsByType(anyString())).willReturn(Optional.of(car));
        mockMvc.perform(MockMvcRequestBuilders.get("/cars/pirus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("pirus"))
                .andExpect(jsonPath("type").value("hybrid"))
                .andExpect(jsonPath("description").value("hybrid car"));
    }

    @Test
    public void getCarByType_notFound() throws Exception{
        given(carService.getCarDetailsByType(anyString())).willThrow(new CarNotFoundException("Car is not found"));
        mockMvc.perform(MockMvcRequestBuilders.get("/cars/hybrid"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void postCar_ShouldReturnCar() throws Exception{
        Car car = new Car("pirus","hybrid","New Car ");
        objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(car);
        given(carService.saveCarDetails(car)).willReturn(car);
        mockMvc.perform(MockMvcRequestBuilders.post("/cars")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void postCar_ShouldReturnConflict() throws Exception{
        Car car = new Car("pirus","hybrid","new Car");
        String json = objectMapper.writeValueAsString(car);
        given(carService.saveCarDetails(car)).willThrow(new CarNotFoundException("Car is already found"));
        mockMvc.perform(MockMvcRequestBuilders.post("/cars")
                                                .contentType(MediaType.APPLICATION_JSON)
                                               .content(json)
                                                .characterEncoding("utf-8"))
                                            .andExpect(status().isNotFound());
    }

    @Test
    public void putCar_ShouldReturnCar() throws Exception{
        Car car = new Car("pirus","hybrid","new Car");
        String json = objectMapper.writeValueAsString(car);
        given(carService.updateCarDetails(car)).willReturn(car);
        mockMvc.perform(MockMvcRequestBuilders.put("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .characterEncoding("utf-8")).andExpect(status().isOk());

    }

    @Test
    public void deleteCar_ShouldReturnCar() throws Exception{
        given(carService.deleteCarDetails(anyString())).willReturn(new Car("pirus","hybrid","description"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/cars/pirus"))
                .andExpect(status().isOk());
    }










  /*  protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }*/
}
