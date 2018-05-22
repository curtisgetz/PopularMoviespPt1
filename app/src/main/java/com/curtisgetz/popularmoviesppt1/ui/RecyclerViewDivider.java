package com.curtisgetz.popularmoviesppt1.ui;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewDivider extends RecyclerView.ItemDecoration {


    /* Used bignerdrang.com and StackOverflow
    * https://www.bignerdranch.com/blog/
    * a-view-divided-adding-dividers-to-your-recyclerview-with-itemdecoration/
    *
    * https://stackoverflow.com/questions/28531996/
    * android-recyclerview-gridlayoutmanager-column-spacing
    */

    private int mSpace;

    public RecyclerViewDivider(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;

        if(parent.getChildLayoutPosition(view) == 0){
            outRect.top = mSpace;
        }else{
            outRect.top = 0;
        }


    }


}
