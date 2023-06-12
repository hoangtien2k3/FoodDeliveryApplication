package com.example.fooddeliveryapplication.Activities.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fooddeliveryapplication.databinding.ActivityForgotBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {
    ActivityForgotBinding binding;
    FirebaseAuth mFirebaseAuth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.edtForgot.getText().toString().isEmpty()) {
                    Toast.makeText(ForgotActivity.this, "Hãy nhập email mà bạn muốn reset mật khẩu", Toast.LENGTH_SHORT).show();
                } else {
                    mFirebaseAuth.sendPasswordResetEmail(binding.edtForgot.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotActivity.this,"Reset thành công, hãy kiểm tra email",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ForgotActivity.this,"Kiếm tra lại email",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}