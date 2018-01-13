package com.example.kozma.versenynaptar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.kozma.versenynaptar.Login.currentApiVersion;

public class Calendar extends AppCompatActivity {

    public final String raceURL = "http://dev.balaz98.hu/mozaikmed_naptar/api/events";
    public final String categoriesURL = "http://dev.balaz98.hu/mozaikmed_naptar/api/categories";
    public static final String joinedURL = "http://dev.balaz98.hu/mozaikmed_naptar/api/events_joined";

    public String resultRace;
    public String resultCategories;
    public static String resultJoined;

    private JSONObject jsonObject;
    private JSONObject jsonObjectCateg;
    private static JSONObject jsonObjectJoined;

    private JSONArray jsonRaces;
    private JSONArray jsonCategs;
    private static JSONArray jsonJoinedRaces;

    public static ArrayList<Race> races = new ArrayList<>();
    public ArrayList<Category> categories = new ArrayList<>();
    public static ArrayList<Race> joinedRaces = new ArrayList<>();

    private ListView listView;
    private EAdapter eAdapter;
    private ToggleButton toggleButton;
    public ArrayList<Race> list = new ArrayList<>();

    private Button left_c;
    private Button left_m;

    private Button right_c;
    private Button right_m;

    private TextView category;
    private TextView month;

    private final String[] months = {"Január", "Február", "Március", "Április", "Május", "Június", "Július", "Augusztus", "Szeptember", "Október", "November", "December"};

    private int categplace = 0;
    private int monthplace = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Login.viewChanger(this);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        left_c = (Button) findViewById(R.id.left_c);
        left_m = (Button) findViewById(R.id.left_m);
        right_c = (Button) findViewById(R.id.right_c);
        right_m = (Button) findViewById(R.id.right_m);

        category = (TextView) findViewById(R.id.category);
        month = (TextView) findViewById(R.id.month);

        races.clear();
        list.clear();
        categories.clear();
        joinedRaces.clear();

        try {
            resultRace = new HttpAsyncTask().execute(raceURL, Login.tmp).get();
            resultCategories = new HttpAsyncTask().execute(categoriesURL, "apikey=asd").get();

            jsonObject = new JSONObject(resultRace);
            jsonObjectCateg = new JSONObject(resultCategories);

            jsonRaces = jsonObject.getJSONObject("data").getJSONArray("events");
            jsonCategs = jsonObjectCateg.getJSONObject("data").getJSONArray("categories");

            System.out.println("Number of events: "+jsonRaces.length());
            System.out.println("Number of categories: "+jsonCategs.length());

            for (int i = 0; i < jsonRaces.length(); i++) {
                races.add(jsonObjectToRace(jsonRaces.getJSONObject(i)));
            }

            for (int i = 0; i < jsonCategs.length(); i++) {
                categories.add(jsonObjectToCategory(jsonCategs.getJSONObject(i)));
            }
            System.out.println(categories.size());
            System.out.println(jsonRaces.get(0).toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getJoined();
        listView = (ListView) findViewById(R.id.listView);

        category.setText(categories.get(categplace).name);
        left_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(categplace == 0) categplace = categories.size()-1;
                else categplace--;
                category.setText(categories.get(categplace).name);
                sort();
            }
        });

        right_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(categplace == categories.size()-1) categplace = 0;
                else categplace++;
                category.setText(categories.get(categplace).name);
                sort();
            }
        });

        month.setText(months[0]);
        left_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(monthplace == 0) monthplace = months.length-1;
                else monthplace--;
                month.setText(months[monthplace]);
                sort();
            }
        });

        right_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(monthplace == 11) monthplace = 0;
                else monthplace++;
                month.setText(months[monthplace]);
                sort();
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    list.clear();
                    sort();
                    eAdapter.notifyDataSetChanged();
                }
                else {
                    list.clear();
                    sort();
                    eAdapter.notifyDataSetChanged();
                }
            }
        });

        eAdapter = new EAdapter(getApplicationContext(), R.layout.raw, list);

        listView.setAdapter(eAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Race item = (Race) parent.getItemAtPosition(position);
                Intent i = new Intent(view.getContext(), Detail.class);
                i.putExtra("ID", item.id);
                startActivity(i);
            }
        });
        sort();
        for(String s: races.get(0).images) {
            System.out.println(s);
        }
    }
    public void sort() {
        list.clear();
        if(toggleButton.isChecked()) {
            System.out.println("joined");
            for (Race r : joinedRaces) {
                if (r.category == categories.get(categplace).id) {
                    if (monthplace == r.date_start.getMonth()) {
                        list.add(r);
                    }
                }
            }
        }
        else {
            System.out.println("all");
            for (Race r : races) {
                if (r.category == categories.get(categplace).id) {
                    if (monthplace == r.date_start.getMonth()) {
                        list.add(r);
                    }
                }
            }
        }
        eAdapter.notifyDataSetChanged();
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

    public static Race jsonObjectToRace(JSONObject jsonObject) {
        Race r = null;
        try {
            r = new Race(jsonObject.getInt("id"),
                    jsonObject.getString("name"),
                    jsonObject.getString("address"),
                    jsonObject.getString("logo"),
                    jsonObject.getInt("category"),
                    jsonObject.getString("description"),
                    jsonObject.getString("category_name"),
                    jsonObject.getInt("joined"));
            r.website = jsonObject.getString("website");
            r.date_starts = jsonObject.getString("date_start");
            r.date_ends = jsonObject.getString("date_end");
            r.date_start  = r.stringToDate(r.date_starts);
            r.date_end  = r.stringToDate(r.date_ends);

            for (int i = 0; i < jsonObject.getJSONArray("images").length(); i++) {
                r.images.add(jsonObject.getJSONArray("images").getString(i));
            }
            for (int i = 0; i < jsonObject.getJSONArray("longset").length(); i++) {
                r.longset.add(jsonObject.getJSONArray("longset").getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return r;
    }
    public Category jsonObjectToCategory(JSONObject jsonObject) {
        Category c = null;
        try {
            c = new Category(jsonObject.getInt("id"),
                    jsonObject.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }

    public class EAdapter extends ArrayAdapter {

        private ArrayList<Race> races;
        private int resource;
        private LayoutInflater inflater;

        public EAdapter(Context context, int resource, ArrayList<Race> objects) {
            super(context, resource, objects);
            races = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.day = (TextView) convertView.findViewById(R.id.day);
                holder.logo = (ImageView) convertView.findViewById(R.id.logo);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Then later, when you want to display image
            Picasso.with(getContext()).load(races.get(position).logo).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into(holder.logo);
            holder.name.setText(races.get(position).name);
            holder.address.setText(races.get(position).address);
            if(races.get(position).date_start != null) {
                String day  = (String) DateFormat.format("dd", races.get(position).date_start); // 20
                holder.date.setText(day);
                if(races.get(position).date_start.getDay() == 0) {
                    holder.day.setText("Vasárnap");
                }
                else if(races.get(position).date_start.getDay() == 1) {
                    holder.day.setText("Hétfő");
                }
                else if(races.get(position).date_start.getDay() == 2) {
                    holder.day.setText("Kedd");
                }
                else if(races.get(position).date_start.getDay() == 3) {
                    holder.day.setText("Szerda");
                }
                else if(races.get(position).date_start.getDay() == 4) {
                    holder.day.setText("Csütörtök");
                }
                else if(races.get(position).date_start.getDay() == 5) {
                    holder.day.setText("Péntek");
                }
                else if(races.get(position).date_start.getDay() == 6) {
                    holder.day.setText("Szombat");
                }
            }

            return convertView;
        }


        class ViewHolder {
            private TextView date;
            private TextView day;
            private ImageView logo;
            private TextView address;
            private TextView name;
        }

    }
    public static void getJoined(){
        try {
            joinedRaces.clear();
            resultJoined = new HttpAsyncTask().execute(joinedURL, Login.tmp).get();
            jsonObjectJoined = new JSONObject(resultJoined);
            jsonJoinedRaces = jsonObjectJoined.getJSONObject("data").getJSONArray("events");
            System.out.println("Number of joined events: "+jsonJoinedRaces.length());
            for (int i = 0; i < jsonJoinedRaces.length(); i++) {
                joinedRaces.add(jsonObjectToRace(jsonJoinedRaces.getJSONObject(i)));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
