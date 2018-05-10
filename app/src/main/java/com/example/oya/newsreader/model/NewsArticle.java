package com.example.oya.newsreader.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsArticle implements Parcelable {

    private final long articleId;
    private final String title;
    private final String thumbnailUrl;
    private final String author;
    private final String articleTrail;
    private final String articleBody;
    private final String date;
    private final String webUrl;
    private final String section;

    public NewsArticle(long id, String title, String thumbnailUrl, String author, String date, String webURL, String section, String trail, String body) {
        articleId = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.author = author;
        articleTrail = trail;
        this.date = date;
        this.webUrl = webURL;
        this.section = section;
        articleBody = body;
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

    public String getArticleBody() {
        return articleBody;
    }

    public long getArticleId() {
        return articleId;
    }

    private NewsArticle(Parcel in) {
        articleId = in.readLong();
        title = in.readString();
        thumbnailUrl = in.readString();
        author = in.readString();
        articleTrail = in.readString();
        articleBody = in.readString();
        date = in.readString();
        webUrl = in.readString();
        section = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(articleId);
        dest.writeString(title);
        dest.writeString(thumbnailUrl);
        dest.writeString(author);
        dest.writeString(articleTrail);
        dest.writeString(articleBody);
        dest.writeString(date);
        dest.writeString(webUrl);
        dest.writeString(section);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NewsArticle> CREATOR = new Parcelable.Creator<NewsArticle>() {
        @Override
        public NewsArticle createFromParcel(Parcel in) {
            return new NewsArticle(in);
        }

        @Override
        public NewsArticle[] newArray(int size) {
            return new NewsArticle[size];
        }
    };

    @Override
    public String toString() {
        return "NewsArticle{" +
                "title='" + title + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", author='" + author + '\'' +
                ", articleTrail='" + articleTrail + '\'' +
                ", articleBody='" + articleBody + '\'' +
                ", date='" + date + '\'' +
                ", webUrl='" + webUrl + '\'' +
                ", section='" + section + '\'' +
                '}';
    }
}
