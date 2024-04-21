package com.example.hafalanku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

//public class CustomSpinnerAdapter extends ArrayAdapter<String> {
//
//    private Context mContext;
//    private String[] mItems;
//    private boolean mIsDropdown;
//
//    public CustomSpinnerAdapter(Context context, int resource, String[] items, boolean isDropdown) {
//        super(context, resource, items);
//        mContext = context;
//        mItems = items;
//        mIsDropdown = isDropdown;
//    }
//
//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        return getView(position, convertView, parent, true);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        return getView(position, convertView, parent, false);
//    }
//
//    private View getView(int position, View convertView, ViewGroup parent, boolean isDropdown) {
//        View view = convertView;
//        if (view == null) {
//            LayoutInflater inflater = LayoutInflater.from(mContext);
//            int layoutId = isDropdown ? R.layout.custom_spinner_dropdown_item : R.layout.custom_spinner_item;
//            view = inflater.inflate(layoutId, parent, false);
//        }
//
//        TextView tv = view.findViewById(R.id.text_nama);
//        if (position == 0 && isDropdown) {
//            tv.setText("");
//        } else {
//            tv.setText(mItems[position]);
//        }
//
//        return view;
//    }
//}

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mItems;
    private boolean mIsDropdown;

    public CustomSpinnerAdapter(Context context, int resource, List<String> items, boolean isDropdown) {
        super(context, resource, items);
        mContext = context;
        mItems = items;
        mIsDropdown = isDropdown;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, false);
    }

    private View getView(int position, View convertView, ViewGroup parent, boolean isDropdown) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            int layoutId = isDropdown ? R.layout.custom_spinner_dropdown_item : R.layout.custom_spinner_item;
            view = inflater.inflate(layoutId, parent, false);
        }

        TextView tv = view.findViewById(R.id.text_nama);
        tv.setText(mItems.get(position));

        return view;
    }
}
