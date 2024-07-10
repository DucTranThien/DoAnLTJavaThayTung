package com.hutech.tranthienducpro.Repository;

import com.hutech.tranthienducpro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);
    @Query("select u.countFail from User u where u.username=?1")
    int countFailByUsername(String username);
    @Query("select u from User u where u.email=?1")
    User findByEmail(String email);
    @Query("select u from User u where u.tokenResetPassword=?1")
    User findByToken(String token);
}
