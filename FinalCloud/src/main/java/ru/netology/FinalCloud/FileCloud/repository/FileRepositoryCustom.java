package ru.netology.FinalCloud.FileCloud.repository;

import ru.netology.FinalCloud.FileCloud.service.FileMeta;

public interface FileRepositoryCustom {
    FileMeta updateName(String oldFilename, String newFilename);
}
