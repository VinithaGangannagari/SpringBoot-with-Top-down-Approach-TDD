package com.example.demo;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CarRepositoryTest {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void getCar_returnsCarDetails() throws Exception{
        Car savedCar = testEntityManager.persistAndFlush(new Car("pirus","hybrid","new car"));

       Car car = carRepository.findByName("pirus");

       // Assertions.assertThat(car.getName()).isEqualTo("pirus");
        Assertions.assertThat(car.getName()).isEqualTo(savedCar.getName());
        Assertions.assertThat(car.getType()).isEqualTo(savedCar.getType());
    }

    @Test
    public void postCarDetails(){
        testEntityManager.persistAndFlush(new Car("pirus","hybrid","new car"));

    }
}
