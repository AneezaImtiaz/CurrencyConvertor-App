package com.example.currencyconvertor.Components;

import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.currencyconvertor.Adapters.CurrencyAdapter;
import com.example.currencyconvertor.Models.CurrencyModel;
import com.example.currencyconvertor.R;
import com.example.currencyconvertor.Services.GetServiceCall;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends GetServiceCall {

    private ArrayList<CurrencyModel> currencyList = new ArrayList<>();
    public static RecyclerView recyclerView;
    private static CurrencyAdapter mAdapter;
    private  Handler handler;
    public static Handler notifyHandler;
    private Boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        notifyHandler = new Handler();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new CurrencyAdapter(currencyList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        callCurrencyApi(true);
        notifyHandler.postDelayed(notifyRunnable, 1000);
        handler.postDelayed(runnable, 1000);

    }

    public static Runnable notifyRunnable = new Runnable() {

        public void run() {
            mAdapter.notifyDataSetChanged();
            notifyHandler.postDelayed(this, 1000);
        }

    };

    public Runnable runnable = new Runnable() {

        public void run() {
            if(!flag) {
                callCurrencyApi(false);
            }
            handler.postDelayed(this, 1000);
        }

    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        notifyHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /********************************
     * Currency API Response
     ********************************/
    @Override
    public void apiCallResponse(JSONObject obj, boolean  loader) {

        try {
                JSONObject data = new JSONObject(obj.getString("rates"));
                CurrencyModel m;
                int i = 0;
                Iterator<String> keys = data.keys();
                while( keys.hasNext() )
                {
                    String key = keys.next();
                    Object innerJObject = data.get(key);

                   if(loader) {
                       m = new CurrencyModel(key, Double.parseDouble(innerJObject.toString()), 0);
                       currencyList.add(i, m);
                    }

                    else{
                        for(int k = 0; k < currencyList.size(); k++)
                        {
                            if(currencyList.get(k).getCurrency() .equalsIgnoreCase(key)){
                                currencyList.get(k).setRate(Double.parseDouble(innerJObject.toString()));
                                break;
                            }
                        }
                    }
                    i++;
                }
                flag = false;
                mAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /********************************
     * Call Currency API
     ********************************/
    private void callCurrencyApi(boolean loader)
    {
        try {
            flag = true;
            String[] params = {"latest?base=EUR"};

            new APICall(loader,MainActivity.this).execute(params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
