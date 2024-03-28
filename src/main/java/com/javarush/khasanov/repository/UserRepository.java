package com.javarush.khasanov.repository;

import com.javarush.khasanov.entity.User;

import java.util.Optional;

public class UserRepository extends AbstractRepository<User> {
    public User getAdmin() {
        Optional<User> optionalUser = get(1L);
        if (optionalUser.isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setPassword("admin");
            create(admin);
            return admin;
        }
        return optionalUser.get();
    }

    public Optional<User> get(String username) {
        return map.values()
                .stream()
                .filter(user -> user.getName().equalsIgnoreCase(username))
                .findFirst();
    }
}