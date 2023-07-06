package com.example.fooddeliveryapplication.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapplication.CustomMessageBox.FailToast;
import com.example.fooddeliveryapplication.CustomMessageBox.SuccessfulToast;
import com.example.fooddeliveryapplication.GlobalConfig;
import com.example.fooddeliveryapplication.Interfaces.IAddressAdapterListener;
import com.example.fooddeliveryapplication.Model.Address;
import com.example.fooddeliveryapplication.Activities.Cart_PlaceOrder.UpdateAddAddressActivity;
import com.example.fooddeliveryapplication.databinding.ItemAddressBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder>{
    private Context mContext;
    private List<Address> mAddresses;
    private RadioButton checkedRadioButton;
    private IAddressAdapterListener addressAdapterListener;
    private String userId;
    static private final int UPDATE_ADDRESS_REQUEST_CODE = 100;

    public AddressAdapter(Context mContext, List<Address> mAddresses, String id) {
        this.mContext = mContext;
        this.mAddresses = mAddresses;
        this.userId = id;
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressAdapter.ViewHolder(ItemAddressBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        Address address = mAddresses.get(position);

        if (address.getAddressId().equals(GlobalConfig.choseAddressId) && address.getState().equals("default")) {
            holder.binding.choose.setChecked(true);
            holder.binding.defaultText.setVisibility(View.VISIBLE);
            checkedRadioButton = holder.binding.choose;
        }
        else if (address.getAddressId().equals(GlobalConfig.choseAddressId)) {
            holder.binding.choose.setChecked(true);
            holder.binding.defaultText.setVisibility(View.INVISIBLE);
            checkedRadioButton = holder.binding.choose;
        }
        else if (address.getState().equals("default")) {
            holder.binding.defaultText.setVisibility(View.VISIBLE);
            holder.binding.choose.setChecked(false);
        }
        else {
            holder.binding.choose.setChecked(false);
            holder.binding.defaultText.setVisibility(View.INVISIBLE);
        }
        holder.binding.receiverName.setText(address.getReceiverName());
        holder.binding.receiverPhoneNumber.setText(address.getReceiverPhoneNumber());
        holder.binding.detailAddress.setText(address.getDetailAddress());

        holder.binding.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.binding.choose.isChecked()) {
                    checkedRadioButton.setChecked(false);

                    if (addressAdapterListener != null) {
                        addressAdapterListener.onCheckedChanged(address);
                    }

                    checkedRadioButton = holder.binding.choose;
                }
            }
        });

        holder.binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalConfig.updateAddressId = address.getAddressId();
                Intent intent = new Intent(mContext, UpdateAddAddressActivity.class);
                intent.putExtra("mode", "update");
                intent.putExtra("userId",userId);
                ((Activity)mContext).startActivityForResult(intent, UPDATE_ADDRESS_REQUEST_CODE);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setMessage("Delete this address?");
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (address.getState().equals("default")) {
                            new FailToast().showToast(mContext, "You cannot delete the default address!");
                            dialogInterface.dismiss();
                        }
                        else {
                            FirebaseDatabase.getInstance().getReference().child("Address").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(address.getAddressId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        new SuccessfulToast().showToast(mContext, "Delete address successfully!");
                                        dialogInterface.dismiss();
                                        if (addressAdapterListener != null) {
                                            addressAdapterListener.onDeleteAddress();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
                alertDialog.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAddresses == null ? 0 : mAddresses.size();
    }

    public void setAddressAdapterListener(IAddressAdapterListener addressAdapterListener) {
        this.addressAdapterListener = addressAdapterListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAddressBinding binding;

        public ViewHolder(@NonNull ItemAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
