package com.rest.files.springrestfiles.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface FileService {

    void save(List<MultipartFile> multipartFiles);

    ResponseEntity rename(String oldFile, String newFile);

    ResponseEntity delete(String file);

    ResponseEntity download(HttpServletResponse response, String fileName);

    ResponseEntity upload(MultipartFile[] files);
}
