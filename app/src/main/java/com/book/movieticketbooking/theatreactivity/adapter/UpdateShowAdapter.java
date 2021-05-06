package com.book.movieticketbooking.theatreactivity.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.book.movieticketbooking.theatreactivity.model.Addshow;
import com.book.movieticketbooking.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UpdateShowAdapter extends FirebaseRecyclerAdapter<Addshow, UpdateShowAdapter.myViewHolder> {

    private DatabaseReference databaseReference;


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UpdateShowAdapter(@NonNull FirebaseRecyclerOptions<Addshow> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(final @NonNull myViewHolder holder, int position, final  @NonNull Addshow model) {
        holder.Movie_Name.setText(model.getMovie_Name());
        holder.Lang.setText(model.getMovie_Lang());
        holder.Director.setText(model.getDirector_Name());
        holder.Cast.setText(model.getMovie_Cast());
        holder.Category.setText(model.getM_category());
        holder.Time.setText(model.getShow_Time());
        holder.Seat.setText(model.getAvailable_Seat());
        holder.Address.setText(model.getM_address());
        holder.Theatre_name.setText(model.getTheatre_Name());
        holder.Theatre_price.setText(model.getPrice());

        String id = model.getMovieId();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Add Show");
        storageReference.child(id)
                .child("Image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().into(holder.imageView);
            }
        });

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                view = inflater.inflate(R.layout.u_show,null);

                TextView textView = (TextView)view.findViewById(R.id.textView);
                View view1 = (View)view.findViewById(R.id.view);

                final EditText editText1 = (EditText) view.findViewById(R.id.up_movie_name);
                final EditText editText2 = (EditText)view.findViewById(R.id.up_lang);
                final EditText editText3 = (EditText)view.findViewById(R.id.up_director);
                final EditText editText4 = (EditText)view.findViewById(R.id.up_cast);
                final EditText editText5 = (EditText)view.findViewById(R.id.up_category);
                final EditText editText6 = (EditText)view.findViewById(R.id.up_timing);
                final EditText editText7 = (EditText)view.findViewById(R.id.up_available);
                final EditText editText8 = (EditText)view.findViewById(R.id.up_address);
                final EditText editText9 = (EditText)view.findViewById(R.id.up_theatre);
                final EditText editText10 = (EditText)view.findViewById(R.id.up_price);

                Button save = (Button)view.findViewById(R.id.up_save);

                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                final String id = model.getMovieId();
                databaseReference = FirebaseDatabase.getInstance().getReference("Add Show").child(id);
                final View finalView = view;
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Addshow addShow = snapshot.getValue(Addshow.class);
                        editText1.setText(addShow.getMovie_Name());
                        editText2.setText(addShow.getMovie_Lang());
                        editText3.setText(addShow.getDirector_Name());
                        editText4.setText(addShow.getMovie_Cast());
                        editText5.setText(addShow.getM_category());
                        editText6.setText(addShow.getShow_Time());
                        editText7.setText(addShow.getAvailable_Seat());
                        editText8.setText(addShow.getM_address());
                        editText9.setText(addShow.getTheatre_Name());
                        editText10.setText(addShow.getPrice());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(finalView.getContext(),error.getCode(), Toast.LENGTH_SHORT).show();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String MovieName = editText1.getText().toString();
                        String Lang = editText2.getText().toString();
                        String Director = editText3.getText().toString();
                        String Cast = editText4.getText().toString();
                        String Category = editText5.getText().toString();
                        String Time = editText6.getText().toString();
                        String Seat = editText7.getText().toString();
                        String Address = editText8.getText().toString();
                        String Theatre = editText9.getText().toString();
                        String Price = editText10.getText().toString();

                         if (TextUtils.isEmpty(MovieName)) {
                            editText1.setError("Enter movie name");
                        }else if (TextUtils.isEmpty(Lang)) {
                            editText2.setError("Enter language");
                        }else if (TextUtils.isEmpty(Director)) {
                            editText3.setError("Enter director name");
                        }else if (TextUtils.isEmpty(Cast)) {
                            editText4.setError("Enter cast name");
                        }else if (TextUtils.isEmpty(Category)) {
                            editText5.setError("Enter category");
                        }else if (TextUtils.isEmpty(Time)) {
                            editText6.setError("Enter show time");
                        }else if (TextUtils.isEmpty(Seat)) {
                            editText7.setError("Enter seat");
                        }else if (TextUtils.isEmpty(Address)) {
                            editText8.setError("Enter address");
                        }else if (TextUtils.isEmpty(Theatre)) {
                            editText9.setError("Enter theatre name");
                        }else if (TextUtils.isEmpty(Price)){
                             editText10.setError("Edit price");
                         }
                         else {
                            databaseReference = FirebaseDatabase.getInstance().getReference("Add Show").child(id);
                            Addshow addShow = new Addshow(id,MovieName,Lang,Director,Cast,Category,Time,Seat,Address,Theatre,Price);
                            databaseReference.setValue(addShow);
                            Toast.makeText(finalView.getContext()," Update show successful ",Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Delete Show");
                    builder.setMessage("Are you sure you want to delete show ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            String id = model.getMovieId();
                            FirebaseDatabase.getInstance().getReference("Add Show").child(id).removeValue();
                            StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("Add Show").child(id).child("Image");
                            storageReference1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(view.getContext()," Delete show successful ",Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }else {
                                        Toast.makeText(view.getContext()," Something is wrong show not deleted ",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
            }
        });
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_listview,parent,false);
        return new UpdateShowAdapter.myViewHolder(view);
    }


    public class myViewHolder extends RecyclerView.ViewHolder{

        TextView Movie_Name,Lang,Director,Cast,Category,Time,Seat,Address,Theatre_name,Theatre_price;
        Button update,delete;
        ImageView imageView;
        public myViewHolder(@NonNull View listViewItem) {
            super(listViewItem);
            Movie_Name = (TextView)listViewItem.findViewById(R.id.moviename);
            Lang = (TextView)listViewItem.findViewById(R.id.language);
            Director = (TextView)listViewItem.findViewById(R.id.director);
            Cast = (TextView)listViewItem.findViewById(R.id.cast);
            Category = (TextView)listViewItem.findViewById(R.id.category);
            Time = (TextView)listViewItem.findViewById(R.id.time);
            Seat = (TextView)listViewItem.findViewById(R.id.seat);
            Address = (TextView)listViewItem.findViewById(R.id.address);
            Theatre_name = (TextView)listViewItem.findViewById(R.id.theatrename);
            Theatre_price = (TextView)listViewItem.findViewById(R.id.movie_price);

            update = (Button)listViewItem.findViewById(R.id.Update);
            delete = (Button)listViewItem.findViewById(R.id.Delete);

            imageView = (ImageView)listViewItem.findViewById(R.id.addShow_imageView);
        }
    }
}
