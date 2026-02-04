package com.wingersoft.pki.controller;

import com.wingersoft.pki.dto.book.BookRequest;
import com.wingersoft.pki.dto.book.EncryptedPayload;
import com.wingersoft.pki.entities.Book;
import com.wingersoft.pki.service.book.BookService;
import com.wingersoft.pki.utils.CryptoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/book")
@Slf4j
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CryptoService cryptoService;

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody BookRequest request){

        Book book = bookService.createBook(request);

        return new ResponseEntity<>( book, HttpStatus.ACCEPTED);
    }

    @PostMapping("/bk")
    public ResponseEntity<EncryptedPayload> sendData(@RequestBody Map<String, Object> data) throws Exception {
        EncryptedPayload encrypted = cryptoService.encrypt(data);
        return ResponseEntity.ok(encrypted);
    }

    @PostMapping("bkDecrypt")
    public ResponseEntity<Book> decryptDate(@RequestBody EncryptedPayload payload) throws Exception {

        Book book = cryptoService.decryptAndVerify(
                payload, Book.class
        );

        return ResponseEntity.ok(book);
    }


}
