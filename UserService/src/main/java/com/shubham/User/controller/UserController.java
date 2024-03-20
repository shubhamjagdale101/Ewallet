package com.shubham.User.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shubham.User.request.CreateUserRequest;
import com.shubham.User.model.User;
import com.shubham.User.service.UserService;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public User createUser(@RequestBody @Valid CreateUserRequest req) throws JsonProcessingException {
        return userService.createUser(req);
    }

    @GetMapping("/getUser")
    public UserDetails getUser(@RequestParam("contact") String contact){
        return userService.getUser(contact);
    }
}
