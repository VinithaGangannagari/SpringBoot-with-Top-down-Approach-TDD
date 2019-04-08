package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = NONE)
//classes = CachingConfig.class)
@AutoConfigureTestDatabase
@AutoConfigureCache
public class CachingTest {


    private CarService carService;

    @MockBean
    private CarRepository carRepository;

    @Before
    public void setUp() {
        carService = new CarService(carRepository);
    }

    @Test
public void caching() throws Exception{
    given(carRepository.findByName(anyString())).willReturn(new Car("pirus","hybrid","new car"));
    carService.getCarDetails("pirus");
    carService.getCarDetails("pirus");

    verify(carRepository, times(1)).findByName("pirus");
}
}
