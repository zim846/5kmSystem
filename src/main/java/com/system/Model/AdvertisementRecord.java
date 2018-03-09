package com.system.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * 广告记录表
 */

@Entity
public class AdvertisementRecord {
    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    @JoinColumn(name = "receiver_id")
    private User user;
    @OneToOne
    @JoinColumn(name = "ad_id")
    private Advertisement advertisement;
    private Date create_time;

    public AdvertisementRecord() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(Advertisement advertisement) {
        this.advertisement = advertisement;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
