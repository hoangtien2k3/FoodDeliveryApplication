package com.example.fooddeliveryapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fooddeliveryapplication.Adapters.OrderProductAdapter;
import com.example.fooddeliveryapplication.Models.Address;
import com.example.fooddeliveryapplication.Models.CartInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProceedOrderActivity extends AppCompatActivity {
    private TextView receiverName;
    private TextView detailAddress;
    private TextView receiverPhoneNumber;
    private TextView change;
    private RecyclerView recyclerViewOrderProducts;
    private OrderProductAdapter orderProductAdapter;
    private ArrayList<CartInfo> cartInfoList;
    private Button complete;
    private TextView totalPrice;
    String totalPriceDisplay;
    private ActivityResultLauncher<Intent> changeAddressLauncher;
    private String addressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed_order);

        initToolbar();
        initChangeAddressActivity();

        receiverName = findViewById(R.id.receiver_name);
        detailAddress = findViewById(R.id.detail_address);
        receiverPhoneNumber = findViewById(R.id.receiver_phone_number);
        change = findViewById(R.id.change);
        recyclerViewOrderProducts = findViewById(R.id.recycler_view_order_product);
        recyclerViewOrderProducts.setHasFixedSize(true);
        recyclerViewOrderProducts.setLayoutManager(new LinearLayoutManager(this));
        cartInfoList = (ArrayList<CartInfo>) getIntent().getSerializableExtra("buyProducts");
        orderProductAdapter = new OrderProductAdapter(this, cartInfoList);
        recyclerViewOrderProducts.setAdapter(orderProductAdapter);
        totalPriceDisplay = getIntent().getStringExtra("totalPrice");
        complete = findViewById(R.id.complete);
        totalPrice = findViewById(R.id.total_price);

        loadInfo();

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Init intent
                FirebaseDatabase.getInstance().getReference().child("Address").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent = new Intent(ProceedOrderActivity.this, ChangeAddressActivity.class);
                        if (addressId == null) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Address address = ds.getValue(Address.class);
                                if (address.getState().equals("default")) {
                                    intent.putExtra("choseAddressId", address.getAddressId());
                                }
                            }
                        }
                        else {
                            intent.putExtra("choseAddressId", addressId);
                        }

                        changeAddressLauncher.launch(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void initChangeAddressActivity() {
        // Init launcher
        changeAddressLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    addressId = data.getStringExtra("addressId");
                    //Toast.makeText(this, addressId, Toast.LENGTH_SHORT).show();

                    loadAddressData(addressId);
                }
            }
        });
    }

    private void loadAddressData(String addressId) {
        FirebaseDatabase.getInstance().getReference().child("Address").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(addressId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Address address = snapshot.getValue(Address.class);
                receiverName.setText(address.getReceiverName());
                detailAddress.setText(address.getDetailAddress());
                receiverPhoneNumber.setText(address.getReceiverPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Proceed order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadInfo() {
        // totalPrice
        totalPrice.setText(totalPriceDisplay);

        // Address
        FirebaseDatabase.getInstance().getReference().child("Address").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Address address = ds.getValue(Address.class);

                    if (address.getState().equals("default")) {
                        addressId = address.getAddressId();
                        receiverName.setText(address.getReceiverName());
                        detailAddress.setText(address.getDetailAddress());
                        receiverPhoneNumber.setText(address.getReceiverPhoneNumber());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}