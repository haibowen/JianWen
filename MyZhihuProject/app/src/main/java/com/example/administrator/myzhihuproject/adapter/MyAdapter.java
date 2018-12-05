package com.example.administrator.myzhihuproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.administrator.myzhihuproject.activity.Main2Activity;
import com.example.administrator.myzhihuproject.bean.News;
import com.example.administrator.myzhihuproject.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context context;
    private List<News> mlists;

    public  MyAdapter(List<News> lists){

        mlists=lists;
    }

    static class  ViewHolder extends  RecyclerView.ViewHolder{
        CardView cardView;
        ImageView imageView;
        TextView textView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             cardView= (CardView) itemView;
             imageView=itemView.findViewById(R.id.item_image);
             textView=itemView.findViewById(R.id.item_textview);


        }
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       if (context==null){
           context=viewGroup.getContext();

       }
       View view= LayoutInflater.from(context).inflate(R.layout.recyclerview_item,viewGroup,false);
       final  ViewHolder holder=new ViewHolder(view);
       holder.cardView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               int postion=holder.getAdapterPosition();
               News news=mlists.get(postion);
               Intent intent=new Intent(context, Main2Activity.class);
               intent.putExtra(Main2Activity.TITLE,news.getTitle());
               intent.putExtra(Main2Activity.IMAGE,news.getImageid());
               intent.putExtra(Main2Activity.CONTENT,news.getContent());
               context.startActivity(intent);

           }
       });

       return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder viewHolder, int i) {

        News news=mlists.get(i);
        viewHolder.textView.setText(news.getTitle());
        Log.e("wenhaibo", "onBindViewHolder: "+news.getTitle() );
        Glide.with(context).load(news.getImageid()).into(viewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return mlists.size();
    }
}
