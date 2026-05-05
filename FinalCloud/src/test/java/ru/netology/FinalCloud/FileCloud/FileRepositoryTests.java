package ru.netology.FinalCloud.FileCloud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.FinalCloud.FileCloud.repository.FileRepository;
import ru.netology.FinalCloud.FileCloud.service.FileMeta;
import ru.netology.FinalCloud.Users.repository.UserRepo;
import ru.netology.FinalCloud.Users.service.JWTBlackListService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@DataMongoTest
@Testcontainers
public class FileRepositoryTests {

    @Container
    @ServiceConnection
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @MockitoBean
    private UserRepo userRepo;

    @MockitoBean
    private JWTBlackListService jwtBlackListService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(FileMeta.class);
    }

    @Test
    void testSaveAndFind() {
        FileMeta file = new FileMeta();
        file.setOriginalName("test.txt");
        file.setUserId(1);

        fileRepository.save(file);

        FileMeta foundFile = fileRepository.findByOriginalNameAndUserId("test.txt", 1);

        assertThat(foundFile).isNotNull();
        assertThat(foundFile.getOriginalName()).isEqualTo("test.txt");
        assertThat(foundFile.getUserId()).isEqualTo(1);
    }

    @Test
    void testFindAndDelete() {
        FileMeta file1 = new FileMeta();
        file1.setOriginalName("test.txt");
        file1.setUserId(1);
        fileRepository.save(file1);

        FileMeta file2 = new FileMeta();
        file2.setOriginalName("test2.txt");
        file2.setUserId(2);
        fileRepository.save(file2);

        FileMeta file3 = new FileMeta();
        file3.setOriginalName("test3.txt");
        file3.setUserId(3);
        fileRepository.save(file3);

        FileMeta foundFile = fileRepository.findByOriginalNameAndUserId("test2.txt", 2);
        fileRepository.delete(foundFile);

        assertThat(fileRepository.findByOriginalNameAndUserId("test2.txt", 2)).isNull();
    }

    @Test
    void testFindAllByUserId() {
        FileMeta file1 = new FileMeta();
        file1.setOriginalName("test.txt");
        file1.setUserId(1);
        fileRepository.save(file1);

        FileMeta file2 = new FileMeta();
        file2.setOriginalName("test2.txt");
        file2.setUserId(1);
        fileRepository.save(file2);

        FileMeta file3 = new FileMeta();
        file3.setOriginalName("test3.txt");
        file3.setUserId(1);
        fileRepository.save(file3);

        List<FileMeta> files = fileRepository.findAllByUserId(1);

        assertThat(files).isNotNull();
        assertThat(files.size()).isEqualTo(3);
    }

}