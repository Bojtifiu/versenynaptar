package com.example.kozma.versenynaptar;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kozma on 2018. 01. 11..
 */

class Race {
    int id;
    String name;
    String address;
    String logo;
    int category;
    String description;
    String category_name;
    int joined;
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> longset = new ArrayList<>();
    String date_starts;
    String date_ends;
    String website;
    Date date_start;
    Date date_end;

    Race(int id, String name, String address, String logo, int category, String description, String category_name, int joined) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.logo = logo;
        this.category = category;
        this.description = description;
        this.category_name = category_name;
        this.joined = joined;
    }

    @Override
    public String toString() {
        return "Race{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", logo='" + logo + '\'' +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", category_name='" + category_name + '\'' +
                ", joined=" + joined +
                ", images=" + images +
                ", longset=" + longset +
                ", data_start='" + date_start + '\'' +
                ", data_end='" + date_end + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
    Date stringToDate(String aDate) throws ParseException {

        if(aDate==null) return null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date stringDate = format.parse(aDate);
        return stringDate;

    }
}
