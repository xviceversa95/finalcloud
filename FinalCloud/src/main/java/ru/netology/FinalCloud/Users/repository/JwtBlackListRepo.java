package ru.netology.FinalCloud.Users.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.netology.FinalCloud.Users.models.JwtBlackListModel;

@Repository
public interface JwtBlackListRepo extends MongoRepository<JwtBlackListModel, String> {
}
