package com.curtisgetz.popularmoviesppt1.data;

import android.content.Context;
import android.databinding.BindingBuildInfo;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.curtisgetz.popularmoviesppt1.R;

import java.util.List;

import butterknife.BindView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> implements SpinnerAdapter, AdapterView.OnItemSelectedListener{


    // help from  http://www.zoftino.com/android-spinner-custom-adapter-&-layout

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<String> mMenuStrings;
    private int mSelectedMenuIndex = -1;

    @BindView(R.id.dropdown_selected_iv)
    ImageView mImageView;

    @BindView(R.id.dropdown_sort_type_tv)
    TextView mTextView;

    public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, 0,objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mMenuStrings = objects;

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = null;
        v = super.getDropDownView(position, null, parent);
        if(position == mSelectedMenuIndex){
            //show image and bold text if currently selected.
            mImageView.setVisibility(View.VISIBLE);
            mTextView.setTypeface(null, Typeface.BOLD);
        }else {
            //all other Views.
            mImageView.setVisibility(View.INVISIBLE);
            mTextView.setTypeface(null, Typeface.NORMAL);
        }
        return v;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedMenuIndex = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Nullable
    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
