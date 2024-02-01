package com.example.demo.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;



@RestController
public class FileDownloadController
{
   private static Logger _logger = LoggerFactory.getLogger(FileDownloadController.class);

   @Value("${resource.basedir}")
   private String resourceFileBaseDir;
   
   @RequestMapping(value="/api/downloadFile/{fileName}", method=RequestMethod.GET)
   public ResponseEntity<StreamingResponseBody> downloadFile(
         @PathVariable("fileName") String fileName)
   {
	   System.out.println("STEP2--> FileDownloadController");
      System.out.println("Received file name: " + fileName);
      ResponseEntity<StreamingResponseBody> retVal = null;
      if (StringUtils.hasText(fileName))
      {
         String fileFullPath = String.format("%s/%s", resourceFileBaseDir, fileName);
         
         try
         {
            File f = new File(fileFullPath);
            if (f.exists() && f.isFile())
            {
               StreamingResponseBody respBody = loadFileDataIntoResponseStream(fileFullPath);
               if (respBody != null)
               {
                  Path path = Paths.get(fileFullPath);
                  String mimeType = Files.probeContentType(path);
                  
                  retVal = ResponseEntity.ok()
                        .header("Content-type", mimeType)
                        .header("Content-length", String.format("%d", f.length()))
                        .body(respBody);
               }
               else
               {
                  retVal = ResponseEntity.notFound().build();
               }
            }
            else
            {
               _logger.error(String.format("File not found: %s", fileFullPath));
               retVal = ResponseEntity.internalServerError().build();
            }
         }
         catch(Exception ex)
         {
            _logger.error(String.format("Exception occurred: %s", ex.getMessage()));
            retVal = ResponseEntity.internalServerError().build();
         }
      }
      else
      {
         _logger.error(String.format("Invalid file name: %s", fileName));
         retVal = ResponseEntity.badRequest().build();
      }
      
      return retVal;
   }
   
   private StreamingResponseBody loadFileDataIntoResponseStream(String imageFileFullPath)
   {
      StreamingResponseBody retVal = null;
      File imageFile = new File(imageFileFullPath);
      if (imageFile.exists() && imageFile.isFile())
      {
         try
         {
            retVal = new StreamingResponseBody()
            {
               @Override
               public void writeTo(OutputStream outputStream) throws IOException
               {
                  FileInputStream fs = null;
                  try
                  {
                     fs = new FileInputStream(imageFile);
                     IOUtils.copy(fs, outputStream);
                     outputStream.flush();
                  }
                  finally                           
                  {
                     outputStream.close();
                     if (fs != null)
                     {
                        fs.close();
                     }
                  }
               }
            };
         }
         catch (Exception ex)
         {
            _logger.error(String.format("Exception: %s", ex.getMessage()));
            retVal = null;
         }
      }
      else
      {
         _logger.error(String.format("Unable to find image file [%s]. file does not exist. Error 404", imageFileFullPath));
         retVal = null;
      }
      
      return retVal;
   }
}
