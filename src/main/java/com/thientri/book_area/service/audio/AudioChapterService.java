package com.thientri.book_area.service.audio;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.thientri.book_area.dto.request.audio.AudioChapterRequest;
import com.thientri.book_area.dto.response.audio.AudioChapterResponse;
import com.thientri.book_area.model.audio.AudioChapter;
import com.thientri.book_area.model.audio.Audiobook;
import com.thientri.book_area.repository.audio.AudioChapterRepository;
import com.thientri.book_area.repository.audio.AudiobookRepository;
import com.thientri.book_area.service.minio.MinioService;

@Service
public class AudioChapterService {

    private final AudioChapterRepository audioChapterRepository;
    private final AudiobookRepository audiobookRepository;
    private final MinioService minioService;

    public AudioChapterService(AudioChapterRepository audioChapterRepository, AudiobookRepository audiobookRepository,
            MinioService minioService) {
        this.audioChapterRepository = audioChapterRepository;
        this.audiobookRepository = audiobookRepository;
        this.minioService = minioService;
    }

    public List<AudioChapterResponse> getAllAudioChapters() {
        List<AudioChapterResponse> list = new ArrayList<>();
        for (AudioChapter audioChapter : audioChapterRepository.findAll()) {
            list.add(mapToResponse(audioChapter));
        }
        return list;
    }

    private AudioChapterResponse mapToResponse(AudioChapter audioChapter) {
        String url = null;
        if (audioChapter.getAudioFileName() != null) {
            try {
                url = minioService.getUrl(audioChapter.getAudioFileName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return AudioChapterResponse.builder()
                .id(audioChapter.getId())
                .audiobookId(audioChapter.getAudiobook().getId())
                .title(audioChapter.getTitle())
                .audioUrl(url)
                .duration(audioChapter.getDuration())
                .build();
    }

    @Transactional // Không dùng throws Exception ở đây
    public AudioChapterResponse createAudioChapter(AudioChapterRequest request) {
        Audiobook audiobook = audiobookRepository.findById(request.getAudiobookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach noi de them chuong moi!"));

        AudioChapter newAudioChapter = new AudioChapter();
        newAudioChapter.setAudiobook(audiobook);
        newAudioChapter.setTitle(request.getTitle());

        String uploadAudioFileName = null; // Khai báo biến tạm bên ngoài khối try
        int chapterDuration = 0;

        try {
            // 1. Xử lý File nếu có
            if (request.getFileAudio() != null && !request.getFileAudio().isEmpty()) {
                chapterDuration = getAudioDuration(request.getFileAudio());
                uploadAudioFileName = minioService.uploadFile(request.getFileAudio());
                newAudioChapter.setAudioFileName(uploadAudioFileName);
            }

            // 2. Gán thời lượng
            newAudioChapter.setDuration(chapterDuration);
            audiobook.setTotalDuration(audiobook.getTotalDuration() + chapterDuration);

            // 3. Lưu Database (Bây giờ nó đã được bảo vệ bởi khối try)
            AudioChapter savedChapter = audioChapterRepository.save(newAudioChapter);
            audiobookRepository.save(audiobook);

            return mapToResponse(savedChapter);

        } catch (Exception e) {
            // Xử lý dọn rác nếu file đã lên MinIO nhưng quá trình sau đó (như lưu DB) bị lỗi
            if (uploadAudioFileName != null) {
                try {
                    minioService.deleteFile(uploadAudioFileName);
                } catch (Exception ex) {
                    System.err.println("Khong the don rac file tren MinIO: " + ex.getMessage());
                }
            }
            throw new RuntimeException("Loi xu ly, da hoan tac giao dich: " + e.getMessage(), e);
        }
    }

    @Transactional // Không dùng throws Exception ở đây
    public AudioChapterResponse updateAudioChapter(Long id, AudioChapterRequest request) {
        AudioChapter audioChapter = audioChapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay CHAPTER!"));

        Audiobook oldAudiobook = audiobookRepository.findById(audioChapter.getAudiobook().getId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay AudioBook cu!"));

        Audiobook newAudiobook = audiobookRepository.findById(request.getAudiobookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay AudioBook moi!"));

        String newUploadedFileName = null; // Biến lưu tên file MinIO mới để dọn rác nếu lỗi

        try {
            // Trừ thời lượng cũ
            oldAudiobook.setTotalDuration(oldAudiobook.getTotalDuration() - audioChapter.getDuration());
            int newChapterDuration = audioChapter.getDuration();

            // Xử lý File mới nếu có
            if (request.getFileAudio() != null && !request.getFileAudio().isEmpty()) {
                newChapterDuration = getAudioDuration(request.getFileAudio());
                newUploadedFileName = minioService.uploadFile(request.getFileAudio()); // Upload file mới

                // Xóa file cũ
                if (audioChapter.getAudioFileName() != null) {
                    minioService.deleteFile(audioChapter.getAudioFileName());
                }

                audioChapter.setAudioFileName(newUploadedFileName);
            }

            // Cập nhật thông tin văn bản
            audioChapter.setAudiobook(newAudiobook);
            audioChapter.setTitle(request.getTitle());
            audioChapter.setDuration(newChapterDuration);

            // Cộng thời lượng mới
            newAudiobook.setTotalDuration(newAudiobook.getTotalDuration() + newChapterDuration);

            // Lưu Database
            if (!oldAudiobook.getId().equals(newAudiobook.getId())) {
                audiobookRepository.save(oldAudiobook);
            }
            audiobookRepository.save(newAudiobook);

            return mapToResponse(audioChapterRepository.save(audioChapter));

        } catch (Exception e) {
            // Dọn rác file MỚI vừa upload nếu như việc lưu DB thất bại
            if (newUploadedFileName != null) {
                try {
                    minioService.deleteFile(newUploadedFileName);
                } catch (Exception ex) {
                    System.err.println("Khong the don rac file tren MinIO: " + ex.getMessage());
                }
            }
            throw new RuntimeException("Loi cap nhat, da hoan tac giao dich: " + e.getMessage(), e);
        }
    }

    @Transactional
    public AudioChapterResponse deleteAudioChapter(Long id) {
        // Tìm Chapter để xóa
        AudioChapter deletedAudioChapter = audioChapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay chuong cua sach noi de thuc hien xoa!"));

        // Tìm Audiobook để cập nhật thời lượng
        Audiobook audiobook = audiobookRepository.findById(deletedAudioChapter.getAudiobook().getId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach noi cua chuong bi xoa!"));

        if (deletedAudioChapter.getAudioFileName() != null) {
            try {
                minioService.deleteFile(deletedAudioChapter.getAudioFileName());
            } catch (Exception e) {
                System.err.println("Khong the xoa FILE tren MinIO: " + e.getMessage());
            }
        }

        // Sửa thời lượng
        audiobook.setTotalDuration(audiobook.getTotalDuration() - deletedAudioChapter.getDuration());
        audiobookRepository.save(audiobook);
        audioChapterRepository.delete(deletedAudioChapter);

        return mapToResponse(deletedAudioChapter);
    }

    // Lấy ra thời lượng của file
    private Integer getAudioDuration(MultipartFile file) throws Exception {
        Path tempDir = Files.createTempDirectory("audio_temp");
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "temp_audio.mp3";
        File tempFile = tempDir.resolve(originalName).toFile();

        Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        try {
            AudioFile audioFile = AudioFileIO.read(tempFile);
            AudioHeader header = audioFile.getAudioHeader();
            return header.getTrackLength();
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}