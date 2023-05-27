package com.example.demo.controllers;
import com.example.demo.TestUtils;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.CreateUserRequest;
import com.example.demo.model.dto.GetUserResponse;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private CartRepository cartRepository = mock(CartRepository.class);

    @Before
    public void setup(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "passwordEncoder", passwordEncoder);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        //User user
    }

    @Test
    public void createUserHappyPath() {
        when(passwordEncoder.encode("test")).thenReturn("hashedTest");
        CreateUserRequest createUserRequest = new CreateUserRequest("khaled","1111","1111");
        ResponseEntity<GetUserResponse> response = userController.createUser(createUserRequest);
        Assert.assertEquals(HttpStatus.CREATED,response.getStatusCode());
        Assert.assertEquals(createUserRequest.getUsername(),response.getBody().getUsername());
    }

    @Test
    public void createUserBadPasswordConfirmation() {
        CreateUserRequest createUserRequest = new CreateUserRequest("khaled","1111","2222");
        try {
            ResponseEntity<GetUserResponse> response = userController.createUser(createUserRequest);
            Assert.assertTrue(false);

        } catch (BadRequestException exception){
            Assert.assertTrue(true);
        }
    }

    @Test
    public void findByUsernameHappyPath() {
        GetUserResponse getUserResponse = new GetUserResponse();
        getUserResponse.setUsername("khaled");
        when(userRepository.findByUsernameIgnoreCase("khaled")).thenReturn(Optional.of(new User(0,"khaled","111",null)));
        ResponseEntity<GetUserResponse> response = userController.findByUserName("khaled");
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
        Assert.assertEquals(getUserResponse.getUsername(),response.getBody().getUsername());
    }

    @Test
    public void findByInvalidUsername() {
        when(userRepository.findByUsernameIgnoreCase("khaled")).thenReturn(Optional.empty());
        try {
            ResponseEntity<GetUserResponse> response = userController.findByUserName("khaled");
            Assert.assertTrue(false);
        } catch (NotFoundException exception) {
            Assert.assertTrue(true);

        }
    }

    @Test
    public void findByIdHappyPath() {
        GetUserResponse getUserResponse = new GetUserResponse();
        getUserResponse.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L,"khaled","111",null)));
        ResponseEntity<GetUserResponse> response = userController.findById(1L);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
        Assert.assertEquals(getUserResponse.getId(),response.getBody().getId());
    }

    @Test
    public void findByInvalidId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        try {
            ResponseEntity<GetUserResponse> response = userController.findById(1L);
            Assert.assertTrue(false);
        } catch (NotFoundException exception) {
            Assert.assertTrue(true);

        }
    }

}
