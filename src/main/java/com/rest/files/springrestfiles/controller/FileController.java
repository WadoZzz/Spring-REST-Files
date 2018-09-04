package com.rest.files.springrestfiles.controller;

import com.rest.files.springrestfiles.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "file")
public class FileController {

    private IFileService IFileService;

    @Autowired
    public FileController(IFileService IFileService) {
        this.IFileService = IFileService;
    }

    /*
    Upload file on server
     */
    @PostMapping(value = "/upload")
    public ResponseEntity upload(@RequestParam("files") MultipartFile[] files) {
        return IFileService.upload(files);
    }

    /*
     Get file from server
      */
    @GetMapping(value = "/{fileName}")
    public ResponseEntity download(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        return IFileService.download(response, fileName);
    }

    /*
    Rename file from server
     */
    @PutMapping(value = "/{fileName}")
    public ResponseEntity rename(@PathVariable("fileName") String file, @RequestParam String renameFile) {
        return IFileService.rename(file, renameFile);
    }

    /*
   Delete file from server
    */
    @DeleteMapping(value = "/{fileName}")
    public ResponseEntity delete(@PathVariable("fileName") String file) {
        return IFileService.delete(file);
    }
}