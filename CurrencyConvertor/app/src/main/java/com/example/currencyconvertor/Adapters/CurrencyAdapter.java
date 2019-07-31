package com.example.currencyconvertor.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.example.currencyconvertor.Components.MainActivity;
import com.example.currencyconvertor.Models.CurrencyModel;
import com.example.currencyconvertor.R;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>{

    private List<CurrencyModel> currencyList;
    Context c;

    public CurrencyAdapter(List<CurrencyModel> currencyList, Context c) {
        this.currencyList = currencyList;
        this.c  = c;
    }

    @Override
    public CurrencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_list_row, parent, false);

        return new CurrencyViewHolder(itemView);
    }

    /********************************
     * Swap Currency
     ********************************/
    private void swapCurrency(CurrencyModel first, CurrencyModel second, int position){
        currencyList.set(0, first);
        currencyList.set(position, second);
    }

    /********************************
     * Calculate amount for all Currency
     ********************************************/
    public void calculateCurrency(String val){
        long k = 0;
        for (int i = 1; i < currencyList.size(); i++) {
            if(!val.equals("0")) {
                k = Math.round((currencyList.get(i).getRate()) * Double.parseDouble(val) * 100);
            }
            else{
                k = 0;
            }
            CurrencyModel m = new CurrencyModel(currencyList.get(i).getCurrency(), currencyList.get(i).getRate(), k);
            currencyList.get(i).setAmount(k);
            k = 0;
        }
    }

    @Override
    public void onBindViewHolder(final CurrencyViewHolder holder, final int position) {

        holder.currency.setText(currencyList.get(position).getCurrency());
        holder.amount.setText(String.valueOf(currencyList.get(position).getAmount()));

        /********************************
         * Method call upon Amount Change of first Item
         ********************************************/
        final TextWatcher watcher =  new TextWatcher() {

            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().equalsIgnoreCase("")){
                    currencyList.get(0).setAmount(0);
                }
                else {
                    currencyList.get(0).setAmount(Long.parseLong(s.toString()));
                    calculateCurrency(s.toString());
                }
            }
        };

        MainActivity.recyclerView.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(c);
                int pastVisibleItems = ((LinearLayoutManager) mLayoutManager).findFirstCompletelyVisibleItemPosition();

                if (pastVisibleItems  == 0) {
                    holder.amount.addTextChangedListener(watcher);
                }
                else{
                    holder.amount.removeTextChangedListener(watcher);
                }
            }
        });

        if(position == 0 ){
            holder.amount.setFocusable(true);
            holder.amount.setFocusableInTouchMode(true);
            holder.amount.setClickable(true);
            int pos = holder.amount.length();
            Selection.setSelection( holder.amount.getText(), pos);
            holder.amount.addTextChangedListener(watcher);
        }
        else{
            holder.amount.removeTextChangedListener(watcher);
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String currency = currencyList.get(0).getCurrency();
                double rate =  currencyList.get(0).getRate();
                CurrencyModel c =  new CurrencyModel(holder.currency.getText().toString().trim(), currencyList.get(position).getRate(), Long.parseLong(holder.amount.getText().toString().trim()));
                CurrencyModel c1 =  new CurrencyModel(currency,rate, currencyList.get(0).getAmount());
                swapCurrency(c, c1, position);
                if(currencyList.get(0).getAmount() != 0){
                    calculateCurrency(String.valueOf(currencyList.get(0).getAmount()));
                }
                holder.amount.removeTextChangedListener(watcher);
            }
        });

    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }


    public class CurrencyViewHolder extends RecyclerView.ViewHolder {

        public TextView currency;
        public EditText amount;
        public CardView cardView;

        public CurrencyViewHolder(View view) {
            super(view);
            currency = (TextView) view.findViewById(R.id.currency);
            amount = (EditText) view.findViewById(R.id.amount);
            cardView = (CardView) view.findViewById(R.id.card);
        }
    }
}

