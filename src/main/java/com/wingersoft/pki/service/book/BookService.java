package com.wingersoft.pki.service.book;

import com.wingersoft.pki.dto.book.BookRequest;
import com.wingersoft.pki.entities.Book;

public interface BookService {

    Book createBook(BookRequest request);
}
