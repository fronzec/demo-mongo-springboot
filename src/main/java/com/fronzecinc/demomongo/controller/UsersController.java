package com.fronzecinc.demomongo.controller;

import com.fronzecinc.demomongo.model.Setting;
import com.fronzecinc.demomongo.model.User;
import com.fronzecinc.demomongo.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/")
public class UsersController {
    private final UserRepository repository;

    public UsersController(final UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return repository.save(user);
    }

    @GetMapping("/{userId}/settings")
    public Map<String, String> getUserSettings(@PathVariable String userId) {
        return repository.findById(userId)
                .map(User::getUserSettings)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{userId}/settings/{settingKey}")
    public String getSettingForUser(@PathVariable final String userId,
                                    @PathVariable final String settingKey) {
        User user =  repository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return Optional.ofNullable(user.getUserSettings().get(settingKey))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @PutMapping("/{userId}/settings")
    public Map<String, String> addSetting(@PathVariable final String userId,
                                          @RequestBody Setting newSettings) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!user.getUserSettings().containsKey(newSettings.getKey())) {
            user.getUserSettings().put(newSettings.getKey(), newSettings.getValue());
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        return repository.save(user).getUserSettings();
    }

}
