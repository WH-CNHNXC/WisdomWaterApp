package cn.xlmdz.wisdomwaterapp.room.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cn.xlmdz.wisdomwaterapp.room.dao.UserDao;
import cn.xlmdz.wisdomwaterapp.room.entity.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();
}
