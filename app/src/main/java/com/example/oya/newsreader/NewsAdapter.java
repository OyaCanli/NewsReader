package com.example.oya.newsreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder>{

    private final ArrayList<NewsArticle> articleList;
    private Context mContext;
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
            holder.author_tv.setText("By " + currentArticle.getAuthor());
        }
        holder.section_tv.setText(currentArticle.getSection());
        String[] dateAndTime = currentArticle.getDate().split("T");
        holder.date_tv.setText(formatDate(dateAndTime[0]) + "\n" + dateAndTime[1].substring(0,5));
        if(!TextUtils.isEmpty(currentArticle.getArticleTrail())){
            holder.trail_tv.setText(Html.fromHtml(currentArticle.getArticleTrail()));
        } else {
            holder.trail_tv.setVisibility(View.GONE);
        }
        Glide.with(mContext)
              .load(currentArticle
              .getThumbnailUrl())
              .into(holder.thumbnail_iv);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView title_tv;
        private ImageView thumbnail_iv;
        private TextView author_tv;
        private ImageButton bookmark_btn;
        private ImageButton share_btn;
        private TextView date_tv;
        private TextView section_tv;
        private TextView trail_tv;
        private View container;

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
            this.container = viewItem.findViewById(R.id.container);
            this.container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onListItemClick(v, getLayoutPosition());
        }
    }

    private String formatDate(String date) {
        Date dateObject = null;
        try {
            dateObject = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("LLL dd, yyyy").format(dateObject);
    }

    public interface ListItemClickListener {
        void onListItemClick(View view, int position);
    }
}
