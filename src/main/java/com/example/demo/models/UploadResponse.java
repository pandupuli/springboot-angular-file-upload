package com.example.demo.models;

public class UploadResponse
{
   private String fileId;
   
   private String fileName;
   
   private String mimeType;
   
   private boolean operationSuccess;
   
   private String statusMessage;

   public String getFileId()
   {
      return fileId;
   }

   public void setFileId(String fileId)
   {
      this.fileId = fileId;
   }

   public String getFileName()
   {
      return fileName;
   }

   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }

   public boolean isOperationSuccess()
   {
      return operationSuccess;
   }

   public void setOperationSuccess(boolean operationSuccess)
   {
      this.operationSuccess = operationSuccess;
   }

   public String getStatusMessage()
   {
      return statusMessage;
   }

   public void setStatusMessage(String statusMessage)
   {
      this.statusMessage = statusMessage;
   }

   public String getMimeType()
   {
      return mimeType;
   }

   public void setMimeType(String mimeType)
   {
      this.mimeType = mimeType;
   }
}
