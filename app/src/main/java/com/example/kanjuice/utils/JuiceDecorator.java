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


        kanTextMap = new HashMap<>();
        kanTextMap.put("amla", R.string.amla_kan);
        kanTextMap.put("black jeera masala soda", R.string.black_jeera_masala_soda_kan);
        kanTextMap.put("cucumber", R.string.cucumber_kan);
        kanTextMap.put("ginger lime", R.string.ginger_lime_kan);
        kanTextMap.put("ginger mint lime", R.string.ginger_mint_lime_kan);
        kanTextMap.put("grapes", R.string.grapes_kan);
        kanTextMap.put("grape", R.string.grapes_kan);
        kanTextMap.put("jal jeera", R.string.jal_jeera_kan);
        kanTextMap.put("jal jeera soda", R.string.jal_jeera_kan);
        kanTextMap.put("kokum", R.string.kokum_kan);
        kanTextMap.put("lime", R.string.lime_kan);
        kanTextMap.put("mint lime", R.string.mint_lime_kan);
        kanTextMap.put("mixed fruit", R.string.mixed_fruit_kan);
        kanTextMap.put("mix fruit", R.string.mixed_fruit_kan);
        kanTextMap.put("muskmelon", R.string.muskmelon_kan);
        kanTextMap.put("musk melon", R.string.muskmelon_kan);
        kanTextMap.put("orange", R.string.orange_kan);
        kanTextMap.put("pineapple", R.string.pineapple_kan);
        kanTextMap.put("salt lime soda", R.string.salt_lime_soda_kan);
        kanTextMap.put("soda", R.string.soda_kan);
        kanTextMap.put("sweet and salt lime soda", R.string.sweet_and_salt_lime_soda_kan);
        kanTextMap.put("sweet & salt lime soda", R.string.sweet_and_salt_lime_soda_kan);
        kanTextMap.put("sweet lime soda", R.string.sweet_lime_soda_kan);
        kanTextMap.put("watermelon", R.string.watermelon_kan);
        kanTextMap.put("water melon", R.string.watermelon_kan);
        kanTextMap.put("banana", R.string.banana_kan);
        kanTextMap.put("banana shake", R.string.banana_kan);
        kanTextMap.put("butter fruit", R.string.butter_fruit_kan);
        kanTextMap.put("butterfruit", R.string.butter_fruit_kan);
        kanTextMap.put("sapota", R.string.sapota_kan);
        kanTextMap.put("apple", R.string.apple_kan);
        kanTextMap.put("mosambi", R.string.moosambi_kan);
        kanTextMap.put("grapes", R.string.grapes_kan);
        kanTextMap.put("grape", R.string.grapes_kan);
        kanTextMap.put("mango", R.string.mango_kan);
        kanTextMap.put("custard apple", R.string.custard_apple);
        kanTextMap.put("custardapple", R.string.custard_apple);
        kanTextMap.put("papaya", R.string.papaya);
        kanTextMap.put("fruits", R.string.fruits);


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
