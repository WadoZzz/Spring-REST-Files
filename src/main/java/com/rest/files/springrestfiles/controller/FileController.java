package com.rest.files.springrestfiles.controller;

import com.rest.files.springrestfiles.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "file")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity upload(@RequestParam("files") MultipartFile[] files) {
        return fileService.upload(files);
    }

    @GetMapping(value = "/{fileName}")
    public ResponseEntity download(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        return fileService.download(response, fileName);
    }

    @PutMapping(value = "/{fileName}")
    public ResponseEntity rename(@PathVariable("fileName") String file, @RequestParam String renameFile) {
        return fileService.rename(file, renameFile);
    }

    @DeleteMapping(value = "/{fileName}")
    public ResponseEntity delete(@PathVariable("fileName") String file) {
        return fileService.delete(file);
    }
}