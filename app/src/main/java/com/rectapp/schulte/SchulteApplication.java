package com.rectapp.schulte;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rectapp.schulte.model.Rank;
import com.rectapp.schulte.util.SPUtil;

import java.util.ArrayList;

public class SchulteApplication extends Application {

    public static Rank rank;

    @Override
    public void onCreate() {
        super.onCreate();
        String rankJson = (String) SPUtil.get(this, "rank", "");
        if (TextUtils.isEmpty(rankJson)) {
            rank = new Rank();
            rank.rankList = new ArrayList<>();
        } else {
            rank = new Gson().fromJson(rankJson, Rank.class);
        }
    }

}
