package com.example.websocketdemo.model;

import javax.persistence.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Entity
@Table(name = "user_data")
public class User {

    public User() {
        this("", "");
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true)
    private int id;


    @Column(name = "u_username", unique = true, length = 32)
    private String username;

    @Column(name = "u_password", unique = true, length = 127)
    private String password;

    @ManyToMany(mappedBy = "users")
    private Set<Room> rooms = new HashSet<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER  )
    private Set<Key> keys = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public Set<Key> getKeys() {
        return keys;
    }

    public void setKeys(Set<Key> keys) {
        this.keys = keys;
    }

    public Key getLastPublic() {
        Optional<Key> key = getKeys().stream().filter(new Predicate<Key>() {
            @Override
            public boolean test(Key key) {
                return key.isPublic();
            }
        }).max(new Comparator<Key>() {
            @Override
            public int compare(Key o1, Key o2) {
                return o1.getCreationTime().compareTo(o2.getCreationTime());
            }
        });

        return key.isPresent() ? key.get() : null;

    }

    public Key getLastPrivate() {
        Optional<Key> key = getKeys().stream().filter(new Predicate<Key>() {
            @Override
            public boolean test(Key key) {
                return !key.isPublic();
            }
        }).max(new Comparator<Key>() {
            @Override
            public int compare(Key o1, Key o2) {
                return o1.getCreationTime().compareTo(o2.getCreationTime());
            }
        });

        return key.get();
    }
}
