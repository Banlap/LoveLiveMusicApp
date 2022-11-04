package com.banlap.llmusic.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityMessageBinding;
import com.banlap.llmusic.databinding.ItemMessageListBinding;
import com.banlap.llmusic.model.Message;
import com.banlap.llmusic.uivm.MessageVM;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends BaseActivity<MessageVM, ActivityMessageBinding>
    implements MessageVM.MessageCallBack {

    public List<Message> messageList;
    public MessageListAdapter messageListAdapter;

    @Override
    protected int getLayoutId() { return R.layout.activity_message; }

    @Override
    protected void initData() {
        messageList = new ArrayList<>();
    }

    @Override
    protected void initView() {
        messageListAdapter = new MessageListAdapter(this, messageList);
        getViewDataBinding().rvMessageList.setLayoutManager(new LinearLayoutManager(this));
        getViewDataBinding().rvMessageList.setAdapter(messageListAdapter);
        messageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void viewBack() { finish(); }

    public static class MessageListViewHolder extends RecyclerView.ViewHolder {
        public MessageListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    public class MessageListAdapter extends RecyclerView.Adapter<MessageListViewHolder> {

        private Context context;
        private List<Message> list;

        public MessageListAdapter(Context context, List<Message> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMessageListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.item_message_list, parent,false);
            return new MessageListViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull MessageListViewHolder holder, int position) {
            ItemMessageListBinding binding = DataBindingUtil.getBinding(holder.itemView);
            if(null != binding) {
                binding.tvMessageTitle.setText(list.get(position).getMessageTitle());
                binding.tvMessageContent.setText(list.get(position).getMessageContent());
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
