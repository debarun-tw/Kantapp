package com.example.kanjuice.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kanjuice.JuiceServer;
import com.example.kanjuice.KanJuiceApp;
import com.example.kanjuice.R;
import com.example.kanjuice.models.HotDrink;
import com.example.kanjuice.utils.JuiceDecorator;
import com.example.kanjuice.utils.TypedJsonString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class AdminActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "Admin";
    private ListAvailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setupViews();
        fetchMenu();
    }

    private KanJuiceApp getApp() {
        return (KanJuiceApp) getApplication();
    }

    private JuiceServer getJuiceServer() {
        return getApp().getJuiceServer();
    }

    private void setupViews() {
        ListView list = (ListView) findViewById(R.id.list);
        adapter = new ListAvailAdapter(this, new ArrayList<HotDrink>());
        list.setAdapter(adapter);
    }

    private void setJuiceAvailability(final HotDrink juice) {
        Log.d(TAG, "setJuiceAvailability: " + juice.asJson());
        getJuiceServer().updateJuice(new TypedJsonString(juice.asJson()), new Callback<Response>() {

            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "Updated juice availibility");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "failed to update  juice availibility");
                juice.available = !juice.available;
            }
        });
    }

    private void fetchMenu() {
        getJuiceServer().getJuices(new Callback<List<HotDrink>>() {
            @Override
            public void success(final List<HotDrink> juices, Response response) {
                AdminActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Iterator<HotDrink> allDrinks = juices.iterator();
                        List<HotDrink> hotDrinks = filterHotDrinks(allDrinks);
                        adapter.addAll(hotDrinks);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to fetch menu list : " + error);


            }
        });
    }

    @NonNull
    private List<HotDrink> filterHotDrinks(Iterator<HotDrink> allDrinks) {
        List<HotDrink> hotDrinks = new ArrayList<>();
        while (allDrinks.hasNext()) {
            HotDrink ctl = allDrinks.next();
            if (ctl.type.toLowerCase().equals("ctl")) {
                hotDrinks.add(ctl);
            }
        }
        return hotDrinks;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        HotDrink juice = (HotDrink) buttonView.getTag();
        if (juice != null) {
            juice.available = !juice.available;
            setJuiceAvailability(juice);
        }
    }

    public static class ListAvailAdapter extends BaseAdapter {

        private AdminActivity adminActivity;
        private final List<HotDrink> juices;
        private LayoutInflater inflater;

        public ListAvailAdapter(AdminActivity adminActivity, List<HotDrink> juices) {
            this.adminActivity = adminActivity;
            this.juices = juices;
            inflater = (LayoutInflater) adminActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return juices.size();
        }

        @Override
        public Object getItem(int position) {
            return juices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.juice_avail_item, parent, false);
            bind(view, (HotDrink) getItem(position));
            return view;
        }

        private void bind(View view, final HotDrink juice) {
            TextView titleView = (TextView) view.findViewById(R.id.title);
            titleView.setText(juice.name);

            TextView kanTitleView = (TextView) view.findViewById(R.id.title_kan);
            kanTitleView.setText(JuiceDecorator.matchKannadaName(juice.name));

            CheckBox availabilityView = (CheckBox) view.findViewById(R.id.availability);
            availabilityView.setChecked(juice.available);
            availabilityView.setOnCheckedChangeListener(adminActivity);
            availabilityView.setTag(juice);

            view.setTag(juice);
        }

        public void addAll(List<HotDrink> juices) {
            this.juices.addAll(juices);
            notifyDataSetChanged();
        }
    }
}
