package com.kyle.controller;


import com.alibaba.fastjson.JSON;
import com.kyle.domain.Book;
import com.kyle.service.BooksService;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BooksController {
    @Autowired
    private BooksService booksService;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @RequestMapping("/esCreateIndexSelectAll")
    public String testCreateIndex() throws IOException {
        //设置索引库名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("books");
        //设置索引库分页数，副本数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", 1).put("number_of_replicas", 0));
        //设置索引库映射
        createIndexRequest.mapping("doc", " {\n" +
                "    \"properties\": {\n" +
                "           \"bname\": {\n" +
                "              \"type\": \"text\",\n" +
                "              \"analyzer\":\"ik_max_word\",\n" +
                "              \"search_analyzer\":\"ik_smart\"\n" +
                "           },\n" +
                "           \"introduce\": {\n" +
                "              \"type\": \"text\",\n" +
                "              \"analyzer\":\"ik_max_word\",\n" +
                "              \"search_analyzer\":\"ik_smart\"\n" +
                "           },\n" +
                "           \"bprice\": {\n" +
                "              \"type\": \"float\"\n" +
                "           },\n" +
                "           \"bpic\": {\n" +
                "              \"type\": \"text\"\n" +
                "           }\n" +
                "        }\n" +
                "}", XContentType.JSON);
        //创建索引客户端
        IndicesClient indices = restHighLevelClient.indices();
        //执行操作
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        //获取返回值
        boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
        if (shardsAcknowledged) {
            System.out.println("索引库，映射创建成功");
        }
        List<Book> books = booksService.selectAllimport();
        Map map=new HashMap();
        for (Book book : books) {
            String jsonString = JSON.toJSONString(book);
            //索引请求对象
            IndexRequest indexRequest = new IndexRequest("books","doc");
            Integer bid = book.getBid();
            indexRequest.id(String.valueOf(bid));
            //指定索引文档内容
            indexRequest.source(jsonString,XContentType.JSON);
            //索引响应对象
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest);
            //获取响应结果
            DocWriteResponse.Result result = indexResponse.getResult();
            System.out.println("添加数据后的结果为==============="+result);
        }
        return "索引库，映射创建成功,并导入成功";
    }
    @RequestMapping("/selectes/{bname}")
    public List<Book> selectes(@PathVariable String bname) throws IOException {
        //请求对象
        SearchRequest searchRequest=new SearchRequest("books");
        //搜索类型
        searchRequest.types("doc");
        //构建搜索源对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //向搜索元对象中设置搜索类型
        // TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name",gname);
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(bname, "bname", "introduce");
        // SearchSourceBuilder query = searchSourceBuilder.query(termQueryBuilder);
        SearchSourceBuilder query = searchSourceBuilder.query(multiMatchQueryBuilder);
        //设置数据源
        searchRequest.source(query);
        //执行数据源
        SearchResponse search = restHighLevelClient.search(searchRequest);
        System.out.println(search.toString());
        SearchHits hits = search.getHits();
        SearchHit[] hits1 = hits.getHits();
        List<Book> list=new ArrayList<>();
        for (SearchHit h:hits1) {
            Integer id = Integer.valueOf(h.getId());
            Map<String, Object> sourceAsMap = h.getSourceAsMap();
            //Integer gid=(Integer)sourceAsMap.get("_id");
            String name=(String)sourceAsMap.get("bname");
            Integer bprice = Integer.parseInt(sourceAsMap.get("bprice").toString());
            BigDecimal bigDecimal=new BigDecimal(bprice.toString());
            String pic=(String)sourceAsMap.get("bpic");
            String info=(String)sourceAsMap.get("introduce");
            Book books=new Book();
            books.setBid(id);
            books.setBname(name);
            books.setIntroduce(info);
            books.setBpic(pic);
            books.setBprice(bigDecimal);
            list.add(books);
        }
        return list;
    }
    @RequestMapping("/selectcb/{cid}")
    public List<Book> selectcb(@PathVariable Integer cid) throws IOException {
        //请求对象
        SearchRequest searchRequest=new SearchRequest("books");
        //搜索类型
        searchRequest.types("doc");
        //构建搜索源对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //向搜索元对象中设置搜索类型
        // TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name",gname);
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(cid, "cid");
        // SearchSourceBuilder query = searchSourceBuilder.query(termQueryBuilder);
        SearchSourceBuilder query = searchSourceBuilder.query(multiMatchQueryBuilder);
        //设置数据源
        searchRequest.source(query);
        //执行数据源
        SearchResponse search = restHighLevelClient.search(searchRequest);
        System.out.println(search.toString());
        SearchHits hits = search.getHits();
        SearchHit[] hits1 = hits.getHits();
        List<Book> list=new ArrayList<>();
        for (SearchHit h:hits1) {
            Integer id = Integer.valueOf(h.getId());
            Map<String, Object> sourceAsMap = h.getSourceAsMap();
            //Integer gid=(Integer)sourceAsMap.get("_id");
            String name=(String)sourceAsMap.get("bname");
            Integer bprice = Integer.parseInt(sourceAsMap.get("bprice").toString());
            BigDecimal bigDecimal=new BigDecimal(bprice.toString());
            String pic=(String)sourceAsMap.get("bpic");
            String info=(String)sourceAsMap.get("introduce");
            Book books=new Book();
            books.setBid(id);
            books.setBname(name);
            books.setIntroduce(info);
            books.setBpic(pic);
            books.setBprice(bigDecimal);
            list.add(books);
        }
        return list;
    }
}
