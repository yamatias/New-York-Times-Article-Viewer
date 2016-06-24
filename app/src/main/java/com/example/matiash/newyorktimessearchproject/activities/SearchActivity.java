package com.example.matiash.newyorktimessearchproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.matiash.newyorktimessearchproject.Article;
import com.example.matiash.newyorktimessearchproject.ArticleArrayAdapter;
import com.example.matiash.newyorktimessearchproject.EndlessScrollListener;
import com.example.matiash.newyorktimessearchproject.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FiltersFragment.OnFinishFiltersClickListener {


    GridView gvResults;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    SearchView searchView;
    FragmentManager fm;
    RequestParams params = new RequestParams();
    MenuItem imFilter;
    String inquiry;
    final String ARTICLE_SEARCH = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    final String TOP_STORIES = "https://api.nytimes.com/svc/topstories/v2/home.json";
    String url = ARTICLE_SEARCH;

    boolean titleOn = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Top Stories");

        gvResults = (GridView)findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this,articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this,ArticleActivity.class);
                intent.putExtra("article",articles.get(i));
                startActivity(intent);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if(searchView != null) {
//                    loadDataFromPageNumber(url,url.equals(ARTICLE_SEARCH)?searchView.getQuery().toString():null,page);
                    loadDataFromPageNumber(ARTICLE_SEARCH,searchView.getQuery().toString(),page); // You don't need to implement endless
                    //scroll for Top Stories here because the API only loads a specific number of results to show. Implementing it for
                    //Top Stories only repeats the list.
                }
                return true;
            }
        });

        loadDataFromPageNumber(TOP_STORIES,null,0);

    }

    public void loadDataFromPageNumber(String link, final String query, int offset) {
        //Remember that the api-key,page,and q params are already set here for params object.
        AsyncHttpClient client = new AsyncHttpClient();
        url = link;
        params.put("api-key","baff8061999341afb2bd4feb116930c2");
        params.put("page",offset);
        if(query != null) {
            params.put("q", query);
        }

        Log.d("this",url+"?"+params);
        client.get(url,params,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    String method = url;
                    JSONArray jsonArray;
                    if(url.equals(ARTICLE_SEARCH)) {
                        jsonArray = response.getJSONObject("response").getJSONArray("docs");
                    }
                    else if(url.equals(TOP_STORIES)) {
                        jsonArray = response.getJSONArray("results");
                    }
                    else {
                        jsonArray = null; //There will be an error if this is called
                    }

                    articles.addAll(Article.fromJSONArray(jsonArray));
                    if(articles.size() == 0)
                        Toast.makeText(SearchActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                } catch(JSONException e) {
                    e.printStackTrace();
                   }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("debug","failed to make response");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        imFilter = (MenuItem)menu.findItem(R.id.action_filter);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Set the imFilter to visible so you can narrow down your search!
                imFilter.setVisible(true);

                //Resetting params so that filters don't get applied during the next search
                params = new RequestParams();

                //Clearing articles to load new items on the first page
                articles.clear();

                //Launching results to search
                loadDataFromPageNumber(ARTICLE_SEARCH,query,0);

                //saving the query for use when adding filters
                inquiry = query;

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }


        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                imFilter.setVisible(false);
                articles.clear(); //resets the current articlees
                loadDataFromPageNumber(TOP_STORIES,null,0); //Closing search query will load top stories
                return false;
            }
        });


        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
//                Toast.makeText(SearchActivity.this, "Your focused changed!", Toast.LENGTH_SHORT).show();
                titleOn = !titleOn;
                getSupportActionBar().setDisplayShowTitleEnabled(titleOn);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    public void onCalendarClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this,R.anim.image_click));
        if(fm != null) {
            FiltersFragment ff = (FiltersFragment) (fm.findFragmentByTag("fragment_filters"));
            ff.runDatePicker(view);
        }
    }

    @Override
    public void OnFinishFiltersClick(Bundle bundle) {
        String sort = bundle.getString("sort");
        String date = bundle.getString("date");
        String[] checkBoxes = bundle.getStringArray("checkBoxes");

        if(!date.equals("")) {
            //Formatting date
            Scanner sc = new Scanner(date);
            sc.useDelimiter("/");
            String month = sc.next();
            if(month.length()<2)
                month = "0" + month;
            String day = sc.next();
            if(day.length() < 2)
                day = "0" + day;
            String year = sc.next();

            String formattedDate = year+month+day;
            params.put("begin_date",formattedDate);
        }

        //Adding sort param - TODO make a no sort spinner option if you want
        params.put("sort",sort.toLowerCase());

        //Formatting NewsDesk parameter
        String newsdesk = "news_desk:(";
        String addednewsdesk = "";
        for(int x = 0;x < checkBoxes.length;x++) {
            if(!checkBoxes[x].equals("")) {
                addednewsdesk+= "\"" + checkBoxes[x] + "\" ";
            }
        }
        if(!addednewsdesk.equals("")) {
            addednewsdesk = addednewsdesk.substring(0,addednewsdesk.length()-1) + ")";
            params.put("fq",newsdesk+addednewsdesk);
        }

        //Resets the articles so that new stuff loads on the second page
        articles.clear();
//        Toast.makeText(SearchActivity.this, "you searched for " + inquiry + "!", Toast.LENGTH_SHORT).show();
        loadDataFromPageNumber(ARTICLE_SEARCH,inquiry,0);
    }

    public void onFiltersClick(MenuItem item) {
        fm = getSupportFragmentManager();
        FiltersFragment filtersFragment = FiltersFragment.newInstance("Filters");
        filtersFragment.show(fm,"fragment_filters"); //this will load up the fragment!
    }
}
