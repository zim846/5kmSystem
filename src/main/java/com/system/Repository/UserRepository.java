package com.system.Repository;

import com.system.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findOneById(int Id);
}
