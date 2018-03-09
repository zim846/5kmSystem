package com.system.Repository;

import com.system.Model.VoucherRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRecordRepository extends JpaRepository<VoucherRecord, Integer> {
    public VoucherRecord findOneById(int Id);
}
