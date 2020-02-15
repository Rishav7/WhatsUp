package com.example.whatsup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Users extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //private ListView listView;
    private ArrayList<String> Wusers;
   // private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

//        FancyToast.makeText(Users.this, "Logged in Successfully" + ParseUser.getCurrentUser()
//                        .getUsername()
//                , FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();

        final ListView listView =findViewById(R.id.listview);
        Wusers = new ArrayList<>();

       final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,Wusers);
        //listView.setOnItemClickListener(this);

       final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeContainer);





        try {
            ParseQuery<ParseUser> queryName = ParseUser.getQuery();
            queryName.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            queryName.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseUser user : objects) {
                            Wusers.add(user.getUsername());
                        }
                        listView.setAdapter(adapter);


                    }
                }


            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            try {
                ParseQuery<ParseUser> queryName = ParseUser.getQuery();
                queryName.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

                queryName.whereNotContainedIn("username",Wusers);
                queryName.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (objects.size() > 0) {
                            if (e == null) {
                                for (ParseUser user : objects) {

                                    Wusers.add(user.getUsername());
                                }
                                adapter.notifyDataSetChanged();
                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }

                                    else
                                    {
                                        if (swipeRefreshLayout.isRefreshing()) {
                                            swipeRefreshLayout.setRefreshing(false);

                                        }
                                }
                            }
                        }
                    }
                });



            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.logout_item:
                ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback()  {
                    @Override
                    public void done(ParseException e) {
                        Intent intent = new Intent(Users.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                break;




        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(Users.this,Chat.class);
        intent.putExtra("selectedUser",Wusers.get(position));
        startActivity(intent);


    }
}

