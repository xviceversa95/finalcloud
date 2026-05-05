package ru.netology.FinalCloud.Users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.FinalCloud.Users.models.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User getUserByUsername(String username);
}
