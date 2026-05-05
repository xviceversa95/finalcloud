package ru.netology.FinalCloud.FileCloud.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.netology.FinalCloud.FileCloud.service.FileMeta;

import java.util.List;

@Repository
public interface FileRepository extends MongoRepository<FileMeta, String>, FileRepositoryCustom {

    public FileMeta findByOriginalNameAndUserId(String originalName, int userId);

    public List<FileMeta> findAllByUserId(Integer userId);

}
