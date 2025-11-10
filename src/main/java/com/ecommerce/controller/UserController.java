package com.ecommerce.controller;

import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        boolean ok = userService.validateUser(email, password);
        if (ok) return ResponseEntity.ok("Login successful");
        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    @GetMapping
    public ResponseEntity<List<User>> all() { return ResponseEntity.ok(userService.getAllUsers()); }

    @GetMapping("/{id}") public ResponseEntity<User> byId(@PathVariable Long id) { return ResponseEntity.ok(userService.getUserById(id)); }

    @PutMapping("/{id}") public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User u) { return ResponseEntity.ok(userService.updateUser(id, u)); }

    @DeleteMapping("/{id}") public ResponseEntity<String> delete(@PathVariable Long id) { userService.deleteUser(id); return ResponseEntity.ok("Deleted"); }
}
