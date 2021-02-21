package com.example.newsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NewsListAdapter extends RecyclerView.Adapter<NewsViewholder> {
    ArrayList<News> items = new ArrayList<>();
    private NewsItemClicked listener;
    NewsListAdapter(NewsItemClicked listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public NewsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item,parent,false);
        NewsViewholder viewHolder = new NewsViewholder(view);
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(items.get(viewHolder.getAdapterPosition()));
                    }
                }
        );
        NewsViewholder newHolder = new NewsViewholder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewholder holder, int position) {
        News currItem = items.get(position);
        holder.title.setText(currItem.title);
        holder.author.setText(currItem.author);
        Glide.with(holder.itemView.getContext()).load(currItem.imageUrl).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void updateNews(ArrayList<News> updatedNews){
        items.clear();
        items.addAll(updatedNews);
        notifyDataSetChanged();
    }
}

class  NewsViewholder extends RecyclerView.ViewHolder{
    TextView title = itemView.findViewById(R.id.viewTitle);
    ImageView image = itemView.findViewById(R.id.image);
    TextView author = itemView.findViewById(R.id.author);
    public NewsViewholder(@NonNull View itemView) {
        super(itemView);
    }
}

interface NewsItemClicked{
    void onItemClicked(News item);
}