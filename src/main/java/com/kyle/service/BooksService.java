package com.kyle.service;

import com.kyle.domain.Book;

import java.util.List;

public interface BooksService {
     List<Book> selectAllimport();
     List<Book> selectAll(Integer cid);
}
