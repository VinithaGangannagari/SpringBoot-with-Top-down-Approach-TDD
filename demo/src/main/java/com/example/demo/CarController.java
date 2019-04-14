package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.example.demo.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
    public Car postCarDetails(@Valid @RequestBody Car car){
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

    @DeleteMapping("/cars/{id}")
    public Car deleteCars(@PathVariable String id){
        return carService.deleteCarDetails(id);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    private String handleValidationExceptions(HttpMessageNotReadableException ex){
//        final String bodyOfResponse = ex.getMessage();
//        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//        return ex.getBindingResult()
//                .getAllErrors().stream()
//                .map(ObjectError::getDefaultMessage)
//                .collect(Collectors.toList());
        return "Hi please select body";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }
}
