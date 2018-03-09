package com.system.Repository;

import com.system.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Horac on 2017/5/15.
 */
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    public Admin findOneById(Integer id);

    public Admin findOneByAccount(Integer account);

}
