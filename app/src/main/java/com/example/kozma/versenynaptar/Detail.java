package com.example.kozma.versenynaptar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.kozma.versenynaptar.Calendar.getJoined;
import static com.example.kozma.versenynaptar.Login.currentApiVersion;
import static com.example.kozma.versenynaptar.Login.tmp;

public class Detail extends AppCompatActivity {

    private TextView name;
    private TextView address;
    private TextView date;
    private TextView days;
    private TextView description;

    private Button join;
    private Button back;

    private ImagesAdapter imagesAdapter;
    private ViewPager images;

    private ArrayList<String> kitolto =  new ArrayList<>();

    private final String joinURL = "http://dev.balaz98.hu/mozaikmed_naptar/api/join_event";
    private final String leaveURL = "http://dev.balaz98.hu/mozaikmed_naptar/api/leave_event";

    private String joinResult;
    private String leaveResult;

    private JSONObject jsonObject;

    private String tmp;

    Race r;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Login.viewChanger(this);

        kitolto.add("https://greatist.com/sites/default/files/running.jpg");
        kitolto.add("http://kadarka.net/wp-content/uploads/2015/04/futok.jpg");

        int id = getIntent().getExtras().getInt("ID");

        for(Race race: Calendar.races) {
            if(race.id == id) {
                r = race;
                break;
            }
        }

        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        date = (TextView) findViewById(R.id.date);
        days = (TextView) findViewById(R.id.day);
        description = (TextView) findViewById(R.id.description);

        join = (Button) findViewById(R.id.join);
        back = (Button) findViewById(R.id.back);

        if(r.joined == 0) {
            join.setText("Csatlakozás");
        }
        else {
            join.setText("Lecsatlakozás");
        }

        images = (ViewPager) findViewById(R.id.images);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        name.setText(r.name);
        address.setText(r.address);
        description.setText(r.description);
        if(r.date_start != null) {
            String day  = (String) DateFormat.format("dd", r.date_start); // 20
            date.setText(day);
            if(r.date_start.getDay() == 0) {
                days.setText("Vasárnap");
            }
            else if(r.date_start.getDay() == 1) {
                days.setText("Hétfő");
            }
            else if(r.date_start.getDay() == 2) {
                days.setText("Kedd");
            }
            else if(r.date_start.getDay() == 3) {
                days.setText("Szerda");
            }
            else if(r.date_start.getDay() == 4) {
                days.setText("Csütörtök");
            }
            else if(r.date_start.getDay() == 5) {
                days.setText("Péntek");
            }
            else if(r.date_start.getDay() == 6) {
                days.setText("Szombat");
            }
        }
        if(r.images.size() == 0) {
            imagesAdapter = new ImagesAdapter(Detail.this, kitolto);
        }
        else {
            imagesAdapter = new ImagesAdapter(Detail.this, r.images);
        }
        images.setAdapter(imagesAdapter);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(r.joined == 0) {
                    tmp = Login.tmp+"&event_id="+r.id;
                    try {
                        joinResult = new HttpAsyncTask().execute(joinURL, tmp).get();
                        jsonObject = new JSONObject(joinResult);
                        if(jsonObject.getInt("success") == 1) {
                            join.setText("Lecsatlakozás");
                            r.joined = 1;
                        }
                        getJoined();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    tmp = Login.tmp+"&event_id="+r.id;
                    try {
                        leaveResult = new HttpAsyncTask().execute(leaveURL, tmp).get();
                        jsonObject = new JSONObject(leaveResult);
                        if(jsonObject.getInt("success") == 1) {
                            join.setText("Csatlakozás");
                            r.joined = 0;
                        }
                        getJoined();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


}
