package com.example.websocketdemo.model;

import javax.persistence.*;
import java.sql.Time;


@Entity
@Table(name = "key_info")
public class Key {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private int id;

    @Column(name = "key_value", length = 3071)
    private String key;

    @Column(name = "asym")
    private boolean isAsym;

    @Column(name = "created_time")
    private Time creationTime;

    @Column(name = "expired_time")
    private Time expiredTime;

    @Column(name = "is_public")
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name = "owner_id")
    private User owner;


    public Key(String key, boolean isAsym, Time creationTime, Time expiredTime, boolean isPublic, User owner) {
        this.key = key;
        this.isAsym = isAsym;
        this.creationTime = creationTime;
        this.expiredTime = expiredTime;
        this.isPublic = isPublic;
        this.owner = owner;
    }

    public Key(String key, boolean isAsym, boolean isPublic, User owner) {
        this.key = key;
        this.isAsym = isAsym;
        this.isPublic = isPublic;
        this.owner = owner;
    }

    public Key() {
        this("", true, true, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isAsym() {
        return isAsym;
    }

    public void setAsym(boolean asym) {
        isAsym = asym;
    }

    public Time getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Time creationTime) {
        this.creationTime = creationTime;
    }

    public Time getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Time expiredTime) {
        this.expiredTime = expiredTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
