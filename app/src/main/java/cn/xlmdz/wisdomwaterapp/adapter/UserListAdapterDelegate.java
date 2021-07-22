package cn.xlmdz.wisdomwaterapp.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.xlmdz.wisdomwaterapp.R;
import cn.xlmdz.wisdomwaterapp.room.entity.User;

public class UserListAdapterDelegate extends BaseQuickAdapter<User, BaseViewHolder> {

    public UserListAdapterDelegate(List<User> userList) {
        super(R.layout.item_node, userList);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, User user) {
        baseViewHolder.setText(R.id.tvName, user.getUserName());
    }
}
