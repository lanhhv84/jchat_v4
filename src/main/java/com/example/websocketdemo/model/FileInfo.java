package com.example.websocketdemo.model;


import javax.persistence.*;

@Entity
@Table(name = "file_data")
public class FileInfo  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int id;

    @Column(name = "old_name")
    private String oldName;
    @Column(name = "new_name")
    private String newName;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name = "owner_id")
    private User owner;

    public FileInfo() {
        oldName = "";
        newName = "";
        owner = null;
    }
}
