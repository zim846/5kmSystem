package com.system.Repository;

import com.system.Model.User;
import com.system.Model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    public Voucher findOneById(int Id);
}
