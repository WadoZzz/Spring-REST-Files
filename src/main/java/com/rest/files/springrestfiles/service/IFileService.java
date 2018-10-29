package com.rest.files.springrestfiles.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IFileService {

    void save(List<MultipartFile> multipartFiles);

    ResponseEntity rename(String oldFile, String newFile);

    ResponseEntity delete(String file);

   

   
}
