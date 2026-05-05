package ru.netology.FinalCloud.Users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.FinalCloud.Users.models.MyUserDetails;
import ru.netology.FinalCloud.Users.models.User;
import ru.netology.FinalCloud.Users.repository.UserRepo;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    //метод ищет юзера по userName
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.getUserByUsername(username);
        System.out.println(user.getUsername());

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new MyUserDetails(user);
    }
}
