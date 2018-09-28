package com.example.oya.newsreader.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.data.NewsContract.NewsEntry;
import com.example.oya.newsreader.utils.GlideApp;
import com.example.oya.newsreader.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class NewsCursorAdapter extends RecyclerView.Adapter<NewsCursorAdapter.NewsCursorHolder>{

    private final Context mContext;
    private final ListItemClickListener itemClickListener;
    private Cursor mCursor;

    public NewsCursorAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public NewsCursorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        view.setFocusable(true);
        return new NewsCursorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsCursorHolder holder, int position) {

        mCursor.moveToPosition(position);
        holder.title_tv.setText(mCursor.getString(mCursor.getColumnIndex(NewsEntry.COLUMN_TITLE)));
        String author = mCursor.getString(mCursor.getColumnIndex(NewsEntry.COLUMN_AUTHOR));
        if(!TextUtils.isEmpty(author)){
            holder.author_tv.setText(mContext.getString(R.string.byline, author));
        }
        holder.section_tv.setText(mCursor.getString(mCursor.getColumnIndex(NewsEntry.COLUMN_SECTION)));
        String[] dateAndTime = formatDateTime(mCursor.getString(mCursor.getColumnIndex(NewsEntry.COLUMN_DATE))).split("T");
        holder.date_tv.setText(dateAndTime[0] + "\n" + dateAndTime[1]);
        String trail = mCursor.getString(mCursor.getColumnIndex(NewsEntry.COLUMN_TRAIL));
        if(!TextUtils.isEmpty(trail)){
            holder.trail_tv.setText(Utils.processHtml(trail));
        } else {
            holder.trail_tv.setVisibility(View.GONE);
        }
        GlideApp.with(mContext)
                .load(mCursor.getString(mCursor.getColumnIndex(NewsEntry.COLUMN_THUMBNAIL_URL)))
                .listImage()
                .into(holder.thumbnail_iv);
        long id = mCursor.getLong(mCursor.getColumnIndex(NewsEntry._ID));
        holder.itemView.setTag(id);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public class NewsCursorHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView title_tv;
        private final ImageView thumbnail_iv;
        private final TextView author_tv;
        private final ImageButton bookmark_btn;
        private final ImageButton share_btn;
        private final TextView date_tv;
        private final TextView section_tv;
        private final TextView trail_tv;

        NewsCursorHolder(View viewItem){
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
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long id = mCursor.getLong(mCursor.getColumnIndex(NewsEntry._ID));
            itemClickListener.onListItemClick(v, id);
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
        void onListItemClick(View view, long id);
    }
}
