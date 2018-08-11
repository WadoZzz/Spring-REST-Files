package com.rest.files.springrestfiles.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Value("${upload.path}")
    private String uploadFolder;

    @Override
    public void save(List<MultipartFile> multipartFiles) {
        for (MultipartFile file : multipartFiles) {
            File serverFile = new File(uploadFolder + File.separator + file.getOriginalFilename());
            try (FileOutputStream stream = new FileOutputStream(serverFile)) {
                byte[] bytes = file.getBytes();
                stream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResponseEntity<?> download(HttpServletResponse response, String fileName) {
        int copy = 0;
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(uploadFolder + fileName))) {
            copy = FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (FileNotFoundException e1) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(copy, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> upload(MultipartFile[] files) {
        String uploadedFileName = Arrays.stream(files).map(MultipartFile::getOriginalFilename)
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
        if (StringUtils.isEmpty(uploadedFileName)) {
            return new ResponseEntity<>("Please select a file!", HttpStatus.BAD_REQUEST);
        }
        save(Arrays.asList(files));
        return new ResponseEntity<>("Successfully uploaded - " + uploadedFileName, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> rename(String oldFile, String newFile) {
        File oldFile1 = new File(uploadFolder + oldFile);
        File newFile2 = new File(uploadFolder + newFile);
        if (oldFile1.renameTo(newFile2)) {
            return new ResponseEntity<>(newFile, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> delete(String file) {
        File deleteFile = new File(uploadFolder + file);
        if (deleteFile.exists()) {
            deleteFile.delete();
        }
        return new ResponseEntity<>(deleteFile, HttpStatus.NOT_FOUND);
    }
}