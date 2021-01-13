package com.kevin.doubansearch.Index;

import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InvertedIndex {

    public static void main(String[] args) {
        Map<String, Integer> treeMap = new TreeMap<>();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input your keyword: ");
        String keyword = scanner.nextLine();
        File file;
        for (int i = 1; i <= 6; i++) {
            int cnt = 0;
            try {
                file = ResourceUtils.getFile("classpath:data/file" + i + ".txt");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    if (s.toLowerCase().contains(keyword)) {
                        cnt++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (cnt != 0) {
                treeMap.put("file" + i + ".txt", cnt);
            }
        }
        //按照相关程度排序（出现次数）
        Comparator<Map.Entry<String, Integer>> valCmp = (o1, o2) -> o2.getValue() - o1.getValue();
        //将Map转化成List<Entry<String, Integer>>，调用排序API
        List<Map.Entry<String, Integer>> list = new ArrayList<>(treeMap.entrySet());
        //根据value大小进行排序
        list.sort(valCmp);

        System.out.println("-------------------");
        System.out.println("|filename\t|count|");
        System.out.println("-------------------");
        for (Map.Entry<String, Integer> entry : list) {
            System.out.println("|"+entry.getKey()+"\t|"+entry.getValue()+"\t  |");
        }
        System.out.println("-------------------");
    }
}
