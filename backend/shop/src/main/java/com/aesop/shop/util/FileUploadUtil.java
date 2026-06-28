package com.aesop.shop.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class FileUploadUtil {

    // 파일 업로드
    public static String upload(MultipartFile file, String uploadDir) throws Exception {
        if (file.isEmpty()) {
            return null;
        }

        // "1234-4321-5678" + "_" + "a.png" = 1234-4321-5678_a.png
        String originalName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + originalName;

        // 업로드 디렉터리 없으면 자동 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File saveFile = new File(uploadDir + fileName);
        file.transferTo(saveFile);

        return fileName;
    }

    // 파일 삭제
    public static void deleteFile(String fileName, String uploadDir) {
        if (fileName != null && !fileName.isEmpty()) {
            File file = new File(uploadDir + fileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}