package com.kevin.doubansearch.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public interface MovieService {
    Boolean parseMovie(String keyword) throws IOException, InterruptedException;

    List<Map<String, Object>> searchPageWithHighlight(String keyword, int pageNo, int pageSize) throws IOException;
}
