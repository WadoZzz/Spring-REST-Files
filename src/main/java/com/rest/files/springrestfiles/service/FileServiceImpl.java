package com.rest.files.springrestfiles.service;

import org.apache.log4j.Logger;
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
public class FileServiceImpl implements IFileService {

    final static Logger logger = Logger.getLogger(FileServiceImpl.class);

    @Value("${uploadPath}")
    private String uploadFolder;

    @Override
    public void save(List<MultipartFile> multipartFiles) {
        File dir = new File(uploadFolder);
        if (!dir.exists() && !dir.mkdirs()) {
            dir.mkdir();
        }
        multipartFiles.forEach(file -> {
            File serverFile = new File(uploadFolder + File.separator + file.getOriginalFilename());
            try (FileOutputStream stream = new FileOutputStream(serverFile)) {
                byte[] bytes = file.getBytes();
                stream.write(bytes);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        logger.info("File successfully saved");
    }

    @Override
    public ResponseEntity<?> download(HttpServletResponse response, String fileName) {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(uploadFolder + File.separator + fileName))) {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (FileNotFoundException e1) {
            logger.error(e1.getMessage(), e1);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        logger.info("File " + fileName + " successfully downloaded");
        return new ResponseEntity<>(fileName, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> upload(MultipartFile[] files) {
        String uploadedFileName = Arrays.stream(files).map(MultipartFile::getOriginalFilename)
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
        if (StringUtils.isEmpty(uploadedFileName)) {
            logger.error("Please select a file!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        save(Arrays.asList(files));
        logger.info("File " + uploadedFileName + " successfully uploaded");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> rename(String oldFile, String newFile) {
        File oldFile1 = new File(uploadFolder + File.separator + oldFile);
        File newFile2 = new File(uploadFolder + File.separator + newFile);
        if (oldFile1.renameTo(newFile2)) {
            logger.info("File " + oldFile + " successfully renamed to " + newFile);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            logger.error("File not exist");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> delete(String file) {
        File deleteFile = new File(uploadFolder + File.separator + file);
        if (deleteFile.exists()) {
            deleteFile.delete();
        } else {
            logger.error("File " + file + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        logger.info("File " + file + " successfully been deleted");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}