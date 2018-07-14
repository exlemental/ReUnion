package com.safaorhan.reunion.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.model.Conversation;
import com.safaorhan.reunion.model.Message;
import com.safaorhan.reunion.model.User;

public class MessagingAdapter extends FirestoreRecyclerAdapter<Message, MessagingAdapter.MessageHolder> {
    private static final String TAG = ConversationAdapter.class.getSimpleName();
    MessageClickListener messageClickListener;


    public MessagingAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    public static MessagingAdapter get() {
        Query query = FirebaseFirestore.getInstance()
                .collection("messages")
                .orderBy("timestamp")
                .limit(50);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        return new MessagingAdapter(options);
    }

    public MessageClickListener getMessageClickListener() {
        if (messageCLickListener == null) {
            messageClickListener = new MessageClickListener() {
                @Override
                public void onMessageClick(DocumentReference documentReference) {
                    Log.e(TAG, "You need to call setConversationClickListener() to set the click listener of ConversationAdapter");
                }
            };
        }

        return messageClickListener;
    }

    public void setMessageClickListener(MessageClickListener messageClickListener) {
        this.messageClickListener = messageClickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull Message message) {
        message.setId(getSnapshots().getSnapshot(position).getId());
        holder.bind(message);
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageHolder(itemView);
    }

    public interface MessageClickListener {
        void onMessageClick(DocumentReference messageRef);
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView opponentNameText;
        TextView lastMessageText;

        public MessageHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            opponentNameText = itemView.findViewById(R.id.opponentNameText);
            lastMessageText = itemView.findViewById(R.id.lastMessageText);
        }

        public void bind(final Message message) {

            itemView.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getMessageListener().onMessageClick(FirestoreHelper.getConversationRef(conversation));
                }
            });

            message.getOpponent().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User opponent = documentSnapshot.toObject(User.class);
                    opponentNameText.setText(opponent.getName());
                    itemView.setVisibility(View.VISIBLE);
                }
            });


            if (message.getLastMessage() != null) {
                message.getLastMessage().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Message lastMessage = documentSnapshot.toObject(Message.class);
                        lastMessageText.setText(lastMessage.getText());
                    }
                });
            } else {
                lastMessageText.setText("Write something to start a conversation!");
            }

        }
    }
}
