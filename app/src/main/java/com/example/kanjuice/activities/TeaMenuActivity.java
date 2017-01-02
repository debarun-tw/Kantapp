package com.example.kanjuice.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.kanjuice.JuiceServer;
import com.example.kanjuice.KanJuiceApp;
import com.example.kanjuice.R;
import com.example.kanjuice.TokenServer;
import com.example.kanjuice.adapters.CTLAdapter;
import com.example.kanjuice.models.GCMToken;
import com.example.kanjuice.models.HotDrink;
import com.example.kanjuice.models.TeaItem;
import com.example.kanjuice.service.GCMRegistrationIntentService;
import com.example.kanjuice.util.Logger;
import com.example.kanjuice.utils.JuiceDecorator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class TeaMenuActivity extends Activity {

    private static final String TAG = "TeaMenuActivity";
    private static final String TOKEN_URL = "http://10.132.127.212:4000";
    private CTLAdapter adapter;
    private boolean isInMultiSelectMode = false;
    private View goButton;
    private View cancelButton;
    private View actionButtonLayout;
    private View noNetworkView;
    private GridView hotDrinksView;
    private View menuLoadingView;
    private BroadcastReceiver broadcastReceiver;
    private Logger logger = Logger.loggerFor(TeaMenuActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_juice_menu);
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        startService(intent);

        setupViews();
        Intent service = new Intent(this, GCMRegistrationIntentService.class);
        startService(service);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Splash activity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        disableRecentAppsClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiveGCMToken();
        exitMultiSelectMode();

        fetchMenu();
        Log.d("Splash activity", "resume");
        registerForGCM();
    }

    private void receiveGCMToken() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_SUCESS)) {
                    String token = intent.getStringExtra("token");
                    sendToken(token);
                } else if (intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_FAILD)) {
                    Toast.makeText(context, "some unknown error occurs try again later..", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void registerForGCM() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new
                IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_FAILD));
    }

    @Override
    public void onBackPressed() {
        if (isInMultiSelectMode) {
            exitMultiSelectMode();
        }

        // else block back button
    }

    private void disableRecentAppsClick() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    private void fetchMenu() {
        getJuiceServer().getJuices(new Callback<List<HotDrink>>() {
            @Override
            public void success(final List<HotDrink> juices, Response response) {
                TeaMenuActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        menuLoadingView.setVisibility(View.GONE);
                        hotDrinksView.setVisibility(View.VISIBLE);
                        Iterator<HotDrink> allDrinks = juices.iterator();
                        List<HotDrink> hotDrinks = filterHotDrinks(allDrinks);
                        onJuicesListReceived(hotDrinks);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to fetch menu list : " + error);
                TeaMenuActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showNoNetworkView();
                    }
                });

            }
        });
    }

    @NonNull
    private List<HotDrink> filterHotDrinks(Iterator<HotDrink> allDrinks) {
        HashSet<String> ctl = new HashSet<>();
        ctl.add("tea");
        ctl.add("lemon tea");
        ctl.add("ginger tea");
        ctl.add("coffee");
        List<HotDrink> hotDrinks = new ArrayList<>();
        while (allDrinks.hasNext()) {
            HotDrink juice = allDrinks.next();
            if (ctl.contains(juice.name.toLowerCase())) {
                hotDrinks.add(juice);
            }
        }
        return hotDrinks;
    }

    private void showNoNetworkView() {
        noNetworkView.setVisibility(View.VISIBLE);
        hotDrinksView.setVisibility(View.GONE);
        menuLoadingView.setVisibility(View.GONE);
    }


    private void onJuicesListReceived(List<HotDrink> hotDrinks) {
        decorate(hotDrinks);
        adapter.addAll(hotDrinks);
    }

    private void decorate(List<HotDrink> hotDrinks) {
        for (HotDrink hotDrink : hotDrinks) {
            hotDrink.imageId = JuiceDecorator.matchImage(hotDrink.name);
            hotDrink.kanId = JuiceDecorator.matchKannadaName(hotDrink.name);
        }
    }

    private KanJuiceApp getApp() {
        return (KanJuiceApp) getApplication();
    }

    private JuiceServer getJuiceServer() {
        return getApp().getJuiceServer();
    }

    private void setupViews() {
        hotDrinksView = (GridView) findViewById(R.id.grid);
        setupAdapter(hotDrinksView);

        hotDrinksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onJuiceItemClick(position);
            }
        });

        hotDrinksView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return onHotDrinksItemLongClick(position);
            }
        });

        setupActionLayout();
        setupNoNetworkLayout();

        menuLoadingView = findViewById(R.id.loading);
    }

    private void setupNoNetworkLayout() {
        noNetworkView = findViewById(R.id.no_network_layout);
        findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLoadingView.setVisibility(View.VISIBLE);
                noNetworkView.setVisibility(View.INVISIBLE);
                fetchMenu();
            }
        });
    }

    private void sendToken(String token) {
        GCMToken gcmToken = new GCMToken();
        gcmToken.setLocation(getString(R.string.location));
        gcmToken.setOutletType(getString(R.string.outlet_type));
        gcmToken.setDeviceID(getString(R.string.device_id));
        gcmToken.setGcmToken(token);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TOKEN_URL)
                .build();

        TokenServer server = restAdapter.create(TokenServer.class);
        server.send(gcmToken, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                logger.d("token sending is successful and status is :" + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                logger.d("token sending is failed");
            }
        });
    }

    private boolean onHotDrinksItemLongClick(int position) {
        if (isRegisterActivity(position)) {
            return false;
        }
        adapter.toggleSelectionChoice(position);
        if (!isInMultiSelectMode) {
            enterMultiSelectionMode();
        }
        return true;
    }

    private void onJuiceItemClick(int position) {
        if (isRegisterActivity(position)) {
            Intent intent = new Intent(TeaMenuActivity.this, CardSwipeActivity.class);
            startActivity(intent);
        } else {
            if (isInMultiSelectMode) {
                adapter.toggleSelectionChoice(position);
            } else {
                gotoSwipingScreen(position);
            }
        }
    }

    private boolean isRegisterActivity(int position) {
        return ((TeaItem) adapter.getItem(position)).teaName.equals("Register User");
    }

    private void setupActionLayout() {
        actionButtonLayout = findViewById(R.id.action_button_layout);

        goButton = actionButtonLayout.findViewById(R.id.order);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSwipingScreen();
            }
        });

        cancelButton = actionButtonLayout.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitMultiSelectMode();
            }
        });
    }

    private void setupAdapter(GridView juicesView) {
        adapter = new CTLAdapter(this);
        juicesView.setAdapter(adapter);
    }

    private void exitMultiSelectMode() {
        adapter.reset();

        isInMultiSelectMode = false;

        ObjectAnimator anim = ObjectAnimator.ofFloat(actionButtonLayout, "translationY", -20f, 200f);
        anim.setDuration(500);
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                actionButtonLayout.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    private void enterMultiSelectionMode() {
        isInMultiSelectMode = true;

        actionButtonLayout.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(actionButtonLayout, "translationY", 100f, -15f);
        anim.setDuration(500);
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                actionButtonLayout.setVisibility(View.VISIBLE);
            }
        });
        anim.start();
    }

    private void gotoSwipingScreen() {
        gotoSwipingScreen(adapter.getSelectedJuices());
    }

    private void gotoSwipingScreen(int position) {
        gotoSwipingScreen(new TeaItem[]{(TeaItem) adapter.getItem(position)});
    }

    private void gotoSwipingScreen(TeaItem[] teaItems) {
        Intent intent = new Intent(TeaMenuActivity.this, UserInputActivity.class);
        intent.putExtra("hotDrinks", teaItems);
        startActivity(intent);
    }
}
