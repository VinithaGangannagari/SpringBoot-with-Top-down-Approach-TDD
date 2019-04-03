package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.demo.*;

@RestController
public class CarController {


    private CarService carService;

    public CarController(CarService carService){
        this.carService = carService;
    }

    @GetMapping("/cars/{name}")
    public Car getCars(@PathVariable String name){

        return carService.getCarDetails(name);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void carNotFoundHandler(CarNotFoundException ex){

    }
}
