package com.example.whatsup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chat extends AppCompatActivity implements View.OnClickListener {
    private ListView chatList;
    private ArrayList<String> ArrayList;
    private ArrayAdapter adapter;
    private String selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        selectedUser = getIntent().getStringExtra("selectedUser");
        FancyToast.makeText(this,"chat with" + selectedUser + "Now !!",
                Toast.LENGTH_SHORT,FancyToast.INFO,true).show();

        findViewById(R.id.btnSend).setOnClickListener(this);

        chatList =   findViewById(R.id.chatList);
       ArrayList = new ArrayList();
       adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,ArrayList);
       chatList.setAdapter(adapter);


       try {

           ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
           ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

           firstUserChatQuery.whereEqualTo("Sender", ParseUser.getCurrentUser().getUsername());
           firstUserChatQuery.whereEqualTo("Receiver", selectedUser);

           secondUserChatQuery.whereEqualTo("Sender", selectedUser);
           secondUserChatQuery.whereEqualTo("Receiver", ParseUser.getCurrentUser().getUsername());

           ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
           allQueries.add(firstUserChatQuery);
           allQueries.add(secondUserChatQuery);

           ParseQuery<ParseObject>myQuery = ParseQuery.or(allQueries);
           myQuery.orderByAscending("createdAt");

           myQuery.findInBackground(new FindCallback<ParseObject>() {
               @Override
               public void done(List<ParseObject> objects, ParseException e) {
                   if(objects.size() > 0 && e==null){
                       for (ParseObject chatObject : objects) {

                           String Message = chatObject.get("Message").toString();
                           if (chatObject.get("Sender").equals(ParseUser.getCurrentUser().getUsername())){

                           Message = ParseUser.getCurrentUser().getUsername() + ": " + Message;
                       }
                       if (chatObject.get("Sender").equals(selectedUser)){
                           Message = selectedUser + ": " + Message;
                       }
                        ArrayList.add(Message);

                       }
                       adapter.notifyDataSetChanged();
                   }
               }
           });

       }

       catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    public void onClick(View v) {

        final EditText editText = findViewById(R.id.editText);
        ParseObject chat = new ParseObject("Chat");
        chat.put("Sender", ParseUser.getCurrentUser().getUsername());
        chat.put("Receiver",selectedUser);
        chat.put("Message",editText.getText().toString());

        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    FancyToast.makeText(Chat.this,"Message from "
                            + ParseUser.getCurrentUser().getUsername() + "Sent to " +selectedUser
                    ,Toast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();

                    ArrayList.add(ParseUser.getCurrentUser().getUsername() + ": " + editText.getText().toString());
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                }
            }
        });

    }
}
