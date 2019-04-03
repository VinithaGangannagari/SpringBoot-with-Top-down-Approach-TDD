package com.example.demo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    private CarRepository carRepository;
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Cacheable("cars")
    public Car getCarDetails(String name){
        Car car = carRepository.findByName(name);
        if(car == null){
            throw new CarNotFoundException();
        }
        return car;
    }
}
