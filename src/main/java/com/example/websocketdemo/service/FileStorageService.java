package com.example.websocketdemo.service;

import com.example.websocketdemo.crypt.CryptoException;
import com.example.websocketdemo.exception.FileStorageException;
import com.example.websocketdemo.exception.MyFileNotFoundException;
import com.example.websocketdemo.property.FileStorageProperties;

import net.bytebuddy.asm.Advice.This;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@Service
public class FileStorageService {

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

    public String storeFile(MultipartFile file, String option) throws CryptoException {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            if (option.equals("AES")){
                byte[] inputBytes = file.getBytes();
                String key = RandomStringUtils.randomAscii(16);
                Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] outputBytes = cipher.doFinal(inputBytes);
                byte[] result = ArrayUtils.addAll(outputBytes, key.getBytes());
                File outputFile = new File(fileName);
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                outputStream.write(result);
                Files.copy(new FileInputStream(outputFile), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                outputStream.close();
            } else if (option.equals("RSA")){

            } else if (option.equals("BlowFish")){
                byte[] inputBytes = file.getBytes();
                String key = RandomStringUtils.randomAscii(16);
                Key secretKey = new SecretKeySpec(key.getBytes(), "Blowfish");
                Cipher cipher = Cipher.getInstance("Blowfish");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] outputBytes = cipher.doFinal(inputBytes);
                byte[] result = ArrayUtils.addAll(outputBytes, key.getBytes());
                File outputFile = new File(fileName);
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                outputStream.write(result);
                Files.copy(new FileInputStream(outputFile), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                outputStream.close();
            }
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException ex) {
                throw new CryptoException("Error encrypting/decrypting file", ex);
        }

    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                System.out.println(resource);
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}