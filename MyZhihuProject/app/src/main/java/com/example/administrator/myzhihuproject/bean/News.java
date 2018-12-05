package com.example.administrator.myzhihuproject.bean;

public class News {
    private  String Title;
    private String Imageid;
    private String Content;

    public News(String title, String imageid,String content) {
        Title = title;
        Imageid = imageid;
        Content=content;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImageid() {
        return Imageid;
    }

    public void setImageid(String imageid) {
        Imageid = imageid;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
