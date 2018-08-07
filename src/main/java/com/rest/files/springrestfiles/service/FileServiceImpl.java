package com.rest.files.springrestfiles.service;

import com.rest.files.springrestfiles.exception.MyFileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    private static String UPLOADED_FOLDER = "C://uploads//";

    @Override
    public void saveUploadedFiles(List<MultipartFile> multipartFiles) {
        File dir = new File(UPLOADED_FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (MultipartFile file : multipartFiles) {
            if (file.isEmpty()) {
                continue;
            }
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
                    FileOutputStream stream = new FileOutputStream(serverFile);
                    stream.write(bytes);
                    stream.close();
                } catch (IOException e) {
                    try {
                        throw new IOException("You failed to upload " + file.getOriginalFilename() + e.getMessage());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public ResponseEntity<?> getAllFiles() {
        File dir = new File(UPLOADED_FOLDER);
        File[] files = dir.listFiles();
        ArrayList list = new ArrayList();
        if (files != null) {
            for (File file : files) {
                list.add(file.getName());
            }
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            try {
                throw new MyFileNotFoundException("Not exist files");
            } catch (MyFileNotFoundException e) {
                return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
            }
        }
    }

    @Override
    public ResponseEntity<?> downLoadFile(HttpServletResponse response, String fileName) {
        File file = new File(UPLOADED_FOLDER + fileName);
        InputStream inputStream;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        int copy;
        try {
            copy = FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(copy, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("please select a file!", HttpStatus.OK);
        }
        saveUploadedFiles(Collections.singletonList(file));
        return new ResponseEntity<>("Successfully uploaded - " + file.getOriginalFilename(), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> uploadMultipleFile(MultipartFile[] files) {
        String uploadedFileName = Arrays.stream(files).map(MultipartFile::getOriginalFilename)
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
        if (StringUtils.isEmpty(uploadedFileName)) {
            return new ResponseEntity<>("please select a file!", HttpStatus.OK);
        }
        saveUploadedFiles(Arrays.asList(files));
        return new ResponseEntity<>("Successfully uploaded - " + uploadedFileName, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> updateUploadFile(String oldFile, String newFile) {
        File oldFile1 = new File(UPLOADED_FOLDER + oldFile);
        File newFile2 = new File(UPLOADED_FOLDER + newFile);
        if (oldFile1.renameTo(newFile2)) {
            return new ResponseEntity<>(newFile, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> deleteUploadFile(String file) {
        File deleteFile = new File(UPLOADED_FOLDER + file);
        if (deleteFile.exists()) {
            deleteFile.delete();
        }
        return new ResponseEntity<>(deleteFile, HttpStatus.NOT_FOUND);

    }

}