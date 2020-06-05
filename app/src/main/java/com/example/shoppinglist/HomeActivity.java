package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppinglist.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab_btn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    private TextView totalResult;

    private String type;
    private  int amount;
    private String note;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        toolbar = findViewById(R.id.home_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("MY SUPER SHOPPING LIST");

        totalResult = findViewById(R.id.total_amount);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);
        mDatabase.keepSynced(true);

        recyclerView = findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

//        Calculate the total amount of the all items
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalAmmount = 0;

                for (DataSnapshot snap:dataSnapshot.getChildren()){

                    Data data = snap.getValue(Data.class);

                    totalAmmount += data.getPrice();

                    String totalPrice = String.valueOf(totalAmmount + ".00");

                    totalResult.setText(totalPrice);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab_btn = findViewById(R.id.fab);

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              customDialog();
            }
        });

    }

        private void customDialog(){

            AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

            View myview = inflater.inflate(R.layout.input_data, null);
            final AlertDialog dialog = mydialog.create();

            dialog.setView(myview);

            final EditText type = myview.findViewById(R.id.item_type);
            final EditText price = myview.findViewById(R.id.item_amount);
            final EditText note = myview.findViewById(R.id.item_note);
            Button buttonSave = myview.findViewById(R.id.button_save);

            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String myType = type.getText().toString().trim();
                    String myPrice = price.getText().toString().trim();
                    String myNote = note.getText().toString().trim();

                    int intPrice = Integer.parseInt(myPrice);

                    if(TextUtils.isEmpty(myType)){
                        type.setError("Hey, cannot leave it blank");
                        return;
                    }
                    if(TextUtils.isEmpty(myPrice)){
                        price.setError("Hey, cannot leave it blank");
                        return;
                    }
                    if(TextUtils.isEmpty(myNote)){
                        note.setError("Hey, cannot leave it blank");
                        return;
                    }

                    String id = mDatabase.push().getKey();

                    String date = DateFormat.getDateInstance().format(new Date());
                    Data data = new Data(myType,intPrice,myNote,date,id);

                    mDatabase.child(id).setValue(data);

                    Toast.makeText(getApplicationContext(),"Item added", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                }
            });

            dialog.show();
        }

        protected  void onStart(){
            super.onStart();

            FirebaseRecyclerAdapter<Data,MyViewHolder>adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>
                    (

                            Data.class,
                            R.layout.item,
                            MyViewHolder.class,
                            mDatabase

                    ) {
                @Override
                protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {
                    viewHolder.setDate(model.getDate());
                    viewHolder.setType(model.getType());
                    viewHolder.setNote(model.getNote());
                    viewHolder.setAmmount(model.getPrice());

                    viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            post_key = getRef(position).getKey();
                            type = model.getType();
                            note = model.getNote();
                            amount = model.getPrice();

                            updateData();
                        }
                    });
                }
            };

            recyclerView.setAdapter(adapter);
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder{
            View myview;


            public MyViewHolder(View itemView){
                super(itemView);
                myview = itemView;
            }

            public void setType(String type){
                TextView mType = myview.findViewById(R.id.type);
                mType.setText(type);
            }

            public void setNote(String note){
                TextView mNote = myview.findViewById(R.id.note);
                mNote.setText(note);
            }

            public void setDate(String date){
                TextView mDate = myview.findViewById(R.id.date);
                mDate.setText(date);
            }

            public void setAmmount(int ammount){
                TextView mAmount = myview.findViewById(R.id.amount);
                String stam = String.valueOf(ammount);
                mAmount.setText(stam);
            }

        }


        public void updateData(){

            AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);

            LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

            View myView = inflater.inflate(R.layout.update_item, null);

            final AlertDialog dialog = mydialog.create();

            dialog.setView((myView));

            final EditText edt_Type = myView.findViewById(R.id.item_type_update);
            final EditText edt_Ammount = myView.findViewById(R.id.item_amount_update);
            final EditText edt_Note = myView.findViewById(R.id.item_note_update);

            edt_Type.setText(type);
            edt_Type.setSelection(type.length());

            edt_Ammount.setText(String.valueOf(amount));
            edt_Ammount.setSelection(String.valueOf(amount).length());

            edt_Note.setText(note);
            edt_Note.setSelection(note.length());

            Button updateButton = myView.findViewById(R.id.button_update);
            Button deleteButton = myView.findViewById(R.id.button_delete);


//            For update button, update it to the firebase database
//            All the type is same with the Data Model
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    type = edt_Type.getText().toString().trim();

                    String myAmmount = String.valueOf(amount);

                    myAmmount = edt_Ammount.getText().toString().trim();

                    note= edt_Note.getText().toString().trim();



                    int intAmount = Integer.parseInt(myAmmount);

                    String date = DateFormat.getDateInstance().format(new Date());

                    Data data = new Data(type, intAmount, note, date, post_key);

                    mDatabase.child(post_key).setValue(data);

                    dialog.dismiss();
                }
            });

//            For delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mDatabase.child(post_key).removeValue();
                    dialog.dismiss();

                }
            });

            dialog.show();

        }





    }


