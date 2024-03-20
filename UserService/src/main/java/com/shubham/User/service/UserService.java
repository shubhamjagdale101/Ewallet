package com.shubham.User.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shubham.Utils.CommonIdentifier;
import com.shubham.User.request.CreateUserRequest;
import com.shubham.User.model.User;
import com.shubham.User.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${user.authority}")
    private String authority;

    public UserDetails getUser(String contact) {
        return userRepository.findByPhNo(contact);
    }

    public void createUserReq(CreateUserRequest req){
        userRepository.save(User.builder().build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // in our case phNo is unique identifier like username
        return userRepository.findByPhNo(username);
    }

    public User createUser(CreateUserRequest req) throws JsonProcessingException {
        User user = req.getUser();
        User exist = userRepository.findByPhNo(user.getPhNo());
        if(exist != null) return exist;

        user.setAuthority(authority);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // publish to kafka for notification
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonIdentifier.USER_CREATED_USER_NAME, user.getName());
        jsonObject.put(CommonIdentifier.USER_CREATED_USER_EMAIL, user.getEmail());
        jsonObject.put(CommonIdentifier.USER_CREATED_USER_USER_PhNo, user.getPhNo());
        jsonObject.put(CommonIdentifier.USER_CREATED_USER_USER_IDENTIFIER_VALUE, user.getUserIdentifierValue());
        jsonObject.put(CommonIdentifier.USER_CREATED_USER_USER_IDENTIFIER, user.getUserIdentifier());
        kafkaTemplate.send(CommonIdentifier.USER_CREATED, objectMapper.writeValueAsString(jsonObject.toString()));

        return userRepository.save(user);
    }
}
