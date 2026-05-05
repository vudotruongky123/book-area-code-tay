package com.thientri.book_area.service.minio;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws Exception {
        // 1. Đổi tên file có tên không trùng nhau
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // 2. Lấy luông dữ liệu và kích thước của file
        InputStream inputStream = file.getInputStream();
        long size = file.getSize(); // Kích thước file
        String contentType = file.getContentType(); // Kiểu file

        // 3. Đẩy file lên MinIO
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(uniqueFilename)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build());

        // 4. Trả về tên file đã lưu
        return uniqueFilename;
    }

    // Hàm lấy link xem/tải file có thời hạn
    public String getBookUrl(String fileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .expiry(2, TimeUnit.HOURS) // Link sẽ tự động hết hạn sau 2 giờ
                        .build());
    }

    // Xóa file
    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build());
    }
}
