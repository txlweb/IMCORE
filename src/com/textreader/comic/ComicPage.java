package com.textreader.comic;

import java.util.ArrayList;
import java.util.List;

public class ComicPage {
    private List<Integer> res_list = new ArrayList<>();
    private String title = "*ERROR GET NAME*";
    public int push(int res_id){
        res_list.add(res_id);
        return res_list.size();
    }
    public int get(int id){
        return res_list.get(id);
    }
    public int size(){
        return res_list.size();
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
