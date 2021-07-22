package cn.xlmdz.wisdomwaterapp.activity.system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.xlmdz.wisdomwaterapp.R;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText mEtAdmin, mEtPassword;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        initView();
    }

    private void initView() {
        mEtAdmin = findViewById(R.id.etAdmin);
        mEtPassword = findViewById(R.id.etPassword);
        mBtnLogin = findViewById(R.id.btnLogin);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminLoginActivity.this, SystemActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}