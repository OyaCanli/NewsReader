package com.example.oya.newsreader.utils;

import android.support.annotation.NonNull;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.oya.newsreader.R;

@GlideExtension
public class NewsReaderGlideExtension {

    private NewsReaderGlideExtension(){
    }

    @NonNull
    @GlideOption
    public static RequestOptions listImage(RequestOptions options) {
        options
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop();
        return options;
    }

    @NonNull
    @GlideOption
    public static RequestOptions detailImage(RequestOptions options) {
        options
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop();
        return options;
    }
}
