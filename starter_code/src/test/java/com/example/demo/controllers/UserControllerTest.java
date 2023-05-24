package com.example.demo.controllers;
import com.example.demo.TestUtils;
import com.example.demo.model.dto.CreateUserRequest;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    @Before
    public void setup(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "passwordEncoder", passwordEncoder);
        //User user
    }

    @Test
    public void createUserHappyPath() {
        when(passwordEncoder.encode("test")).thenReturn("hashedTest");
        ResponseEntity response = userController.createUser(new CreateUserRequest("khaled","1111","1111"));
        Assert.assertEquals(HttpStatus.CREATED,response.getStatusCode());
    }

}
