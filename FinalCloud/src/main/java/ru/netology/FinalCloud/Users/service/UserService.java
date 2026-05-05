package ru.netology.FinalCloud.Users.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.netology.FinalCloud.Users.repository.UserRepo;

//под расширение - если нужно будет добавить бизнес-логику на пользователей
@Service
public class UserService {

    private UserRepo repository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepo repo) {
        this.repository = repo;
    }
}
