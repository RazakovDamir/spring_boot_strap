package web.controller;

import web.model.Role;
import web.model.User;
import web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping()
    public String getUsers(ModelMap model) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("user", userService.findByUsername(name));
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("user", userService.getUserById(id));
        return "user";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("user") User user) {
        return "new";
    }

    @PostMapping()
    public String addUser(@ModelAttribute("user") User user, @RequestParam(value= "rolesList", required = false) String rolesList) {
        user.setRoles(setRoles(rolesList));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editUser(ModelMap modelMap, @PathVariable("id") Long id) {
        modelMap.addAttribute("user", userService.getUserById(id));
        return "update";
    }

    @PatchMapping("/{id}/edit")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam(value= "rolesList", required = false) String rolesList) {
        User newUser = userService.findByUsername(user.getUsername());
        user.setRoles(setRoles(rolesList));
        if (user.getPassword().equals("")) {
            user.setPassword(newUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.save(user);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    private Set<Role> setRoles(String rolesList) {
        Set<Role> roles = new HashSet<>();

        rolesList = rolesList == null ? "" : rolesList;
        if (rolesList.contains("ROLE_ADMIN")) {
            roles.add(new Role(1L, "ROLE_ADMIN"));
        }

        if (rolesList.contains("ROLE_USER")) {
            roles.add(new Role(2L, "ROLE_USER"));
        }

        return roles;
    }

}