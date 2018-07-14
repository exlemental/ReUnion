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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.model.Conversation;
import com.safaorhan.reunion.model.Message;
import com.safaorhan.reunion.model.User;

import java.util.Random;

import io.grpc.internal.SharedResourceHolder;

public class ConversationAdapter extends FirestoreRecyclerAdapter<Conversation, ConversationAdapter.ConversationHolder> {
    private static final String TAG = ConversationAdapter.class.getSimpleName();
    ConversationClickListener conversationClickListener;
    int[] myImageList = new int[]{R.drawable.bg_blue1,
            R.drawable.bg_blue2,
            R.drawable.bg_green0,
            R.drawable.bg_purple,
            R.drawable.bg_red,
            R.drawable.bg_turquoise_blue,
    };


    public ConversationAdapter(@NonNull FirestoreRecyclerOptions<Conversation> options) {
        super(options);
    }

    public ConversationClickListener getConversationClickListener() {
        if (conversationClickListener == null) {
            conversationClickListener = new ConversationClickListener() {
                @Override
                public void onConversationClick(DocumentReference documentReference) {
                    Log.e(TAG, "You need to call setConversationClickListener() to set the click listener of ConversationAdapter");
                }
            };
        }

        return conversationClickListener;
    }

    public void setConversationClickListener(ConversationClickListener conversationClickListener) {
        this.conversationClickListener = conversationClickListener;
    }

    public static ConversationAdapter get() {
        String id = FirestoreHelper.getMe().getId();
        Query query = FirebaseFirestore.getInstance()
                .collection("conversations")
                .whereEqualTo(id, true)
                //.orderBy("timestamp")
                .whereEqualTo(FirestoreHelper.getMe().getId(), true)
                .limit(50);

        FirestoreRecyclerOptions<Conversation> options = new FirestoreRecyclerOptions.Builder<Conversation>()
                .setQuery(query, Conversation.class)
                .build();

        return new ConversationAdapter(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ConversationHolder holder, int position, @NonNull Conversation conversation) {
        conversation.setId(getSnapshots().getSnapshot(position).getId());
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String myId = firebaseAuth.getUid();
        Log.e("myId", myId);
        Log.e("conversation.getId", getSnapshots().getSnapshot(position).getId());
        holder.bind(conversation);
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationHolder(itemView);
    }

    public class ConversationHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView firstChar;
        TextView opponentNameText;
        TextView lastMessageText;

        public ConversationHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            firstChar = itemView.findViewById(R.id.char_item);
            opponentNameText = itemView.findViewById(R.id.opponentNameText);
            lastMessageText = itemView.findViewById(R.id.lastMessageText);
        }

        public void bind(final Conversation conversation) {

            itemView.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getConversationClickListener().onConversationClick(FirestoreHelper.getConversationRef(conversation));
                }
            });

            conversation.getOpponent().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User opponent = documentSnapshot.toObject(User.class);
                    if (opponent != null) {
                        opponentNameText.setText(opponent.getName());
                        firstChar.setText(String.format("%s", opponent.getName().charAt(0)));
                        // Todo: Set this
//                        firstChar.setBackground();
                        itemView.setVisibility(View.VISIBLE);
                    } else {
                        Log.e("documentSnapshot", documentSnapshot.getId());
                        Log.e("conversation.getId(2)", conversation.getId());
                    }
                }
            });


            if (conversation.getLastMessage() != null) {
                conversation.getLastMessage().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

    public interface ConversationClickListener {
        void onConversationClick(DocumentReference conversationRef);
    }

    public int anyBG() {
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(myImageList.length);
        return myImageList[index];
    }

}
