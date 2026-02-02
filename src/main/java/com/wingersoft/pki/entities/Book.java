package com.wingersoft.pki.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

@Entity
@Table(name = "book")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="book_id")
    private Integer bookId;

    @Column(name="title",  length = 100, nullable = false)
    private String title;

    @Column(name="author",  length = 100, nullable = false)
    private String author;

    @Column(name="publisher",  length = 100, nullable = false)
    private String publisher;

    @Column(name="pages",   nullable = false)
    private Integer pages;






}
