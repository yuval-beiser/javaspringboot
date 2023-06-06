package com.handson.basic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentsConstroller {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> hello(){
        return new ResponseEntity<>("Hello world", HttpStatus.OK);
    }
}
