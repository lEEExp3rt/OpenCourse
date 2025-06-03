package org.opencourse.services.user;

import org.opencourse.models.User;
import org.opencourse.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User information details service manager.
 * 
 * @author LJX
 */
@Service
public class UserInfoService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public UserInfoService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("未找到邮箱为: " + email + " 的用户"));
        return user;
    }
}
