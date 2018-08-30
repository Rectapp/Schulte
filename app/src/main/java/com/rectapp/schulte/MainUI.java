package com.rectapp.schulte;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rectapp.schulte.core.SchulteView;
import com.rectapp.schulte.model.Game;
import com.rectapp.schulte.util.SPUtil;

import java.util.Date;

public class MainUI extends AppCompatActivity {

    private SchulteView schulteView;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        initView();
    }

    private void initView() {
        schulteView = findViewById(R.id.schulte_view);
        schulteView.setOnSuccessListener((column, time) -> {
            if (column * column * 1000 - time < 0) {
                new AlertDialog.Builder(context).setMessage(R.string.retry).setPositiveButton(R.string.sure, (dialog, which) -> {
                    replayGame(column - 2);
                }).setNegativeButton(R.string.cancel, null).show();
            } else {
                //记录最佳成绩
                long levelBestTime = getLevelBestTime(column);
                if (levelBestTime != 0) {
                    if (time < levelBestTime) {
                        SPUtil.put(context, String.valueOf(column), time);
                        Game game = new Game(column, time, System.currentTimeMillis());
                        if (SchulteApplication.rank.rankList.contains(game)) {
                            SchulteApplication.rank.rankList.remove(game);
                        }
                        SchulteApplication.rank.rankList.add(0, game);
                    }
                } else {
                    SchulteApplication.rank.rankList.add(0, new Game(column, time, System.currentTimeMillis()));
                }
                new AlertDialog.Builder(context).setMessage(R.string.refresh_best).setPositiveButton(R.string.show, (dialog, which) -> {
                    showRank();
                }).setNegativeButton(R.string.next, (dialog, which) -> replayGame(column - 1)).show();
            }
        });
    }

    public long getLevelBestTime(int level) {
        for (Game game : SchulteApplication.rank.rankList) {
            if (level == game.level) {
                return game.time;
            }
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_replay:
                replayGame();
                break;
            case R.id.action_change:
                showLevelChoice();
                break;
            case R.id.action_rank:
                showRank();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLevelChoice() {
        new AlertDialog.Builder(context).setSingleChoiceItems(R.array.level, 0, (dialog, which) -> {
            replayGame(which + 1);
            dialog.dismiss();
        }).show();
    }

    private void showRank() {
        if (!SchulteApplication.rank.rankList.isEmpty()) {
            View rankView = View.inflate(context, R.layout.layout_rank, null);
            RecyclerView rankRecyclerView = rankView.findViewById(R.id.rv_rank);
            BaseQuickAdapter rankAdapter = new BaseQuickAdapter<Game, BaseViewHolder>(R.layout.item_rank, SchulteApplication.rank.rankList) {
                @Override
                protected void convert(BaseViewHolder helper, Game item) {
                    helper.setText(R.id.tv_level, String.valueOf(item.level*item.level));
                    helper.setText(R.id.tv_time, String.valueOf(item.time));
                    helper.setText(R.id.tv_date, new Date(item.date).toLocaleString());
                }
            };
            rankRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            rankRecyclerView.setAdapter(rankAdapter);
            new AlertDialog.Builder(context).setView(rankView).show();
        } else {
            Snackbar.make(schulteView, R.string.no_history, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void replayGame() {
        if (schulteView != null) {
            schulteView.rePlay();
        }
    }

    private void replayGame(int level) {
        if (schulteView != null) {
            schulteView.changeLevel(level);
        }
    }
}
