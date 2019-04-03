package com.example.demo;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    private CarService carService;

    @Before
    public void setUp() {
        carService = new CarService(carRepository);
    }

    @Test
    public void getCarDetails_returnsCarInfo(){
        given(carRepository.findByName("pirus")).willReturn(new Car("pirus", "hybrid"));
        Car car = carService.getCarDetails("pirus");

        Assertions.assertThat(car.getName()).isEqualTo("pirus");

        Assertions.assertThat(car.getType()).isEqualTo("hybrid");
    }

    @Test(expected = CarNotFoundException.class)
    public void getCarDetails_whenCarNotFound(){
        given(carRepository.findByName("pirus")).willReturn(null);
        carService.getCarDetails("pirus");
    }
}
