package cn.xlmdz.wisdomwaterapp.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cn.xlmdz.wisdomwaterapp.room.entity.User;

@Dao
public interface UserDao {
    //这里的...表示可以同时操作多条数据
    @Insert
    void insertUser(User user);

    @Insert
    void insertUsers(User... users);

    @Update
    void updateUser(User user);

    @Update
    void updateUsers(User... users);

    @Delete
    void deleteUser(User user);

    @Delete
    void deleteUsers(User... users);

    @Query("Delete from users")
    void deleteAllUsers();

    @Query("SELECT *  from users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE userName=:userName")
    User getUserForUserName(String userName);
}
