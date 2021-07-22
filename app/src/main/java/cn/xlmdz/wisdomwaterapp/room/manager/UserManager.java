package cn.xlmdz.wisdomwaterapp.room.manager;

import android.content.Context;

import androidx.room.Room;

import java.util.List;

import cn.xlmdz.wisdomwaterapp.room.database.UserDatabase;
import cn.xlmdz.wisdomwaterapp.room.entity.User;

public class UserManager {
    private static UserDatabase mDatabase;

    public static UserDatabase getIntance(Context context) {
        if (mDatabase == null) {
            mDatabase = Room.databaseBuilder(context,
                    UserDatabase.class, "users.db")
                    .allowMainThreadQueries()
                    .build();
        }
        return mDatabase;
    }

    //新增
    public static synchronized void addUser(Context context, User user) {
        getIntance(context).getUserDao().insertUser(user);
    }

    //查询
    public static synchronized User getUser(Context context, String userName) {
        return getIntance(context).getUserDao().getUserForUserName(userName);
    }

    //删除
    public static synchronized void deleteUser(Context context, User user) {
        getIntance(context).getUserDao().deleteUser(user);
    }

    //清空
    public static synchronized void deleteAllUsers(Context context) {
        getIntance(context).getUserDao().deleteAllUsers();
    }

    //修改
    public static synchronized void updateUser(Context context, User user) {
        getIntance(context).getUserDao().updateUser(user);
    }

    //获取用户信息
    public static synchronized List<User> getAllUsers(Context context) {
        return getIntance(context).getUserDao().getAllUsers();
    }
}
