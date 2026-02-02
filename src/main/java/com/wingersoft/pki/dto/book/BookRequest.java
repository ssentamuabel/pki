package com.wingersoft.pki.dto.book;

public record BookRequest (
        String title,
        String author,
        String publisher,
        Integer pages
){

}
