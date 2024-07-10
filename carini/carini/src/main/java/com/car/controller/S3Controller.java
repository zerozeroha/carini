package com.car.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.car.impl.S3Service;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/s3")
@AllArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            s3Service.uploadFile(file);
            
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file.");
        }
    }

    @GetMapping("/download/{filename}")
    public String downloadFile(@PathVariable("filename") String filename,Model model) throws IOException {
        try {
            // Amazon S3에서 파일 다운로드
            String data = s3Service.downloadFile(filename);
            model.addAttribute("file_link", data);

            // byte 배열을 ResponseEntity로 포장하여 반환
            return null;
        } catch (RuntimeException e) {
            return null;
        }
    }
}
