package com.example.websocketdemo.db;

import com.example.websocketdemo.model.Key;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KeyDB extends JpaRepository<Key, Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM key_data WHERE owner_id=?1 and is_public=?2 ORDER BY created_time LIMIT 1")
    Key findLast(int ownerId, boolean isPublic);
}
