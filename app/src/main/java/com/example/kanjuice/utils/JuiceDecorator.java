package com.example.kanjuice.utils;

import com.example.kanjuice.R;

import java.util.HashMap;

public class JuiceDecorator {

    private static HashMap<String, Integer> imageIdMap;
    private static HashMap<String, Integer> kanTextMap;

    static {
        imageIdMap = new HashMap<>();
        imageIdMap.put("tea", R.drawable.tea);
        imageIdMap.put("lemon tea", R.drawable.lemon_tea);
        imageIdMap.put("ginger tea", R.drawable.ginger_tea);
        imageIdMap.put("coffee", R.drawable.coffee);
        imageIdMap.put("almond milk", R.drawable.almond);
        imageIdMap.put("register user",R.drawable.register_user);
        imageIdMap.put("buttermilk", R.drawable.buttermilk);
        kanTextMap = new HashMap<>();
        kanTextMap.put("tea", R.string.tea);
        kanTextMap.put("lemon tea", R.string.lemon_tea);
        kanTextMap.put("ginger tea", R.string.ginger_tea);
        kanTextMap.put("coffee", R.string.coffee);
        kanTextMap.put("almond milk", R.string.almond_milk);
        kanTextMap.put("buttermilk", R.string.buttermilk);

    }

    public static int matchImage(String name) {
        Integer id = imageIdMap.get(name.toLowerCase().trim());
        if (id == null) {
            id = R.drawable.mixed_fruit;
        }
        return id;
    }

    public static int matchKannadaName(String name) {
        Integer id = kanTextMap.get(name.toLowerCase().trim());
        if (id == null) {
            id = R.string.unknown_kan;
        }
        return id;
    }
}
