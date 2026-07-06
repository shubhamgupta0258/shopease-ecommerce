package com.example.e_commerce.controllers;

import com.example.e_commerce.models.User;
import com.example.e_commerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    //Register a new user
    @PostMapping("/register")
    public User registerUser(@RequestBody User user){
        return userService.createUser(user);
    }

    //Login (basic version -no security yet)
    @PostMapping("/login")
    public String loginUser(@RequestBody User loginRequest){

        Optional<User> optionalUser = userService.getUserByEmail(loginRequest.getEmail());

        if (optionalUser.isPresent() &&
                optionalUser.get().getPassword().equals(loginRequest.getPassword())) {

            User user = optionalUser.get();
            return "Login successful for user: " + user.getName() + " with id: " + user.getId();

        } else {
            return "Invalid email or password";
        }
    }


    //Get all users
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    //Get user by ID
    @GetMapping("/{id}")
    public Optional<User> getUserByID(@PathVariable Long id){
        return userService.getUserById(id);
    }

//    // Update User
//    @PutMapping("/{id}")
//    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
//        return userService.updateUser(id, updatedUser);
//    }

    // Delete User
//    @DeleteMapping("/{id}")
//    public String deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return "User deleted successfully!";
//    }


}
