package com.example.greenapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;

public  class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public class FirebaseHelper {

        DatabaseReference db;
        Boolean saved;
        ArrayList<Content> contents = new ArrayList<>();
        ListView mListView;
        Context c;
        Adapter adapter;

        /*
       let's receive a reference to our FirebaseDatabase
       */
        public FirebaseHelper(DatabaseReference db, Context context, ListView mListView) {
            this.db = db;
            this.c = context;
            this.mListView = mListView;
            this.retrieve();
        }

        /*
        let's now write how to save a single Teacher to FirebaseDatabase
         */
        public Boolean save(Content content) {
            //check if they have passed us a valid content. If so then return false.
            if (content == null) {
                saved = false;
            } else {
                //otherwise try to push data to firebase database.
                try {
                    //push data to FirebaseDatabase. Table or Child called Teacher will be created.
                    db.child("Item").push().setValue(content);
                    saved = true;

                } catch (DatabaseException e) {
                    e.printStackTrace();
                    saved = false;
                }
            }
            //tell them of status of save.
            return saved;
        }

        /*
        Retrieve and Return them clean data in an arraylist so that they just bind it to ListView.
         */
        public ArrayList<Content> retrieve() {
            db.child("Item").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contents.clear();
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            //Now get Teacher Objects and populate our arraylist.
                            Content content = ds.getValue(Content.class);
                            contents.add(content);
                        }
                        adapter = new CustomAdapter(c, contents);
                        mListView.setAdapter((ListAdapter) adapter);

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                mListView.smoothScrollToPosition(contents.size());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("mTAG", databaseError.getMessage());
                    Toast.makeText(c, "ERROR " + databaseError.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

            return contents;
        }

    }

    /**********************************CUSTOM ADAPTER START************************/
    class CustomAdapter extends BaseAdapter {
        Context c;
        ArrayList<Content> contents;
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReferenceFromUrl("gs://greenapplication-4f493.appspot.com").child("fruit.jpg");

        public CustomAdapter(Context c, ArrayList<Content> contents) {
            this.c = c;
            this.contents = contents;
        }

        @Override
        public int getCount() {
            return contents.size();
        }

        @Override
        public Object getItem(int position) {
            return contents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(c).inflate(R.layout.model, parent, false);
            }

            TextView nameTextView = convertView.findViewById(R.id.nameTextView);
            TextView categoryTextView = convertView.findViewById(R.id.categoryTextView);
            TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);

            final Content s = (Content) this.getItem(position);

            nameTextView.setText(s.getName());
            categoryTextView.setText(s.getPropellant());
            descriptionTextView.setText(s.getDescription());

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(c, s.getName(), Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }
    }

    DatabaseReference db;
    FirebaseHelper helper;
    CustomAdapter adapter;
    ListView mListView;
    EditText name, costQt, qty;
    TextView tCost;
    private String currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.listContent);
        //initialize firebase database
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelper(db, this, mListView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getApplicationContext());
                dialog.setContentView(R.layout.inputitem);
               // mListView.smoothScrollToPosition(0);
                //displayInputDialog();
                //displayNewInputDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Toast.makeText(getApplicationContext(),currentuser,Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = getSharedPreferences("CurrentUserId", MODE_PRIVATE).edit();
        editor.putString("userId", currentuser);
        editor.apply();
    }

   /* private void displayInputDialog() {
        //create input dialog
        Dialog d = new Dialog(this);
        d.setTitle("Save To Firebase");
        d.setContentView(R.layout.input);

        //find widgets
        nameEditTxt = d.findViewById(R.id.nameEditText);
        quoteEditText = d.findViewById(R.id.quoteEditText);
        descriptionEditText = d.findViewById(R.id.descEditText);
        Button saveBtn = d.findViewById(R.id.saveBtn);

        //save button clicked
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get data from edittexts
                String name = nameEditTxt.getText().toString();
                String quote = quoteEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                //set data to POJO
                Content s = new Content();
                s.setName(name);
                s.setPropellant(quote);
                s.setDescription(description);

                //perform simple validation
                if (name != null && name.length() > 0) {
                    //save data to firebase
                    if (helper.save(s)) {
                        //clear edittexts
                        nameEditTxt.setText("");
                        quoteEditText.setText("");
                        descriptionEditText.setText("");

                        //refresh listview
                        ArrayList<Content> fetchedData = helper.retrieve();
                        adapter = new CustomAdapter(HomeActivity.this, fetchedData);
                        mListView.setAdapter(adapter);
                        mListView.smoothScrollToPosition(fetchedData.size());
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Name Must Not Be Empty Please", Toast.LENGTH_SHORT).show();
                }
            }
        });

        d.show();
    }
*/

    private void displayNewInputDialog() {
        //create input dialog
        Dialog d = new Dialog(this);
        d.setTitle("Save To Firebase");
        d.setContentView(R.layout.inputitem);

        //find widgets
        name = d.findViewById(R.id.itemName);
        qty = d.findViewById(R.id.quantity);
        costQt = d.findViewById(R.id.cost);
        tCost = d.findViewById(R.id.totalCost);
        Button saveBtn = d.findViewById(R.id.saveBtn);

        //get data from edittexts
//        final String itemName = name.getText().toString();
//        float quantity = Float.valueOf(qty.getText().toString());
//        float cost = Float.valueOf(costQt.getText().toString());

//        if ( itemName!= null && quantity > 0.0 && cost > 0.0 ){
//            String totalCost = String.valueOf((quantity*cost));
//            tCost.setText(totalCost);
//        }

        //save button clicked
        /*saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //set data to POJO
                Content s = new Content();
                s.setName(itemName);
                s.setPropellant(quote);
                s.setDescription(description);

                //perform simple validation
                if (name != null && name.length() > 0) {
                    //save data to firebase
                    if (helper.save(s)) {
                        //clear edittexts
                        nameEditTxt.setText("");
                        quoteEditText.setText("");
                        descriptionEditText.setText("");

                        //refresh listview
                        ArrayList<Content> fetchedData = helper.retrieve();
                        adapter = new CustomAdapter(HomeActivity.this, fetchedData);
                        mListView.setAdapter(adapter);
                        mListView.smoothScrollToPosition(fetchedData.size());
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Name Must Not Be Empty Please", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        d.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences settings = getApplicationContext().getSharedPreferences("CurrentUserId", Context.MODE_PRIVATE);
            settings.edit().remove("userId").commit();
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation vi ew item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
