package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class CarController {


    private CarService carService;

    public CarController(CarService carService){
        this.carService = carService;
    }

    @GetMapping("/cars/name/{name}")
    public Car getCars(@PathVariable("name") String name){

        return carService.getCarDetails(name);
    }

    @GetMapping("/cars/{type}")
    public ResponseEntity<Car> getCarByType(@PathVariable("type") String type){
        return carService.getCarDetailsByType(type)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cars")
    public Car postCarDetails(@RequestBody Car car){
       /* System.out.println(carService.isCarExists(car));
        if(carService.isCarExists(car)) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }*/
       return carService.saveCarDetails(car);
       // HttpHeaders headers = new HttpHeaders();
     //   headers.setLocation(ucBuilder.path("/cars/{id}").buildAndExpand(car.getId()).toUri());
        //return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    @PutMapping("/cars")
    public Car putCarDetails(@RequestBody Car car){
        return carService.updateCarDetails(car);
    }

    @DeleteMapping("/cars/{name}")
    public Car deleteCars(@PathVariable String name){
        return carService.deleteCarDetails(name);
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void carNotFoundHandler(CarNotFoundException ex){

    }
}
