package com.thientri.book_area.service.catalog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thientri.book_area.dto.request.catalog.BookImageRequest;
import com.thientri.book_area.dto.response.catalog.BookImageResponse;
import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.model.catalog.BookImage;
import com.thientri.book_area.repository.catalog.BookImageRepository;
import com.thientri.book_area.repository.catalog.BookRepository;
import com.thientri.book_area.service.minio.MinioService;

@Service
public class BookImageService {

    private final BookImageRepository bookImageRepository;
    private final BookRepository bookRepository;
    private final MinioService minioService; // Bổ sung MinioService

    public BookImageService(BookImageRepository bookImageRepository, BookRepository bookRepository,
            MinioService minioService) {
        this.bookImageRepository = bookImageRepository;
        this.bookRepository = bookRepository;
        this.minioService = minioService;
    }

    public List<BookImageResponse> getAllBookImages() {
        List<BookImageResponse> list = new ArrayList<>();
        for (BookImage bookImage : bookImageRepository.findAll()) {
            list.add(mapToResponse(bookImage));
        }
        return list;
    }

    // Ánh xạ sang DTO và nối link tĩnh Public từ MinIO
    private BookImageResponse mapToResponse(BookImage bookImage) {
        String url = null;
        if (bookImage.getImageFileName() != null) {
            // Sử dụng hàm getPublicImageUrl để tạo link không thời hạn cho ảnh
            url = minioService.getPublicImageUrl(bookImage.getImageFileName());
        }
        return BookImageResponse.builder()
                .id(bookImage.getId())
                .bookId(bookImage.getBook().getId())
                .imageUrl(url)
                .build();
    }

    @Transactional
    public BookImageResponse createBookImage(BookImageRequest bookImageRequest) {
        Book book = bookRepository.findById(bookImageRequest.getBookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach de them moi image cho sach!"));

        BookImage newBookImage = new BookImage();
        newBookImage.setBook(book);

        String uploadedFileName = null; // Biến tạm để dọn rác nếu lỗi

        try {
            // 1. Upload ảnh lên MinIO nếu có file gửi lên
            if (bookImageRequest.getFileImage() != null && !bookImageRequest.getFileImage().isEmpty()) {
                uploadedFileName = minioService.uploadFile(bookImageRequest.getFileImage());
                newBookImage.setImageFileName(uploadedFileName);
            }

            // 2. Lưu Database
            BookImage savedImage = bookImageRepository.save(newBookImage);
            return mapToResponse(savedImage);

        } catch (Exception e) {
            // 3. Dọn rác MinIO nếu quá trình lưu Database thất bại
            if (uploadedFileName != null) {
                try {
                    minioService.deleteFile(uploadedFileName);
                } catch (Exception ex) {
                    System.err.println("Khong the don rac anh tren MinIO: " + ex.getMessage());
                }
            }
            throw new RuntimeException("Loi them anh, da hoan tac giao dich: " + e.getMessage(), e);
        }
    }

    @Transactional
    public BookImageResponse updateBookImage(Long id, BookImageRequest bookImageRequest) {
        BookImage bookImage = bookImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay anh de cap nhat!"));

        Book book = bookRepository.findById(bookImageRequest.getBookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach de cap nhat anh!"));

        String newUploadedFileName = null; // Biến tạm cho file mới

        try {
            // 1. Xử lý đổi file ảnh
            if (bookImageRequest.getFileImage() != null && !bookImageRequest.getFileImage().isEmpty()) {
                // Upload ảnh mới lên MinIO
                newUploadedFileName = minioService.uploadFile(bookImageRequest.getFileImage());

                // Xóa ảnh cũ trên MinIO (nếu trước đó đã có ảnh)
                if (bookImage.getImageFileName() != null) {
                    minioService.deleteFile(bookImage.getImageFileName());
                }

                // Cập nhật tên file mới
                bookImage.setImageFileName(newUploadedFileName);
            }

            // 2. Cập nhật sách liên kết và lưu Database
            bookImage.setBook(book);
            return mapToResponse(bookImageRepository.save(bookImage));

        } catch (Exception e) {
            // 3. Dọn rác ảnh mới vừa up nếu tiến trình bị lỗi
            if (newUploadedFileName != null) {
                try {
                    minioService.deleteFile(newUploadedFileName);
                } catch (Exception ex) {
                    System.err.println("Khong the don rac anh tren MinIO: " + ex.getMessage());
                }
            }
            throw new RuntimeException("Loi cap nhat anh, da hoan tac giao dich: " + e.getMessage(), e);
        }
    }

    @Transactional
    public BookImageResponse deleteBookImage(Long id) {
        BookImage deletedBookImage = bookImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay anh de xoa!"));

        // 1. Xóa file vật lý trên MinIO trước
        if (deletedBookImage.getImageFileName() != null) {
            try {
                minioService.deleteFile(deletedBookImage.getImageFileName());
            } catch (Exception e) {
                System.err.println("Khong the xoa anh tren MinIO: " + e.getMessage());
            }
        }

        // 2. Xóa bản ghi trong Database
        bookImageRepository.deleteById(id);
        return mapToResponse(deletedBookImage);
    }
}