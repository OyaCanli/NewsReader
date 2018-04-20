package com.example.oya.newsreader;

public class NewsArticle {

    private String title;
    private String thumbnailUrl;
    private String author;
    private String articleTrail;
    //private String articleBody;
    private String date;
    private String webUrl;
    private String section;

    public NewsArticle(String title, String thumbnailUrl, String author, String date, String webURL, String section, String trail) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.author = author;
        articleTrail = trail;
        this.date = date;
        this.webUrl = webURL;
        this.section = section;
        //this.articleBody = articleBody;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getWebUrl() {
        return webUrl;
    }


    public String getSection() {
        return section;
    }

    public String getArticleTrail() {
        return articleTrail;
    }

    @Override
    public String toString() {
        return "NewsArticle{" +
                "title='" + title + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                ", webUrl='" + webUrl + '\'' +
                '}';
    }
}
