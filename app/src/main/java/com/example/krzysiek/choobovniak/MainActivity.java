package com.example.krzysiek.choobovniak;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;

import me.angrybyte.goose.Article;
import me.angrybyte.goose.ContentExtractor;
import me.angrybyte.goose.network.GooseDownloader;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    Configuration conf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    URL a = new URL("https://www.wired.com/story/iphone-x-camera-and-faceid-test/");

                    ExtractArticle extractArticle = new ExtractArticle();

                    extractArticle.execute(a);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    class ExtractArticle extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            int count = urls.length;


            me.angrybyte.goose.Configuration conf = new me.angrybyte.goose.Configuration(getApplicationContext().getCacheDir().toString());
            ContentExtractor extractor = new ContentExtractor(conf);

            Article article = extractor.extractContent(urls[0].toString(),true );
            if (article == null) {
                Log.e(TAG, "Couldn't load the article, is your URL correct, is your Internet working?");
                return (long)1;
            }


            String details = article.getCleanedArticleText();
            if (details == null) {
                Log.w(TAG, "Couldn't load the article text, the page is messy. Trying with page description...");
                details = article.getMetaDescription();
            }
            Log.e("Article", details);




            return (long)0;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
    }

}
