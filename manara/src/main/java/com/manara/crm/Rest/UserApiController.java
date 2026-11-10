package com.manara.crm.Rest;

import com.manara.crm.dto.PasswordForm;
import com.manara.crm.dto.UserDTO;
import com.manara.crm.entity.User;
import com.manara.crm.mapper.UserMapper;
import com.manara.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
//@CrossOrigin(origins = "https://localhost:4200", allowCredentials = "true")
public class UserApiController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return UserMapper.toDTOList(userService.findAll());
    }

    @GetMapping("/all")
    public List<UserDTO> afficherUtilisateurs() {
        return UserMapper.toDTOList(userService.afficherUtilisateurs());
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return UserMapper.toDTO(userService.findById(id));
    }

    @GetMapping("/email")
    public UserDTO getUserByEmail(@RequestParam String email) {
        return UserMapper.toDTO(userService.findByEmail(email));
    }

    @GetMapping("/role/{role}")
    public List<UserDTO> getUsersByRole(@PathVariable String role) {
        return UserMapper.toDTOList(userService.findByRole(role));
    }

    @GetMapping("/count/role/{role}")
    public long countByRole(@PathVariable String role) {
        return userService.countByRole(role);
    }

    @GetMapping("/count/new-this-month/{role}")
    public long countNewThisMonth(@PathVariable String role) {
        return userService.countNewThisMonth(role);
    }

    @PostMapping("/register-parent")
    public UserDTO inscrireParent(@RequestBody User user) {
        return UserMapper.toDTO(userService.inscrireParent(user));
    }

    @PostMapping
    public UserDTO createUser(@RequestBody User user) {
        return UserMapper.toDTO(userService.creerUser(user));
    }

    @PostMapping("/create-simple")
    public void creerUtilisateur(@RequestBody User utilisateur) {
        userService.creerUtilisateur(utilisateur);
    }

    @PutMapping("/{id}/profile")
    public UserDTO updateProfile(@PathVariable Long id, @RequestBody User form) {
        return UserMapper.toDTO(userService.updateProfile(id, form));
    }

    @PutMapping("/{id}/change-password")
    public void changePassword(@PathVariable Long id, @RequestBody PasswordForm form) {
        userService.changePassword(id, form);
    }

    @PatchMapping("/{id}/toggle-active")
    public UserDTO toggleActive(@PathVariable Long id) {
        return UserMapper.toDTO(userService.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}