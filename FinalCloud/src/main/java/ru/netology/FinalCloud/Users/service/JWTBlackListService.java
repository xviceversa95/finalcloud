package ru.netology.FinalCloud.Users.service;

import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Service;
import ru.netology.FinalCloud.Users.models.JwtBlackListModel;
import ru.netology.FinalCloud.Users.repository.JwtBlackListRepo;

import java.util.Date;


@Service
public class JWTBlackListService {
    private JwtBlackListRepo jwtBlackListRepo;
    private MongoTemplate mongoTemplate;
    private JWTService jwtService;

    public JWTBlackListService(JwtBlackListRepo jwtBlackListRepo, MongoTemplate mongoTemplate, JWTService jwtService) {
        this.jwtBlackListRepo = jwtBlackListRepo;
        this.mongoTemplate = mongoTemplate;
        this.jwtService = jwtService;
    }

    public JwtBlackListModel deactivateToken(String token) {
        return addToBlackList(token);
    }

    public JwtBlackListModel addToBlackList(String token) {
        JwtBlackListModel model = new JwtBlackListModel(token, jwtService.extractId(token), new Date(System.currentTimeMillis() + 60 * 60 * 10 * 1000));
        return jwtBlackListRepo.save(model);
    }

    public boolean isInBlackList(String token) {
        String id = jwtService.extractId(token);
        return jwtBlackListRepo.existsById(id);
    }

}
