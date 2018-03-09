package com.system.Repository;

import com.system.Model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Integer> {
    public Advertisement findOneById(int Id);

    @Query("select advertisement from Advertisement advertisement where advertisement.pics like ?1")
    public Advertisement findOneByPics(String url);
}
