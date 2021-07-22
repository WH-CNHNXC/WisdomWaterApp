package cn.xlmdz.wisdomwaterapp.room.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "users", primaryKeys = {"userName"})
public class User {
    @NonNull
    private String userName;
    private String name;
    private String password;
    private String createTime;

    public User(String userName, String name, String password) {
        this.userName = userName;
        this.name = name;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
