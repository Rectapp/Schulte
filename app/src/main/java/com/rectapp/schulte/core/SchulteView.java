package com.rectapp.schulte.core;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.rectapp.schulte.R;

import java.util.ArrayList;
import java.util.Collections;


public class SchulteView extends RelativeLayout {

    private int mColumn = 5;
    private int mMargin = 2;
    private int mPadding;
    private boolean once;
    private int mWidth;
    private int mItemWidth;
    private ArrayList<String> mNums;
    private int mMarginTop = 1;
    private GameCallBack gameCallBack;
    private int currentNum = 0;
    private Button lastView;
    private ItemClickListener itemClickListener;
    private long gameStartTime;

    public SchulteView(Context context) {
        this(context, null);
    }

    public SchulteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SchulteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //把设置的margin值转换为dp
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mMargin, getResources().getDisplayMetrics());
        mMarginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mMarginTop, getResources().getDisplayMetrics());
        // 设置Layout的内边距，四边一致，设置为四内边距中的最小值
        mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
                getPaddingBottom());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获得游戏布局的边长
        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());
        if (!once) {
            initData();
            initItem();
            rePlay();
        }
        once = true;
        setMeasuredDimension(mWidth, mWidth);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return true;
//    }

    private void initData() {
        if (mNums == null) {
            mNums = new ArrayList<>();
        }
        for (int i = 0; i < mColumn * mColumn; i++) {
            mNums.add(String.valueOf(i + 1));
        }
    }

    public void setOnSuccessListener(GameCallBack callBack) {
        gameCallBack = callBack;
    }

    public interface GameCallBack {
        void onSuccess(int column, long time);
    }

    public void rePlay() {
        Collections.shuffle(mNums);
        initItem();
        currentNum = 0;
        gameStartTime = System.currentTimeMillis();
    }

    public void changeLevel(int level) {
        mColumn = level + 2;
        mNums.clear();
        initData();
        rePlay();
    }

    class ItemClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Button textView = (Button) v;
            if (lastView != null) {
                lastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            int focusNum = Integer.parseInt(textView.getText().toString());
            if (currentNum == 0) {
                gameStartTime = System.currentTimeMillis();
            }
            if (focusNum - currentNum == 1) {
                currentNum++;
                textView.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                if (currentNum >= mNums.size()) {
                    if (gameCallBack != null) {
                        gameCallBack.onSuccess(mColumn, System.currentTimeMillis() - gameStartTime);
                    }
                }
            } else {
                //提示错误
                textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            lastView = textView;
        }
    }

    private void initItem() {
        removeAllViews();
        mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1))
                / mColumn;
        Button[] mItems = new Button[mColumn * mColumn];
        for (int i = 0; i < mItems.length; i++) {

            LayoutParams lp = new LayoutParams(mItemWidth,
                    mItemWidth);
            Button textView = new Button(getContext());
            mItems[i] = textView;
            textView.setText(mNums.get(i));
            textView.setTextSize(20);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            textView.setId(i + 1);
            textView.setTag(i + 1 + "tag");
            textView.setGravity(Gravity.CENTER);
            if (itemClickListener == null) {
                itemClickListener = new ItemClickListener();
            }

            textView.setOnClickListener(itemClickListener);


            // textView.setLayoutParams(lp);
            // 设置横向边距,不是最后一列
            if ((i + 1) % mColumn != 0) {
                lp.rightMargin = mMargin;
            }
            // 如果不是第一列
            if (i % mColumn != 0) {
                lp.addRule(RelativeLayout.RIGHT_OF,//
                        mItems[i - 1].getId());
            } else {
                lp.leftMargin = mMargin / 2;
            }
            // 如果不是第一行，//设置纵向边距，非最后一行
            if ((i + 1) > mColumn) {
                lp.topMargin = mMargin - mMarginTop;
                lp.addRule(RelativeLayout.BELOW,//
                        mItems[i - mColumn].getId());
            } else {
                lp.topMargin = mMargin / 2;
            }
            addView(textView, lp);
        }
    }

    private int min(int... params) {
        int minValue = params[0];
        for (int param : params) {
            if (minValue > param) {
                minValue = param;
            }
        }
        return minValue;
    }

}
