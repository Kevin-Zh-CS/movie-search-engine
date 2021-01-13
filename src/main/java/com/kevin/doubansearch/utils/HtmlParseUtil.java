package com.kevin.doubansearch.utils;

import com.kevin.doubansearch.pojo.Movie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlParseUtil {
    public static final String INDEX_NAME = "kevin_movie_list";

//    public static void main(String[] args) throws IOException, InterruptedException {
//       parseDouban("碟中谍");
//    }

    public List<Movie> parseDouban(String keyword) throws IOException, InterruptedException {
        String url = "https://www.1905.com/search/?q=" + keyword + "&enc=utf-8";
        //解析网页
        Document document = Jsoup.connect(url).timeout(30000).get();
        Elements elements = document.getElementsByClass("new_content");
        List<Movie> movieList = new ArrayList<>();
        for (Element element : elements) {
            String title = element
                    .getElementsByClass("title-mv")
                    .eq(0)
                    .text();

            String img = element
                    .getElementsByTag("img")
                    .eq(0)
                    .attr("src");
            if (img.isBlank() || !img.substring(0, 5).contains("http")) {
                img = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2619626237,3624368718&fm=15&gp=0.jpg";
            }

            String score = element
                    .getElementsByClass("movie-pic")
                    .eq(0)
                    .text();
            if (score.isBlank()) {
                score = "暂无评分";
            }

            String detail = element
                    .getElementsByTag("p")
                    .eq(0)
                    .text()
                    .replaceAll("简介：", "")
                    .replaceAll("[详细]", "")
                    .trim();
            if (detail.isBlank()) {
                detail = "暂无简介";
            } else if (detail.length() > 60) {
                detail = detail.substring(0, 60);
            }
            detail += "...";
            //detail = detail.substring(detail.lastIndexOf("简介"), detail.indexOf("["));
            //System.out.println(img);
            //System.out.println(detail);
            movieList.add((new Movie(title, img, score, detail)));
        }
        //System.out.println(movieList);
        return movieList;
    }
}
