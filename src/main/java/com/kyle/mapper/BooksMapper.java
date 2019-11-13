package com.kyle.mapper;

import com.kyle.domain.Book;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;
@Mapper
public interface BooksMapper {
    List<Book> selectAll(Integer cid);
}
