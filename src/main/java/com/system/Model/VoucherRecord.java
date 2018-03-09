package com.system.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * 代金券记录表
 */

@Entity
public class VoucherRecord {
    @Id
    private int id;
    @OneToOne
    @JoinColumn(name = "receiver_id")
    private User user;
    @OneToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
    private Date create_time;
    //创建日期的三天后
    private Date valid_time;
    //默认0，0未使用，1已使用，2已过期
    private int status;

    public VoucherRecord() {
        this.status = 0;
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

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getValid_time() {
        return valid_time;
    }

    public void setValid_time(Date valid_time) {
        this.valid_time = valid_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
