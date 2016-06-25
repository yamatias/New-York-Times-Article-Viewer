package com.example.matiash.newyorktimessearchproject;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by matiash on 6/20/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article>{
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context,android.R.layout.simple_list_item_1,articles);

    }

    private static class ViewHolder{
        ImageView ivImage;
        TextView tvTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Article article = this.getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_article_result, parent, false);
            viewHolder = new ViewHolder();
            ButterKnife.bind(convertView);
            viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //Reset the ImageView to that it doesn't show any recycled material. This is important!
        viewHolder.ivImage.setImageResource(0);

        // Populate the data into the template view using the data object
        viewHolder.tvTitle.setText(article.getHeadline());
        if(!TextUtils.isEmpty(article.getThumbnail())) {
            Glide.with(getContext()).load(article.getThumbnail()).fitCenter().into(viewHolder.ivImage);//.resize(viewHolder.ivImage.getMaxWidth(),viewHolder.ivImage.getMaxHeight()).into(viewHolder.ivImage);
        } else {
            Glide.with(getContext()).load(R.drawable.placeholder).fitCenter().into(viewHolder.ivImage);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
