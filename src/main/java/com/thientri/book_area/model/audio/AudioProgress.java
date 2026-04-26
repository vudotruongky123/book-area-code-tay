package com.thientri.book_area.model.audio;

import com.thientri.book_area.model.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "audio_progress")
public class AudioProgress {
    @EmbeddedId
    private AudioProgressId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("audiobookId")
    @JoinColumn(name = "audiobook_id")
    private Audiobook audiobook;

    @Column(name = "progress")
    private Integer progress;
}
