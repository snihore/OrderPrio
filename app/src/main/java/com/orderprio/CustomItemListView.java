package com.orderprio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orderprio.data.ShopMenuData;

import java.util.List;

public class CustomItemListView extends BaseAdapter {

    private Context context;
    private List<ShopMenuData> list;

    public CustomItemListView(Context context, List<ShopMenuData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View view1 = LayoutInflater.from(this.context).inflate(R.layout.menu_listview_item, viewGroup, false);

        TextView cuisineName = view1.findViewById(R.id.listview_item_cuisine_name);
        TextView cuisinePrice = view1.findViewById(R.id.listview_item_cuisine_price);

        if(list != null){
            ShopMenuData shopMenuData = this.list.get(i);
            if(shopMenuData != null){
                cuisineName.setText(shopMenuData.getCuisineName());
                cuisinePrice.setText("\u20B9 "+shopMenuData.getCuisinePrice());
            }
        }

        return view1;
    }
}
