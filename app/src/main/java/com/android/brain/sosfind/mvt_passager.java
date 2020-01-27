package com.android.brain.sosfind;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.brain.sosfind.Models.Cmvt;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class mvt_passager extends AppCompatActivity {

    DatabaseReference base;
    FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private RecyclerView mUsersList;
    DatabaseReference bases;
    FirebaseUser user;

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvt_passager);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //base = FirebaseDatabase.getInstance().getReference().child("mvtClient").child(user.getUid()).child("SpGeerQjKWR2raqmvSFoLlqHZuo2");
        base = FirebaseDatabase.getInstance().getReference().child("mvtClient").child(user.getUid());

        mtoolbar = (Toolbar) findViewById(R.id.bar_mvt_passager);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Mes commandes effectuées");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersList = (RecyclerView) findViewById(R.id.usersList);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        base.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot s : dataSnapshot.getChildren()) {

                    final String key = s.getKey();
                    bases = FirebaseDatabase.getInstance().getReference().child("mvtClient").child(user.getUid()).child(key);
                    bases.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            FirebaseRecyclerAdapter<Cmvt, UsersViewHolder>
                                    firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Cmvt, UsersViewHolder>(
                                    Cmvt.class, // elle comporte des accesseurs(variable) à afficher dans le recycleview
                                    R.layout.single_line_list, //le Layout qui comporte les views(2 TextViews et circleImgae) qui doivent afficher les informations
                                    UsersViewHolder.class, //la classe qui arrange la liste dans le recicleView
                                    bases //Reference de la base des données firebase
                            ) {
                                @Override
                                protected void populateViewHolder(UsersViewHolder viewHolder, Cmvt model, int position) {
                                    viewHolder.setDisplayDate(model.getDatecmd());
                                    viewHolder.setDisplayCourse(model.getDepart() + "-" + model.getDestination());
                                    final String chauffeurId = getRef(position).getParent().getKey();
                                    final String cmdId = getRef(position).getKey();
                                    final String datecd = model.getDatecmd();
                                    final String departs = model.getDepart();
                                    final String destinat = model.getDestination();
                                    final String details = model.getDetails();
                                    final String mont = model.getMontant();

                                    viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final StringBuffer buffer = new StringBuffer();
                                            DatabaseReference getChauffeur = FirebaseDatabase.getInstance()
                                                    .getReference().child("Chauffeurs").child(chauffeurId);
                                            getChauffeur.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    buffer.append("Chauffeur:" + dataSnapshot.child("noms").getValue().toString() + "\n");
                                                    buffer.append("Vehicule:" + dataSnapshot.child("vehicule").getValue().toString() + "\n");
                                                    buffer.append("Date:" + datecd + "\n");
                                                    buffer.append("Point de depart:" + departs + "\n");
                                                    buffer.append("Destination:" + destinat + "\n");
                                                    buffer.append("Details:" + details + "\n");
                                                    buffer.append("Montant payé:" + mont + "\n");
                                                    buffer.append("Id Commande:" + cmdId + "\n\n");
                                                    showMessage("Detail de la commande", buffer.toString());
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    });
                                }
                            };
                            mUsersList.setAdapter(firebaseRecyclerAdapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mview;
        private String mItem;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        //des procedure pour charger de views
        public void setDisplayDate(String date) {
            TextView txtName = (TextView) mview.findViewById(R.id.mvt_date);
            txtName.setText(date);
        }

        public void setDisplayCourse(String course) {
            TextView txtStatus = (TextView) mview.findViewById(R.id.mvt_course);
            txtStatus.setText(course);
        }

    }
}
