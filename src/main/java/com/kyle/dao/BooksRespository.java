package com.kyle.dao;

import com.kyle.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BooksRespository extends JpaRepository<Book,Integer> {
}
