package com.example.fooddeliveryapplication.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fooddeliveryapplication.Activities.ProductInfoActivity;
import com.example.fooddeliveryapplication.Adapters.StatusManagementPagerAdapter;
import com.example.fooddeliveryapplication.Adapters.StatusOrderRecyclerViewAdapter;
import com.example.fooddeliveryapplication.Helpers.FirebaseStatusOrderHelper;
import com.example.fooddeliveryapplication.Model.Bill;
import com.example.fooddeliveryapplication.R;

import java.util.List;


public class ConfirmStatusDeliveryFragment extends Fragment {

    RecyclerView recConfirmDelivery;
    View view;
    String userId;

    public ConfirmStatusDeliveryFragment(String Id) {
        userId = Id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_confirm_status_delivery,container,false);

        // find view by id
        recConfirmDelivery = (RecyclerView) view.findViewById(R.id.recConfirmDelivery);

        //set data and adapter for list
        new FirebaseStatusOrderHelper().readConfirmBills(userId, new FirebaseStatusOrderHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Bill> bills, List<String> keys) {
                StatusOrderRecyclerViewAdapter adapter = new StatusOrderRecyclerViewAdapter(getContext(),bills,keys);
                recConfirmDelivery.setHasFixedSize(true);
                recConfirmDelivery.setLayoutManager(new LinearLayoutManager(getContext()));
                recConfirmDelivery.setAdapter(adapter);
            }

            @Override
            public void DataIsInserted() {}

            @Override
            public void DataIsUpdated() {}

            @Override
            public void DataIsDeleted() {}
        });


        // return statement
        return view;
    }
}