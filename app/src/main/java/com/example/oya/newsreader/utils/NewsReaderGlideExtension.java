package com.example.oya.newsreader.utils;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.oya.newsreader.R;

@GlideExtension
public class NewsReaderGlideExtension {

    private NewsReaderGlideExtension(){
    }

    @GlideOption
    public static void listImage(RequestOptions options) {
        options
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop();
    }

    @GlideOption
    public static void detailImage(RequestOptions options) {
        options
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop();
    }
}
