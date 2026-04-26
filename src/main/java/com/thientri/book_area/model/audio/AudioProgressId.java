package com.thientri.book_area.model.audio;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AudioProgressId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "audio_id")
    private Long audiobookId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioProgressId that = (AudioProgressId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(audiobookId, that.audiobookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, audiobookId);
    }
}
