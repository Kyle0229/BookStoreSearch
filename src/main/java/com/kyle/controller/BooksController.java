package com.kyle.controller;
import com.alibaba.fastjson.JSON;
import com.kyle.domain.Book;
import com.kyle.service.BooksService;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class BooksController {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
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
                "           },\n" +
                "           \"time\": {\n" +
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
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(d);
        System.out.println("当前时间：" + format);
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
            Object bprice = sourceAsMap.get("bprice");
            String s = String.valueOf(bprice);
            BigDecimal bigDecimal=new BigDecimal(s);
            String pic=(String)sourceAsMap.get("bpic");
            String info=(String)sourceAsMap.get("introduce");
            Integer cid = (Integer) sourceAsMap.get("cid");
            Integer aid1 = (Integer) sourceAsMap.get("aid");
            Integer scount = (Integer) sourceAsMap.get("scount");
            Object nummoney = sourceAsMap.get("nummoney");
            String nummoney1 = String.valueOf(nummoney);
            BigDecimal nummoney2=new BigDecimal(nummoney1);
            Book books=new Book();
            books.setBid(id);
            books.setBname(name);
            books.setIntroduce(info);
            books.setBpic(pic);
            books.setBprice(bigDecimal);
            books.setCid(cid);
            books.setAid(aid1);
            books.setScount(scount);
            books.setNummoney(nummoney2);
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
            Object bprice = sourceAsMap.get("bprice");
            String s = String.valueOf(bprice);
            BigDecimal bigDecimal=new BigDecimal(s);
            String pic=(String)sourceAsMap.get("bpic");
            String info=(String)sourceAsMap.get("introduce");
            Integer cid1 = (Integer) sourceAsMap.get("cid");
            Integer aid1 = (Integer) sourceAsMap.get("aid");
            Integer scount = (Integer) sourceAsMap.get("scount");
            Object nummoney = sourceAsMap.get("nummoney");
            String nummoney1 = String.valueOf(nummoney);
            BigDecimal nummoney2=new BigDecimal(nummoney1);
            Book books=new Book();
            books.setBid(id);
            books.setBname(name);
            books.setIntroduce(info);
            books.setBpic(pic);
            books.setBprice(bigDecimal);
            books.setCid(cid1);
            books.setAid(aid1);
            books.setScount(scount);
            books.setNummoney(nummoney2);
            list.add(books);
        }
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(d);
        System.out.println("当前时间：" + format);
        return list;
    }
    @RequestMapping("/saveBook")
    public String savees(@RequestBody Book book) throws IOException {
        booksService.save(book);
        //获取索引库
        IndexRequest indexRequest=new IndexRequest("books","doc");
        //单独存id
        Integer bid = book.getBid();
        indexRequest.id(String.valueOf(bid));
        String s = JSON.toJSONString(book);
        indexRequest.source(s,XContentType.JSON);
        IndexResponse index = restHighLevelClient.index(indexRequest);
        DocWriteResponse.Result result = index.getResult();
        System.out.println("添加数据后的结果================="+result);
        return "sucess";
    }
    @RequestMapping("/selectOneBook/{bid}")
    public Book selectOneBook(@PathVariable Integer bid){
        Book book = booksService.selectOneBook(bid);
        return book;
    }
    @RequestMapping("/deleteOneBook")
    public String deleteOneBook(@RequestBody Book book) throws IOException {
        Integer bid = book.getBid();
        booksService.deleteOneBook(book.getBid());
        DeleteRequest deleteRequest = new DeleteRequest("books", "doc",String.valueOf(bid));
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest);
        DocWriteResponse.Result result = delete.getResult();
        System.out.println("删除成功=========="+result);
        return "delete success";
    }
    /*@RequestMapping("/Precudflushes")
    public String flushes() throws IOException, ParseException {
        //rides中取上次时间time
            //添加操作数据
//        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String format1 = sdf.format(date);
//        redisTemplate.opsForValue().set("time",format1);
        Object time = redisTemplate.opsForValue().get("time");
        //String s = String.valueOf(time);
        System.out.println(time);
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Date d=format.parse(map.get("FORMENDTIME").toString());

        //Date parse = format.parse(s);
        List<Book> books = booksService.selectBid(time.toString());
        System.out.println(books.toString());
        for (Book cudtime:books) {
            Integer bid = cudtime.getBid();
            DeleteRequest deleteRequest=new DeleteRequest("books","doc",String.valueOf(bid));
            DeleteResponse delete = restHighLevelClient.delete(deleteRequest);
            DocWriteResponse.Result result = delete.getResult();
            System.out.println("删除后的结果为=================="+result.toString());
        }
        return "=======================cud成功";
    }
        //对数据库操作放中间
        //根据时间取被操作书的bid
        //根据bid查出Book对象重新导入
       *//* Object time = redisTemplate.opsForValue().get("time");
        String s = String.valueOf(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Date d=format.parse(map.get("FORMENDTIME").toString());
        Date parse = format.parse(s);*//*

       @RequestMapping("/lastflushes")
       public String lastflushes() throws IOException {
           Object time = redisTemplate.opsForValue().get("time");
           //String s = String.valueOf(time);
           System.out.println(time);
        List<Book> cudtimes = booksService.selectBid(time.toString());
        for (Book cudtime:cudtimes) {
            Integer bid1 = cudtime.getBid();
            List<Book> books = booksService.selectAllb(bid1);
            Map map = new HashMap();
            for (Book book : books) {
                String jsonString = JSON.toJSONString(book);
                //索引请求对象
                IndexRequest indexRequest = new IndexRequest("books", "doc");
                Integer bid = book.getBid();
                indexRequest.id(String.valueOf(bid));
                //指定索引文档内容
               indexRequest.source(jsonString, XContentType.JSON);
                //索引响应对象
              *//*  map.put("bname",book.getBname());
                map.put("time",book.getTime());
                map.put("bpic",book.getBpic());
                map.put("introduce",book.getIntroduce());
                map.put("bprice",book.getBprice());
                IndexRequest source = indexRequest.source(map);*//*
                IndexResponse indexResponse = restHighLevelClient.index(indexRequest);
                //获取响应结果
                DocWriteResponse.Result result = indexResponse.getResult();
                System.out.println("添加数据后的结果为===============" + result);

                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format1 = sdf.format(d);
                System.out.println("当前时间：" + format1);
                redisTemplate.opsForValue().set("time",format1);
                Cudtime cudtime1 = new Cudtime();
                cudtime1.setBid(bid);
                cudtime1.setCudtime(format1);
                //更改cuttime表时间
                booksService.updatecudb(cudtime1);
                booksService.updatecud(cudtime1);

            }
        }
        return "cud成功,并导入成功";
    }*/
}
