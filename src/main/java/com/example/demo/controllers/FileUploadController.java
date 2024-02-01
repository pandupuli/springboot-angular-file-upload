package com.example.demo.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.models.Gallery;
import com.example.demo.models.UploadResponse;

import io.micrometer.common.util.StringUtils;

@RestController
public class FileUploadController {
	private static Logger _logger = LoggerFactory.getLogger(FileUploadController.class);

	@Value("${resource.basedir}")
	private String uploadFileBaseDir;
	
	//working: from Angular App.
	@PostMapping("/api/fileUpload1")
	public ResponseEntity<UploadResponse> uploadFile1(@RequestParam("MediaUpload") MultipartFile srcFile, 
			@RequestParam("id") int id, @RequestParam("userName") String userName,
			@RequestParam("password") String password,
			@RequestParam("address") String address) {

		UploadResponse resp = processFileUpload(srcFile, userName);
		
		boolean status =  resp.isOperationSuccess() ? saveUserDetailsToFile(userName, password, address): false;
		
		return ResponseEntity.ok(resp);
	}

	private boolean saveUserDetailsToFile(String userName, String password, String address) {
		
		boolean result =  Boolean.TRUE;
		StringBuilder sbFileContent = new StringBuilder();
		sbFileContent.append(userName).append("|");
		sbFileContent.append(password).append("|");
		sbFileContent.append(address);
		
		StringBuilder sbFileURL = new StringBuilder();
		sbFileURL.append(uploadFileBaseDir);
		sbFileURL.append("//");
		sbFileURL.append(userName);
		sbFileURL.append("_");
		sbFileURL.append(getDateTime());
		sbFileURL.append(".txt");
				
		try {
			Files.write(Paths.get( sbFileURL.toString() ), sbFileContent.toString().getBytes() );		
		} catch (IOException e) {
			e.printStackTrace();
			result = Boolean.FALSE;
		}
		
		return result;
	}

	//working : from Angular APP... uploading only image file.
	@PostMapping("/api/fileUpload")
	public ResponseEntity<UploadResponse> uploadFile(@RequestParam("MediaUpload") MultipartFile srcFile) {
		
		System.out.println("STEP1--> FileUploadController");
		UploadResponse resp = processFileUpload(srcFile, "");
		return ResponseEntity.ok(resp);
	}
	
	
	//working : from postman
	@PostMapping(value = "/api/upload22",
			consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE })
//	@PostMapping(value = "/api/upload22")
	public ResponseEntity<UploadResponse> uploadFilesExample22(@RequestBody Gallery gallery) {
		
		_logger.info("galleries details: " + gallery);
		
		MultipartFile srcFile = gallery.getFile();
		UploadResponse resp = processFileUpload(srcFile, "");	

		return ResponseEntity.ok(resp);
	}

	private UploadResponse processFileUpload(MultipartFile srcFile, String userName) {

		UploadResponse resp = new UploadResponse();

		if (srcFile != null) {
			String origFileName = srcFile.getOriginalFilename();
			String contentType = srcFile.getContentType();			
			String editedFileName = StringUtils.isEmpty(userName)? origFileName : userName.concat("_").concat(origFileName);		

			FileOutputStream writeToFile = null;
			try {
				String fileName = String.format("%s/%s", uploadFileBaseDir, editedFileName);
				File destMediaFile = new File(fileName);

				writeToFile = new FileOutputStream(destMediaFile);
				IOUtils.copy(srcFile.getInputStream(), writeToFile);
				writeToFile.flush();

				_logger.info(String.format("Uploaded file has been saved to [%s].", fileName));

				resp.setFileId("000000001");
				resp.setFileName(editedFileName);
				resp.setMimeType(contentType);
				resp.setOperationSuccess(true);
				resp.setStatusMessage(String.format("File [%s] has been saved successfully.", editedFileName));
				
			} catch (Exception ex) {
				String errorDetails = String.format("Exception occurred when write media file to disk: %s",
						ex.getMessage());
				_logger.error("MediaMgmtServiceImpl.saveMediaFile: " + errorDetails);
				
				throw new RuntimeException(ex);
			} finally {
				if (writeToFile != null) {
					try {
						writeToFile.close();
					} catch (Exception ex) {
					}
				}
			}
		} else {
			resp.setFileId("");
			resp.setFileName("");
			resp.setMimeType("");
			resp.setOperationSuccess(false);
			resp.setStatusMessage("HTTP request does not contain a file object.");
		}

		return resp;
	}
	
	private final static String getDateTime()  
	{  
		final String DEFAULT_FILE_PATTERN = "yyyy-MM-dd-HH-mm-ss";		
		Date date = new Date(System.currentTimeMillis());
	    SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FILE_PATTERN);
	    return format.format(date); 
	} 
}
