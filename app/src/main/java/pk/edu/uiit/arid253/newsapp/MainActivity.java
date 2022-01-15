package pk.edu.uiit.arid253.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NewsItemClicked {

    private NewsListAdapter adptr;

    Button mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapV);

        RecyclerView recView = findViewById(R.id.recycleView);
        recView.setLayoutManager(new LinearLayoutManager(this));

        adptr = new NewsListAdapter((NewsItemClicked) this);
        ftchData();
        recView.setAdapter(adptr);

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapView.class));
            }
        });
    }

    private void ftchData(){
        String url = "https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=273c8def451f4089b2a12edde22aaefa";
//        String gNewsUrl = "https://gnews.io/api/v4/search?q=example&token=070414819d40a332a00b5b37d1a5214c";
        Context context = getApplicationContext();
        Toast.makeText(context,"Fetching from url",Toast.LENGTH_LONG).show();
        JsonObjectRequest jOR = new JsonObjectRequest(Request.Method.GET, url, null,
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
                                        newsObj.getString("urlToImage")
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
        }){

            /**
             * Passing some request headers
             * @return
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("User-Agent", "Mozilla/5.0");
                return headers;
            }
        };;
        MySingleton.getInstance(this).addToRequestQueue(jOR);
    }

    @Override
    public void onItemClicked(News item) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(item.url));
    }


}