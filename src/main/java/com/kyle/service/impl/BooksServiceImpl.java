package com.kyle.service.impl;

import com.kyle.dao.BooksRespository;
import com.kyle.domain.Book;
import com.kyle.mapper.BooksMapper;
import com.kyle.service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
}
