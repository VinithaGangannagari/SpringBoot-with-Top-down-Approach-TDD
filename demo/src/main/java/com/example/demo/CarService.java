package com.example.demo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<Car> getCarDetailsByType(String name){
        Optional<Car> car = carRepository.findByType(name);
        return car;
    }

    public Car saveCarDetails(Car car){
       //carRepository.save(car);

            if(1==1) {
                throw new CarNotFoundException();
            }
        return car;
    }

    public boolean isCarExists(Car car) {
       return carRepository.findByDescription(car.getDescription())!=null;
      }

    public Car updateCarDetails(Car car) {
        if(carRepository.findByName(car.getName())!=null){
            carRepository.save(car);
        }
        return car;
    }

    public Car deleteCarDetails(String anyString) {
        Car car = carRepository.findByName(anyString);
        if(carRepository.findByName(anyString)!=null){
            carRepository.deleteByName(anyString);
        }
        return car;
    }
}
