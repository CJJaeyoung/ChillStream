package com.example.chillStream.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {
    public String uploadFile(String uploadPath, String oriContentsName, byte[] fileData) throws Exception{
        UUID uuid = UUID.randomUUID();
        String extension = oriContentsName.substring(oriContentsName.lastIndexOf("."));
        String saveFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath +"/"+saveFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl); // 파일 쓰기 객체 생성
        fos.write(fileData);
        fos.close();
        return saveFileName;
    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);

        if(deleteFile.exists()){
            deleteFile.delete();
            log.info("파일 삭제");
        }else{
            log.info("파일 존재하지 않음");
        }
    }
}
