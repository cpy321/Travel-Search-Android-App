package com.example.pengyuchen.hw91;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.sf.json.JSONObject;

import java.net.URLEncoder;


public class detailActivity extends AppCompatActivity{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SharedPreferences sp;
    private ViewPager mViewPager;
    private String detail;
    private String placeId;
    private String yelp;

    private String placeUrl="#";
    private String address;
    private String name;
    private String icon;
    private String saveJson;
    private String lat;
    private String lon;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        RequestQueue queue = Volley.newRequestQueue(this);
        
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        String detailJson = bundle.getString("fromResult");
        setPlaceId( bundle.getString("fromResultPlaceId"));
        address = bundle.getString("fromResultAddress");
        name = bundle.getString("fromResultPlaceName");
        icon = bundle.getString("fromResultIcon");
        lat = bundle.getString("fromResultLat");
        lon = bundle.getString("fromResultLon");


        setmTitle(detailJson);


        if(JSONObject.fromObject(detailJson).get("status").toString().equals("OK")) {

            JSONObject resultObj = (JSONObject) JSONObject.fromObject(detailJson).get("result");

            if (resultObj.get("website") != null) {
                placeUrl = resultObj.get("website").toString();
            } else {
                placeUrl = resultObj.get("url").toString();
            }
        }

        toolbar.setTitle(name);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(onMenuItemClick);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        String url = "http://place-env.us-west-1.elasticbeanstalk.com/yelp?formatted_address="+address+"&name="+name;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<org.json.JSONObject>() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                    setYelp(response.toString());

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });
        queue.add(jsonObjectRequest);
    }


    public String getmTitle() {
        return detail;
    }

    public void setmTitle(String title) {
        this.detail = title;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getYelp() {
        return yelp;
    }

    public void setYelp(String placeId) {
        this.yelp = placeId;
    }

    public String getLon(){ return lon;}

    public String getLat(){ return lat;}

    public String getName(){return name;}


    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_share:
                    String url = "https://twitter.com/intent/tweet?text="+"Check out "+name+" located at "+address+". Website: "+placeUrl+"&hashtags=TravelAndEntertainmentSearch";
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    detailActivity.this.startActivity(intent);
                    break;
                case R.id.action_like:
                    sp = getSharedPreferences("Favorite", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    saveJson ="{\"icon\":\""+icon+"\", \"name\": \""+ name+"\", \"place_id\": \"" + placeId + "\", \"vicinity\": \"" + address + "\", \"geometry\": {\"location\":{\"lat\":\""+lat+"\",\"lng\":\""+lon+"\" } } }";

                    if(menuItem.getIcon().getCurrent().getConstantState()==getResources().getDrawable(R.drawable.heart_outline_white).getConstantState()){
                        menuItem.setIcon(R.drawable.heart_fill_white);
                        editor.putString(placeId,saveJson);
                        Toast.makeText(detailActivity.this, name+" was added to favorites", Toast.LENGTH_LONG).show();


                    }else{
                        menuItem.setIcon(R.drawable.heart_outline_white);
                        editor.remove(placeId);
                        Toast.makeText(detailActivity.this, name+" was removed  favorites", Toast.LENGTH_LONG).show();

                    }
                    editor.commit();


                    break;

            }

            if(!msg.equals("")) {
                Toast.makeText(detailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_detail, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态

        sp = getSharedPreferences("Favorite", Context.MODE_PRIVATE);
        if(sp.contains(placeId)){
            menu.findItem(R.id.action_like).setIcon(R.drawable.heart_fill_white);
        }else{
            menu.findItem(R.id.action_like).setIcon(R.drawable.heart_outline_white);
        }
        return super.onPrepareOptionsMenu(menu);
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




    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Context mContext;
        private String tabTitles[] = new String[]{"INFO", "PHOTOS", "MAP", "REVIEWS"};
        private int[] imageResId = {
                R.drawable.info_outline,
                R.drawable.photos,
                R.drawable.maps,
                R.drawable.review
        };

        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    infoFragment tab1 = new infoFragment();
                    return tab1;
                case 1:
                    photoFragment tab2 = new photoFragment();
                    return tab2;
                case 2:
                    mapFragment tab3 = new mapFragment();
                    return tab3;
                case 3:
                    reviewFragment tab4 = new reviewFragment();
                    return tab4;

                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            Drawable image = mContext.getResources().getDrawable(imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            // Replace blank spaces with image icon
            SpannableString sb = new SpannableString("   " + tabTitles[position]);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }


    }



}
