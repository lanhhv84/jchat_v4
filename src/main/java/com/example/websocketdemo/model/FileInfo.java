package com.example.websocketdemo.model;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "file_info")
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int id;

    @Column(name = "old_name")
    private String oldName;

    @Column(name = "new_name")
    private String newName;

    @Column(name = "crypto_alg")
    private String algorithm;

    @Column(name = "send_time")
    private Date sendTime;

    @Column(name = "symmetric_key", length = 2047)
    private String key;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name = "owner_id")
    private User owner;

    public FileInfo() {
        oldName = "";
        newName = "";
        owner = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
