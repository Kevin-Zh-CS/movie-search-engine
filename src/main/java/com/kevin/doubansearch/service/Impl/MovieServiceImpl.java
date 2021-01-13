package com.kevin.doubansearch.service.Impl;

import com.alibaba.fastjson.JSON;
import com.kevin.doubansearch.pojo.Movie;
import com.kevin.doubansearch.service.MovieService;
import com.kevin.doubansearch.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //解析数据放入es索引中
    @Override
    public Boolean parseMovie(String keyword) throws IOException, InterruptedException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(HtmlParseUtil.INDEX_NAME);
        //判断索引是否存在
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if(!exists){
            //创建索引
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(HtmlParseUtil.INDEX_NAME);
            //执行创建请求
            restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }
        List<Movie> movieList = new HtmlParseUtil().parseDouban(keyword);
        //创建批量插入请求
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueMinutes(1));
        for (Movie movie : movieList) {
            bulkRequest.add(
                    new IndexRequest(HtmlParseUtil.INDEX_NAME)
                        .source(JSON.toJSONString(movie), XContentType.JSON)
            );
        }
        //提交批量创建
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        //执行正常则返回true
        //System.out.println(movieList);
        return !bulkResponse.hasFailures();
    }

    @Override
    public List<Map<String, Object>> searchPageWithHighlight(String keyword, int pageNo, int pageSize) throws IOException {
        List<Map<String, Object>> hitList = new ArrayList<>();
        //parseGood(keyword);
        if (pageNo <= 1) {
            pageNo = 1;
        }
        //条件搜索
        SearchRequest searchRequest = new SearchRequest(HtmlParseUtil.INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        //分词匹配
        //MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        //FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("title", keyword);
        //TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(keyword);
        searchSourceBuilder.query(queryStringQueryBuilder);
        searchSourceBuilder.timeout(TimeValue.timeValueMinutes(1));

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        for (SearchHit hit : searchResponse.getHits()) {
            //解析高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("title");
            //System.out.println(highlightFields);
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (highlightField != null) {
                Text[] fragments = highlightField.fragments();
                StringBuilder n_title = new StringBuilder();
                for (Text text : fragments) {
                    n_title.append(text);
                }
                // 高亮字段替换掉原来的内容即可
                sourceAsMap.put("title", n_title.toString());
            }
            hitList.add(sourceAsMap);
        }

        return hitList;
    }
}






