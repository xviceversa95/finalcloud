package ru.netology.FinalCloud.FileCloud.repository;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import ru.netology.FinalCloud.FileCloud.service.FileMeta;

public class FileRepositoryImpl implements FileRepositoryCustom {

    private MongoTemplate template;

    public FileRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public FileMeta updateName(String oldFileName, String newFilename, int userId) {
        Query query = new Query(Criteria.where("originalName").is(oldFileName)
                .and("userId").is(userId));
        Update update = new Update().set("originalName", newFilename);
        return template.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), FileMeta.class);
    }
}
