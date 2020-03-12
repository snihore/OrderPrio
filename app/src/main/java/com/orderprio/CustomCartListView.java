package com.orderprio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orderprio.data.Cart;

import java.util.ArrayList;
import java.util.Set;

public class CustomCartListView extends BaseAdapter {

    private Context context;
    private Cart cartData;
    private ArrayList<String> names;

    public CustomCartListView(Context context, Cart cartData) {
        this.context = context;
        this.cartData = cartData;

        names = new ArrayList<>(cartData.getCuisineNames());
    }

    @Override
    public int getCount() {
        return names.size();
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

        View view1 = LayoutInflater.from(this.context).inflate(R.layout.cart_listview_item, viewGroup, false);

        TextView itemName = view1.findViewById(R.id.cart_listview_item_name);
        TextView itemQuantity = view1.findViewById(R.id.cart_listview_item_quantity);
        TextView itemPrice = view1.findViewById(R.id.cart_listview_item_price);

        itemName.setText(names.get(i));
        itemQuantity.setText(""+cartData.getCuisineQuantities().get(names.get(i)));
        itemPrice.setText("\u20B9 "+cartData.getCuisinePrices().get(names.get(i)));

        return view1;
    }
}
