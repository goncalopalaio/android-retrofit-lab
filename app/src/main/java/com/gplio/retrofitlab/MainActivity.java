package com.gplio.retrofitlab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.gplio.retrofitlab.beyonce.ItunesResults;
import com.gplio.retrofitlab.beyonce.Result;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    Handler mainHandler;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String term = "";
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    term = getResources().getString(R.string.title_home);
                    retrieveArtist(term);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    term = getResources().getString(R.string.title_dashboard);
                    retrieveArtist(term);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    term = getResources().getString(R.string.title_notifications);
                    retrieveArtist(term);
                    return true;


            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        mTextMessage.setText("");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Result obj = (Result) msg.obj;
                CharSequence text = mTextMessage.getText();
                mTextMessage.setText(text +"\n"+ obj.getTrackName());
            }
        };
    }


    private static String endpoint = "https://itunes.apple.com/";
    private interface Beyonce {
        @GET("search?entity=musicVideo")
        //term=beyonce
        Call<ItunesResults> retrieveResults(@Query("term") String artistName);
    }

    private void retrieveArtist(final String artist) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(endpoint)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Beyonce beyonce = retrofit.create(Beyonce.class);
                Call<ItunesResults> text = beyonce.retrieveResults(artist);
                try {
                    ItunesResults body = text.execute().body();

                    for (Result result : body.getResults()) {
                        Log.d("ZZZ", String.valueOf(result));
                        Message message = mainHandler.obtainMessage(11, result);
                        message.sendToTarget();
                    }

                } catch (IOException e) {
                    Log.e("ZZZ", String.valueOf(e));
                }
            }
        }).start();
    }

}
