package cn.xlmdz.wisdomwaterapp.activity.system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import cn.xlmdz.wisdomwaterapp.R;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText mEtAdmin, mEtPassword;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_login);

        initView();
    }

    private void initView() {
        mEtAdmin = findViewById(R.id.et_admin_username);
        mEtPassword = findViewById(R.id.et_admin_passWord);
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