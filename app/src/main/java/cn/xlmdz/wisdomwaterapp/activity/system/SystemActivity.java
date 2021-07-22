package cn.xlmdz.wisdomwaterapp.activity.system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.xlmdz.wisdomwaterapp.R;
import cn.xlmdz.wisdomwaterapp.adapter.UserListAdapterDelegate;
import cn.xlmdz.wisdomwaterapp.room.entity.User;
import cn.xlmdz.wisdomwaterapp.room.manager.UserManager;
import cn.xlmdz.wisdomwaterapp.utils.DetectService;
import cn.xlmdz.wisdomwaterapp.utils.VerticalSpacingItemDecoration;

public class SystemActivity extends AppCompatActivity {
    private Button mBtnAddUser;
    private RecyclerView mRecyclerView;
    private UserListAdapterDelegate mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);

        initRecyclerView();
        initView();
    }

    private void initView() {
        mBtnAddUser = findViewById(R.id.btnAddUser);
        mBtnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SystemActivity.this);
                builder.setTitle("创建用户");
                View view = LayoutInflater.from(SystemActivity.this).inflate(R.layout.dialog_add_user, null);
                EditText userName = view.findViewById(R.id.etUserName);
                EditText etName = view.findViewById(R.id.etName);
                EditText secret = view.findViewById(R.id.etSecret);
                EditText confirmSecret = view.findViewById(R.id.etConfirmSecret);
                builder.setView(view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = userName.getText().toString().trim();
                        String name = etName.getText().toString().trim();
                        String password = secret.getText().toString().trim();
                        String secret = confirmSecret.getText().toString().trim();

                        if (TextUtils.isEmpty(username)) {
                            Toast.makeText(SystemActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(SystemActivity.this, "请输入用户姓名", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(password)) {
                            Toast.makeText(SystemActivity.this, "请输入密码用户姓名", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (username.length() < 3) {
                            Toast.makeText(SystemActivity.this, "用户名太短", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!password.equals(secret)) {
                            Toast.makeText(SystemActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        User user = UserManager.getUser(SystemActivity.this, username);
                        if (user != null) {
                            Toast.makeText(SystemActivity.this, "该用户名已被注册", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(SystemActivity.this, InputVoiceprintActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("name", name);
                        intent.putExtra("password", password);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllUserForDatabase();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.rvUserList);

        //layoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //设置item间隔
        VerticalSpacingItemDecoration itemDecoration = new VerticalSpacingItemDecoration(20);
        mRecyclerView.addItemDecoration(itemDecoration);

        //设置回收复用池大小--如果一屏内相同类型的 View 个数比较多，需要设置一个合适的大小，防止来回滚动时重新创建 View
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        mRecyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 15);

        mAdapter = new UserListAdapterDelegate(new ArrayList<User>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.item_node_empty);

        // 先注册需要点击的子控件id（注意，请不要写在convert方法里）
        mAdapter.addChildClickViewIds(R.id.btnDelete);
        // 设置子控件点击监听
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.btnDelete) {
                    int ret = DetectService.deleteUser(mAdapter.getData().get(position).getUserName(), "td");
                    if (ret == 0) {
                        UserManager.deleteUser(SystemActivity.this, mAdapter.getData().get(position));
                        getAllUserForDatabase();
                    }
                }
            }
        });

        getAllUserForDatabase();
    }

    private void getAllUserForDatabase() {
        List<User> userList = UserManager.getAllUsers(this);
        setDataToRecycleView(userList);
    }

    private void setDataToRecycleView(List<User> userList) {
        // 设置新的数据方法
        mAdapter.setNewInstance(userList);
    }
}