package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewsItemClicked {

    private NewsListAdapter adptr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recView = findViewById(R.id.recycleView);
        recView.setLayoutManager(new LinearLayoutManager(this));

        adptr = new NewsListAdapter(this);
        ftchData();
        recView.setAdapter(adptr);
    }


    private void ftchData(){
        //String url = "http://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=273c8def451f4089b2a12edde22aaefa";
        String gNewsUrl = "https://gnews.io/api/v4/search?q=example&token=070414819d40a332a00b5b37d1a5214c";
        Context context = getApplicationContext();
        Toast.makeText(context,"News checking",Toast.LENGTH_LONG).show();
        JsonObjectRequest jOR = new JsonObjectRequest(Request.Method.GET, gNewsUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray newsJsonArray = null;

                        try {
                            Toast.makeText(context,"News loading",Toast.LENGTH_LONG).show();
                            newsJsonArray = response.getJSONArray("articles");
                            ArrayList<News> newsArray = new ArrayList<>();
                            for (int i = 0; i < newsJsonArray.length(); i++ ){
                                JSONObject newsObj = newsJsonArray.getJSONObject(i);
                                JSONObject sourceObj = newsObj.getJSONObject("source");
                                News news = new News(
                                        newsObj.getString("title"),
                                        sourceObj.getString("name"),
                                        newsObj.getString("url"),
                                        newsObj.getString("image")
                                );
                                newsArray.add(news);
                            }
                            adptr.updateNews(newsArray);

                        } catch (JSONException e) {
                            Toast.makeText(context,"Json exp..."+ e,Toast.LENGTH_LONG).show();
                        }
                    }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Unable to load News. Please Retry..." + error,Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jOR);
    }

    @Override
    public void onItemClicked(News item) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(item.url));
    }
}