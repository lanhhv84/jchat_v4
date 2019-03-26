package com.example.websocketdemo.service;

import com.example.websocketdemo.crypt.Crypto;
import com.example.websocketdemo.crypt.Hasher;
import com.example.websocketdemo.exception.FileStorageException;
import com.example.websocketdemo.exception.MyFileNotFoundException;
import com.example.websocketdemo.model.FileInfo;
import com.example.websocketdemo.model.Key;
import com.example.websocketdemo.model.User;
import com.example.websocketdemo.property.FileStorageProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileStorageService {


    @Autowired
    Hasher hasher;

    @Autowired
    UserService userService;

    @Autowired
    FileInfoService fileInfoService;

    @Autowired
    Crypto crypto;

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Map<String, String> storeFile(MultipartFile file, String option, String username) throws IOException {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        User owner = userService.findOne(username);

        FileInfo fileInfo = new FileInfo();
        if (fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }

        byte[] inputBytes = file.getBytes();

        System.out.println(inputBytes.length);
        Key publicKey = userService.findOne(username).getLastPublic();
        Map<String, byte[]> values = crypto.encrypt(inputBytes, option + "2", publicKey.toPublic());
        byte[] encrypted = Base64.encodeBase64(values.get("value"));
        fileInfo.setKey(values.get("key"));

        String newFileName = hasher.hash(encrypted);
        Path targetLocation = this.fileStorageLocation.resolve(newFileName);
        File newFile = new File(targetLocation.toUri());
        newFile.createNewFile();
        FileOutputStream newFileOutputStream = new FileOutputStream(newFile);
        newFileOutputStream.write(encrypted);


        fileInfo.setAlgorithm(option);
        fileInfo.setNewName(newFileName);
        fileInfo.setOldName(fileName);
        fileInfo.setSendTime(Calendar.getInstance().getTime());
        fileInfo.setOwner(owner);

        fileInfoService.add(fileInfo);


        Map<String, String> res = new HashMap<>();
        res.put("old", fileName);
        res.put("new", newFileName);
        res.put("size", String.valueOf(file.getSize()));


        return res;

    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                System.out.println(resource);
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}