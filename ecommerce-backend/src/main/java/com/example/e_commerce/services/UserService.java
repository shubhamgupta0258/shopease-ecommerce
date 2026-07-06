package com.example.e_commerce.services;

import com.example.e_commerce.models.User;
import com.example.e_commerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //create new user -
//    So save(user) is the standard way in Spring Data JPA to persist a new or
//    updated entity to the database.
    public User createUser(User user){
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    //Get user by ID
//     Optional is a container that may hold a non-null value or be empty,
//    helping avoid NullPointerException.
    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }

    //get all users-
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //update users
    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        // add more fields as needed

        return userRepository.save(user);
    }
//    public User updateUser(Long id, User updatedUser) {
//        return userRepository.findById(id).map(user -> {
//            user.setName(updatedUser.getName());
//            user.setEmail(updatedUser.getEmail());AC
//            user.setPassword(updatedUser.getPassword());
//            return userRepository.save(user);
//        }).orElse(null);
//    }
    // Delete user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // ✅ Find user by email (used for login)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
/// ask perplex for the basic flow of till now i did ?
