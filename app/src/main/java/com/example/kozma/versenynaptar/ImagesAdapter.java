package com.example.kozma.versenynaptar;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagesAdapter extends PagerAdapter {
    private Activity activity;
    private String[] images;

    public ImagesAdapter(Activity activity, ArrayList<String> imagesArray) {
        this.activity = activity;
        images = imagesArray.toArray(new String[imagesArray.size()]);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container, false);
        ImageView imageView;
        imageView = (ImageView) itemView.findViewById(R.id.kep);
        DisplayMetrics dis = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
        int height = dis.heightPixels;
        int width = dis.widthPixels;
        imageView.setMinimumHeight(height);
        imageView.setMinimumWidth(width);
        Picasso.with(activity.getApplicationContext()).load(images[position]).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into(imageView);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container ).removeView((View)object);
    }
}
