package com.rest.files.springrestfiles.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface FileService {

    void saveUploadedFiles(List<MultipartFile> multipartFiles);

    ResponseEntity updateUploadFile(String oldFile, String newFile);

    ResponseEntity deleteUploadFile(String file);

    ResponseEntity downLoadFile(HttpServletResponse response, String fileName);

    ResponseEntity getAllFiles();

    ResponseEntity uploadFile(MultipartFile file);

    ResponseEntity uploadMultipleFile(MultipartFile[] files);
}
