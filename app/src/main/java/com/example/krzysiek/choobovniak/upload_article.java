package com.example.krzysiek.choobovniak;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

import me.angrybyte.goose.Article;
import me.angrybyte.goose.ContentExtractor;

import static android.content.ContentValues.TAG;

enum ErrorStatus
{
    WrongIntent,
    NoUrl,
    UrlMalformed,
    CannotDownload,
    CannotFindArticle
}

public class upload_article extends AppCompatActivity {

    Animation scaleIn;
    TextView downloadingUrl, extractingArticle, speechSynthesis;
    String articleText, articleTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_article);

        scaleIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_in);
        scaleIn.reset();
        downloadingUrl = findViewById(R.id.downloadingUrl);
        extractingArticle = findViewById(R.id.extractingArticle);
        speechSynthesis = findViewById(R.id.speechSynthesis);


        //scaleInAnimate(extractingArticle);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                trySavingArticle(intent); // Handle text being sent
            }
            else{
                displayError(ErrorStatus.WrongIntent);// Handle single image being sent
            }
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    void scaleInAnimate(View view)
    {
        scaleIn.reset();
        view.clearAnimation();
        view.startAnimation(scaleIn);
    }
    void trySavingArticle(Intent intent)
    {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            scaleInAnimate(downloadingUrl);

            try {
                URL url = new URL(sharedText);
                new ExtractArticle().execute(url);
            }
            catch (Exception e) {}


        }
    }

    void displayError(ErrorStatus error)
    {
        //TODO
        return;
    }

    class ExtractArticle extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {

            me.angrybyte.goose.Configuration conf = new me.angrybyte.goose.Configuration(getApplicationContext().getCacheDir().toString());
            ContentExtractor extractor = new ContentExtractor(conf);

            Article article = extractor.extractContent(urls[0].toString(),true );
            if (article == null) {
                Log.e(TAG, "Couldn't load the article, is your URL correct, is your Internet working?");
                return (long)1;
            }

            articleText = article.getCleanedArticleText();
            articleTitle = article.getTitle();


            if (articleText == null) {
                Log.w(TAG, "Couldn't load the article text, the page is messy. Trying with page description...");
                //details = article.getMetaDescription();
            }


            publishProgress(1);





            return (long)0;
        }

        protected void onProgressUpdate(Integer... progress) {

            scaleInAnimate(extractingArticle);
        }

        protected void onPostExecute(Long result) {

            scaleInAnimate(speechSynthesis);
            Toast.makeText(getApplicationContext(), articleTitle, Toast.LENGTH_LONG).show();

        }
    }

}
