package com.example.matiash.newyorktimessearchproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.matiash.newyorktimessearchproject.Article;
import com.example.matiash.newyorktimessearchproject.NewShareActionProvider;
import com.example.matiash.newyorktimessearchproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {

    NewShareActionProvider shareActionProvider;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.wvArticle) WebView wvArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        wvArticle.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        Article article = (Article)getIntent().getParcelableExtra("article");
        wvArticle.loadUrl(article.getLink());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail,menu);

        shareActionProvider = ((NewShareActionProvider)(MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share))));

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,wvArticle.getUrl());

        if(shareActionProvider != null) {
            shareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
                @Override
                public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                    startActivity(intent);
                    return true;
                }
            });

            shareActionProvider.setShareIntent(shareIntent);
        }

//        Toast.makeText(ArticleActivity.this,"Share action provider is loaded!",Toast.LENGTH_SHORT).show();
        return super.onCreateOptionsMenu(menu);
    }
}
