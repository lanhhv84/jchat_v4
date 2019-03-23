package com.example.websocketdemo.service;

import com.example.websocketdemo.db.FileInfoDB;
import com.example.websocketdemo.model.FileInfo;
import org.springframework.stereotype.Service;

@Service
public class FileInfoService extends IService<FileInfo, FileInfoDB> {

    public FileInfo findFirstByNewName(String newName) {
        return db.findFirstByNewName(newName);
    }
}
