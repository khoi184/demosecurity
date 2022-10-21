package com.thuanthanhtech.demosecurity.repository;

import com.thuanthanhtech.demosecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select u from User u where lower(concat(coalesce(u.username,''), coalesce(u.email ,''))) like lower(concat('%', :username, '%'))")
    User findByUsernameOrEmail(@Param("username") String username);

    User findByEmail(String email);

    User findByUsername(String username);

    User findByToken(String token);

    boolean existsByUsername(String username);
}
