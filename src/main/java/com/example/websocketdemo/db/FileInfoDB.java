package com.example.websocketdemo.db;

import com.example.websocketdemo.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileInfoDB extends JpaRepository<FileInfo, Integer> {
    @Query(value = "SELECT * FROM file_info WHERE new_name=?1 LIMIT 1", nativeQuery = true)
    FileInfo findFirstByNewName(String newName);
}
