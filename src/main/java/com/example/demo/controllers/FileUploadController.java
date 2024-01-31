package com.example.demo.controllers;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.models.UploadResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class FileUploadController {
	private static Logger _logger = LoggerFactory.getLogger(FileUploadController.class);

	@Value("${resource.basedir}")
	private String uploadFileBaseDir;

   @PostMapping("/api/fileUpload")
   public ResponseEntity<UploadResponse> uploadFile(
         @RequestParam("MediaUpload") MultipartFile srcFile)
   {
      UploadResponse resp = new UploadResponse();
      if (srcFile != null)
      {
         String origFileName = srcFile.getOriginalFilename();
         String contentType = srcFile.getContentType();
         
         System.out.println("Uploaded File Name: " + origFileName);
         System.out.println("Uploaded File MIME Type: " + contentType);
         
         FileOutputStream writeToFile = null;
         try
         {
            String fileName = String.format("%s/%s", uploadFileBaseDir, origFileName);
            File destMediaFile = new File(fileName);
            
            writeToFile = new FileOutputStream(destMediaFile);
            IOUtils.copy(srcFile.getInputStream(), writeToFile);
            writeToFile.flush();
            
            _logger.info(String.format("Uploaded file has been saved to [%s].", fileName));
            
            resp.setFileId("000000001");
            resp.setFileName(origFileName);
            resp.setMimeType(contentType);
            resp.setOperationSuccess(true);
            resp.setStatusMessage(String.format("File [%s] has been saved successfully.", origFileName));
         }
         catch (Exception ex)
         {
            String errorDetails = String.format("Exception occurred when write media file to disk: %s", ex.getMessage());
            _logger.error("MediaMgmtServiceImpl.saveMediaFile: " + errorDetails);
            throw new RuntimeException(ex);
         }
         finally
         {
            if (writeToFile != null)
            {
               try
               {
                  writeToFile.close();
               }
               catch(Exception ex)
               { }
            }
         }
      }
      else
      {
         resp.setFileId("");
         resp.setFileName("");
         resp.setMimeType("");
         resp.setOperationSuccess(false);
         resp.setStatusMessage("HTTP request does not contain a file object.");
      }
      
      return ResponseEntity.ok(resp);
   }
}
