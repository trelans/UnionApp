package com.union.unionapp.views;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.union.unionapp.controllers.AdapterChatlist;
import com.union.unionapp.R;
import com.union.unionapp.models.ModelChat;
import com.union.unionapp.models.ModelChatlist;
import com.union.unionapp.models.ModelUsers;

import java.util.ArrayList;
import java.util.List;


/**
 * This fragment shows chatbox of a user and enable them to communicate with others in private chats
 *
 * @author unionTeam
 * @version 04.05.2021
 */
public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<ModelChatlist> chatlistList;
    private List<ModelUsers> usersList;
    private FirebaseUser currentUser;
    private AdapterChatlist adapterChatlist;

    // firebase
    private DatabaseReference reference;


    @Nullable
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_message, container, false );
        recyclerView = view.findViewById( R.id.recyclerView );


        // firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        chatlistList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference( "BilkentUniversity/Chatlist" ).child( currentUser.getUid() );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                chatlistList.clear();
                for ( DataSnapshot ds : snapshot.getChildren() ) {
                    ModelChatlist chatlist = ds.getValue( ModelChatlist.class );
                    chatlistList.add( chatlist );
                }
                loadChats();

            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        } );


        return view;
    }

    /**
     * This method loads user chats
     */
    private void loadChats() {
        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference( "BilkentUniversity/Users" );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                usersList.clear();
                for ( DataSnapshot ds : snapshot.getChildren() ) {
                    ModelUsers user = ds.getValue( ModelUsers.class );
                    for ( ModelChatlist chatlist : chatlistList ) {
                        if ( user.getUid() != null && user.getUid().equals( chatlist.getId() ) ) {
                            usersList.add( user );
                            break;
                        }
                    }
                    // adapter
                    adapterChatlist = new AdapterChatlist( getContext(), usersList );
                    // set adapter
                    recyclerView.setAdapter( adapterChatlist );
                    recyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );
                    // set last message
                    for ( int i = 0; i < usersList.size(); i++ ) {
                        lastMessage( usersList.get( i ).getUid() );
                    }
                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        } );
    }


    /**
     * This method gets the last message from database
     *
     * @param userId user that current user has a chat with him/her
     */
    private void lastMessage( String userId ) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference( "BilkentUniversity/Chats" );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                String theLastMessage = "default";
                for ( DataSnapshot ds : snapshot.getChildren() ) {
                    ModelChat chat = ds.getValue( ModelChat.class );
                    if ( chat == null ) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if ( sender == null || receiver == null ) {
                        continue;
                    }
                    if ( chat.getReceiver().equals( currentUser.getUid() ) &&
                            chat.getSender().equals( userId ) || chat.getReceiver().equals( userId ) &&
                            chat.getSender().equals( currentUser.getUid() ) ) {
                        theLastMessage = chat.getMessage();

                    }
                }
                adapterChatlist.setLastMessageMap( userId, theLastMessage );
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {
            }
        } );
    }


}
