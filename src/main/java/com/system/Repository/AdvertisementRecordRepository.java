package com.system.Repository;

import com.system.Model.AdvertisementRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementRecordRepository extends JpaRepository<AdvertisementRecord, Integer> {
    public AdvertisementRecord findOneById(int Id);
}
