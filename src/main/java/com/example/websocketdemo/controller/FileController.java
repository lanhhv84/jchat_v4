package com.example.websocketdemo.controller;

import com.example.websocketdemo.crypt.Crypto;
import com.example.websocketdemo.model.FileInfo;
import com.example.websocketdemo.model.User;
import com.example.websocketdemo.payload.UploadFileResponse;
import com.example.websocketdemo.service.FileInfoService;
import com.example.websocketdemo.service.FileStorageService;
import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private Crypto crypto;

    @PostMapping("/uploadFile")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file,
                                          @RequestParam("option") String option,
                                          @RequestParam("owner") String username,
                                          @RequestParam("receiver") String receiver) {
        System.out.println("Receiving file");
        System.out.print("Length: ");
        System.out.println(file.getSize());
        System.out.print("Receiver:");
        System.out.println(receiver);
        Map<String, String> res = null;
        try {
            res = fileStorageService.storeFile(file, option, username);
        } catch (IOException ex) {
            res = new HashMap<>();
        }
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(res.get("new"))
                .toUriString();
        res.put("download", fileDownloadUri);
        res.put("type", file.getContentType());

        return res;
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return null;

    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public void downloadFile(@PathVariable String fileName,
                             HttpServletRequest request, HttpServletResponse response) {

        Resource resource = fileStorageService.loadFileAsResource(fileName);
        File plainFile = null;
        try {
            File encryptedFile = resource.getFile();
            byte[] encryptedData = Files.readAllBytes(encryptedFile.toPath());

            FileInfo fileInfo = fileInfoService.findFirstByNewName(encryptedFile.getName());
            byte[] key = fileInfo.getKey();
            byte[] plainData = null;
            User owner = fileInfo.getOwner();
            if (owner.getLastPrivate().toPrivate() == null) {
                System.out.println("Key is null");
                System.out.println(Hex.encodeHexString(owner.getLastPrivate().getKey()));
            }

            plainData = crypto.decrypt(encryptedData, fileInfo.getAlgorithm(), key, owner.getLastPrivate().toPrivate());


            plainFile = fileStorageService.getFileStorageLocation().resolve(fileInfo.getOldName()).toFile();
            plainFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(plainFile);
            fileOutputStream.write(plainData);

            fileOutputStream.close();


        } catch (IOException ex) {

        }


        try {
            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(plainFile);
            IOUtils.copy(in, out);
            out.close();
            in.close();
            plainFile.delete();
        }
        catch (IOException ex) {

        }


    }

}