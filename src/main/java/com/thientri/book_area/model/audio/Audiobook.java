package com.thientri.book_area.model.audio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thientri.book_area.model.catalog.Book;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audiobooks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Audiobook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "book_id", unique = true)
    private Book book;

    @Column(name = "total_duration")
    private Integer totalDuration;

    @OneToMany(mappedBy = "audiobook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AudioChapter> chapters = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "audiobook_narrators", joinColumns = @JoinColumn(name = "audiobook_id"), inverseJoinColumns = @JoinColumn(name = "narrator_id"))
    private Set<Narrator> narrators = new HashSet<>();
}
