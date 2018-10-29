package com.rest.files.springrestfiles.controller;

import com.rest.files.springrestfiles.service.FileServiceImpl;
import com.rest.files.springrestfiles.service.IFileService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "file")
public class FileController {

	@Value("${uploadPath}")
	private String uploadFolder;

	final static Logger logger = Logger.getLogger(FileServiceImpl.class);

	private IFileService IFileService;

	@Autowired
	public FileController(IFileService IFileService) {
		this.IFileService = IFileService;
	}

	/*
	 * Upload file on server
	 */
	@PostMapping(value = "/upload")
	public ResponseEntity upload(@RequestParam("files") MultipartFile[] files) {
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

	/*
	 * Get file from server
	 */
	@GetMapping(value = "/{fileName}")
	public ResponseEntity download(HttpServletResponse response, @PathVariable("fileName") String fileName) {
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

	/*
	 * Rename file from server
	 */
	@PutMapping(value = "/{fileName}")
	public ResponseEntity rename(@PathVariable("fileName") String file, @RequestParam String renameFile) {
		File oldFile1 = new File(uploadFolder + File.separator + file);
        File newFile2 = new File(uploadFolder + File.separator + renameFile);
        if (oldFile1.renameTo(newFile2)) {
            logger.info("File " + file + " successfully renamed to " + renameFile);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            logger.error("File not exist");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
	}

	/*
	 * Delete file from server
	 */
	@DeleteMapping(value = "/{fileName}")
	public ResponseEntity delete(@PathVariable("fileName") String file) {
		return IFileService.delete(file);
	}

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
}