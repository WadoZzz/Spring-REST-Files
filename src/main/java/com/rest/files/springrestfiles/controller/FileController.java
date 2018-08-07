package com.rest.files.springrestfiles.controller;

import com.rest.files.springrestfiles.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    //Single upload file
    @PostMapping(value = "/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return fileService.uploadFile(file);
    }

    //Multi upload file
    @PostMapping(value = "/uploadMulti")
    public ResponseEntity uploadMultipleFile(@RequestParam("files") MultipartFile[] files) {
        return fileService.uploadMultipleFile(files);
    }
    //Get all files
    @GetMapping(path = "/all")
    public ResponseEntity getAll() {
        return fileService.getAllFiles();
    }

    //Get one file
    @GetMapping(value = "/file/{fileName:.+}")
    public ResponseEntity get(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        return fileService.downLoadFile(response, fileName);
    }

    //Rename file
    @PutMapping(value = "/file/{fileName:.+}")
    public ResponseEntity update(@PathVariable("fileName") String file, @RequestParam String renFile) {
        return fileService.updateUploadFile(file, renFile);

    }

    //Delete file
    @DeleteMapping(value = "/file/{fileName:.+}")
    public ResponseEntity delete(@PathVariable("fileName") String file) {
        return fileService.deleteUploadFile(file);

    }
}