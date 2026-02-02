package com.wingersoft.pki.service.book;

import com.wingersoft.pki.dto.book.BookRequest;
import com.wingersoft.pki.entities.Book;
import com.wingersoft.pki.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookServiceImp implements  BookService {


    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book createBook(BookRequest request) {

        log.info("This is before the request is logged");
        log.info(" {}", request.toString() );
        log.info("This is after the request is logged");

        Book book = new Book();

        book.setAuthor(request.author());
        book.setPages(request.pages());
        book.setTitle(request.title());
        book.setPublisher(request.publisher());


      return bookRepository.save(book);

    }
}
