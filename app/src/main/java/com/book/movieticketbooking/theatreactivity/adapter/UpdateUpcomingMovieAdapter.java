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

import com.book.movieticketbooking.theatreactivity.model.Upcomingmovie;
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

public class UpdateUpcomingMovieAdapter extends FirebaseRecyclerAdapter<Upcomingmovie, UpdateUpcomingMovieAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public UpdateUpcomingMovieAdapter(@NonNull FirebaseRecyclerOptions<Upcomingmovie> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final myViewHolder holder, int position, @NonNull final Upcomingmovie model) {
        holder.Moviename.setText(model.getMovie_Name());
        holder.Year.setText(model.getMovie_Year());

        String id = model.getMovieId();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Upcoming Movie Image");
        storageReference.child(id)
                .child("Image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().into(holder.imageView);
            }
        });

        holder.Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                view = inflater.inflate(R.layout.update_upcoming,null);

                TextView textView = (TextView)view.findViewById(R.id.textView);
                View view1 = (View)view.findViewById(R.id.view);

                final EditText editText1 = (EditText) view.findViewById(R.id.upm_Movie_Name);
                final EditText editText5 = (EditText)view.findViewById(R.id.upm_Release);

                Button save = (Button)view.findViewById(R.id.upm_Save);

                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                final String id = model.getMovieId();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Upcoming Movie").child(id);
                final View finalView = view;
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Upcomingmovie upcoming_movie = snapshot.getValue(Upcomingmovie.class);
                        editText1.setText(upcoming_movie.getMovie_Name());
                        editText5.setText(upcoming_movie.getMovie_Year());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(finalView.getContext(),error.getCode(),Toast.LENGTH_SHORT).show();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String MovieName = editText1.getText().toString();
                        String YEar = editText5.getText().toString();

                         if (TextUtils.isEmpty(MovieName)){
                            editText1.setError("Enter movie name");
                        }else if (TextUtils.isEmpty(YEar)){
                            editText5.setError("Enter releasing year");
                        }else {
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Upcoming Movie").child(id);
                            Upcomingmovie upcomingMovie = new Upcomingmovie(id,MovieName,YEar);
                            databaseReference1.setValue(upcomingMovie);
                            Toast.makeText(finalView.getContext(),"Update successful",Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Delete Upcoming Movie");
                    builder.setMessage("Are you sure you want to delete upcoming movie?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            String id = model.getMovieId();
                            FirebaseDatabase.getInstance().getReference("Upcoming Movie").child(id).removeValue();
                            StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("Upcoming Movie Image").child(id).child("Image");
                            storageReference1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(v.getContext(),"Delete successful",Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }else {
                                        Toast.makeText(v.getContext()," Something is wrong upcoming movie not deleted ",Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_upcomingmovie_listview,parent,false);
        return new UpdateUpcomingMovieAdapter.myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView Moviename,Year;
        ImageView imageView;
        Button Delete,Update;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            Moviename = (TextView)itemView.findViewById(R.id.upm_moviename);
            Year = (TextView)itemView.findViewById(R.id.upm_year);

            imageView = (ImageView)itemView.findViewById(R.id.upm_imageView);

            Update = (Button)itemView.findViewById(R.id.upm_Update);
            Delete = (Button)itemView.findViewById(R.id.upm_Delete);
        }
    }
}
