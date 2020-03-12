package com.orderprio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orderprio.data.Cart;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private Cart cartData;

    private ListView cartListView;
    private TextView cartTotal;
    private Button proceedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();

        try{
            cartData = (Cart) getIntent().getSerializableExtra("CART_DATA");
            Log.i("CART_NAMES", cartData.getCuisineNames().toString());
            cartListView.setAdapter(new CustomCartListView(getApplicationContext(), cartData));

            List<Integer> list = new ArrayList(cartData.getCuisinePrices().values());
            Log.i("TOTAL::: ", list.toString());

            if(list.size()>0){
                int total = 0;
                for(int i: list){
                    total += i;
                }
                cartTotal.setText("\u20B9 "+String.valueOf(total));
            }else{
                cartTotal.setText("\u20B9 0");
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void initViews() {

        cartListView = (ListView)findViewById(R.id.cart_listview);
        cartTotal = (TextView)findViewById(R.id.cart_total);
        proceedBtn = (Button) findViewById(R.id.proceed_btn);
    }
}
