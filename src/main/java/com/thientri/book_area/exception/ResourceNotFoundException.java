package com.thientri.book_area.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Đánh dấu lỗi này sẽ luôn trả về mã 404 (Not Found)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    // Constructor nhận message lỗi
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
