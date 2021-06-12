package com.example.oya.newsreader.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder>{

    private final ArrayList<NewsArticle> articleList;
    private final Context mContext;
    private final ListItemClickListener itemClickListener;

    public NewsAdapter(Context context, ArrayList<NewsArticle> articleList, ListItemClickListener listener) {
        mContext = context;
        this.articleList = articleList;
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
        NewsArticle currentArticle = articleList.get(position);
        holder.title_tv.setText(currentArticle.getTitle());
        if(!TextUtils.isEmpty(currentArticle.getAuthor())){
            holder.author_tv.setText(mContext.getString(R.string.byline, currentArticle.getAuthor()));
        }
        String temp = new String(currentArticle.getSection());
        holder.section_tv.setText(temp);
        String[] dateAndTime = formatDateTime(currentArticle.getDate()).split("T");
        holder.date_tv.setText(dateAndTime[0] + "\n" + dateAndTime[1]);
        if(!TextUtils.isEmpty(currentArticle.getArticleTrail())){
            holder.trail_tv.setText(Utils.processHtml(currentArticle.getArticleTrail()));
        } else {
            holder.trail_tv.setVisibility(View.GONE);
        }
        GlideApp.with(mContext)
              .load(currentArticle.getThumbnailUrl())
                .listImage()
                .centerCrop()
              .into(holder.thumbnail_iv);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView title_tv;
        private final ImageView thumbnail_iv;
        private final TextView author_tv;
        private final ImageButton bookmark_btn;
        private final ImageButton share_btn;
        private final TextView date_tv;
        private final TextView section_tv;
        private final TextView trail_tv;

        NewsHolder(View viewItem){
            super(viewItem);
            this.title_tv = viewItem.findViewById(R.id.title);
            this.thumbnail_iv = viewItem.findViewById(R.id.thumbnail);
            this.author_tv = viewItem.findViewById(R.id.author);
            this.date_tv = viewItem.findViewById(R.id.date);
            this.section_tv = viewItem.findViewById(R.id.section);
            this.bookmark_btn = viewItem.findViewById(R.id.bookmark);
            this.bookmark_btn.setOnClickListener(this);
            this.share_btn = viewItem.findViewById(R.id.share);
            this.share_btn.setOnClickListener(this);
            this.trail_tv = viewItem.findViewById(R.id.trail);
            viewItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onListItemClick(v, getLayoutPosition());
        }
    }

    private String formatDateTime(String dateTime){
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sourceFormat.setTimeZone(timeZone);
        Date parsedTime = null;
        try {
            parsedTime = sourceFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat destFormat = new SimpleDateFormat("LLL dd, yyyy'T'HH:mm");
        destFormat.setTimeZone(tz);
        return destFormat.format(parsedTime);
    }

    public interface ListItemClickListener {
        void onListItemClick(View view, int position);
    }
}
