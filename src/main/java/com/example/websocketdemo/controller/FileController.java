package com.example.websocketdemo.controller;

import com.example.websocketdemo.crypt.Crypto;
import com.example.websocketdemo.model.FileInfo;
import com.example.websocketdemo.payload.UploadFileResponse;
import com.example.websocketdemo.service.FileInfoService;
import com.example.websocketdemo.service.FileStorageService;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
                                          @RequestParam("owner") String username) {
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
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {

        Resource resource = fileStorageService.loadFileAsResource(fileName);
        File plainFile = null;
        try {
            File encryptedFile = resource.getFile();
            byte[] encryptedData = Files.readAllBytes(encryptedFile.toPath());

            FileInfo fileInfo = fileInfoService.findFirstByNewName(encryptedFile.getName());
            String key = fileInfo.getKey();
            byte[] plainData = null;
            try {
                plainData = crypto.decrypt(encryptedData, fileInfo.getAlgorithm(), key);
            } catch (InvalidAlgorithmParameterException ex) {
                ex.printStackTrace();
            }


            plainFile = fileStorageService.getFileStorageLocation().resolve(fileInfo.getOldName()).toFile();
            plainFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(plainFile);
            fileOutputStream.write(plainData);


        } catch (IOException ex) {

        }


        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + plainFile.getName() + "\"")
                .body(resource);
    }

}