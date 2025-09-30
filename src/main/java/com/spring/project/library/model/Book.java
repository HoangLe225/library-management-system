package com.spring.project.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "isbn")
    private String isbn;
    @Column(name = "title")
    private String title;
    @Column(name = "author")
    private String author;
    @Column(name = "year")
    private Integer year;
    @Column(name = "total_copies")
    private Integer totalCopies;
    @Column(name = "available_copies")
    private Integer availableCopies;
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
    private List<Loan> loans;
}
