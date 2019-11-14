package com.kyle.service;

import com.kyle.domain.Book;

import java.util.List;

public interface BooksService {
     List<Book> selectAllimport();
     List<Book> selectAll(Integer cid);
     List<Book> selectBid(String time);
     List<Book> selectAllb(Integer bid);
     void save(Book book);
     Book selectOneBook(Integer bid);
     void deleteOneBook(Integer bid);
}
