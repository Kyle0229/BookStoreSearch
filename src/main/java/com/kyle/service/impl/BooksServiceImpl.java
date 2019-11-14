package com.kyle.service.impl;

import com.kyle.dao.BooksRespository;
import com.kyle.domain.Book;
import com.kyle.mapper.BooksMapper;
import com.kyle.service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class BooksServiceImpl implements BooksService {

    @Resource
    private BooksMapper booksMapper;
    @Autowired
    private BooksRespository booksRespository;
    @Override
    public List<Book> selectAllimport() {
        return booksRespository.findAll();
    }

    @Override
    public List<Book> selectAll(Integer cid) {
        return booksMapper.selectAll(cid);
    }

    @Override
    public void save(Book book) {
        booksRespository.save(book);
    }

    @Override
    public void deleteOneBook(Integer bid) {
        booksRespository.deleteById(bid);
    }

    @Override
    public Book selectOneBook(Integer bid) {
        Optional<Book> byId = booksRespository.findById(bid);
        Book book = byId.get();
        return book;
    }

    @Override
    public List<Book> selectBid(String time) {
        return booksMapper.selectCud(time);
    }

    @Override
    public List<Book> selectAllb(Integer bid) {
        return booksMapper.selectAllb(bid);
    }


}
