package com.ema.ema_backend.domain.chatroom.service;

import com.ema.ema_backend.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final S3Client s3;
    private final MessageRepository messageRepo;

    @Value("${app.s3.bucket-name}")
    private String bucket;

    @Value("${app.s3.base-url}")
    private String baseUrl;

    public String uploadAndSave(Long chatRoomId, MultipartFile file) {
        // 1) 키(파일명) 생성: UUID + 원본 확장자
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String key = "chatrooms/" + chatRoomId + "/" + UUID.randomUUID() + "." + ext;

        // 2) S3에 업로드
        PutObjectRequest por = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3.putObject(por, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 I/O Exception",e);
        }

        // 3) S3 URL 조립
        String url = baseUrl + "/" + key;

        return url;
    }
}
