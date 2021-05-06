package com.book.movieticketbooking.useractivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.book.movieticketbooking.login.Login;
import com.book.movieticketbooking.theatreactivity.model.Upcomingmovie;
import com.book.movieticketbooking.useractivity.adapter.UpcomingMovieAdapter;
import com.book.movieticketbooking.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.FirebaseDatabase;

public class UpcomingMovie extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UpcomingMovieAdapter upcoming_movie_adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_movie);

        getSupportActionBar().setTitle("Upcoming Movie");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSplash)));
        getWindow().setStatusBarColor((ContextCompat.getColor(this,R.color.colorSplash)));

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);
        recyclerView = (RecyclerView)findViewById(R.id.recview);
        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<Upcomingmovie> options = new FirebaseRecyclerOptions.Builder<Upcomingmovie>().
                setQuery(FirebaseDatabase.getInstance().getReference("Upcoming Movie"), Upcomingmovie.class).build();

        upcoming_movie_adapter = new UpcomingMovieAdapter(options);
        recyclerView.setAdapter(upcoming_movie_adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        upcoming_movie_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        upcoming_movie_adapter.stopListening();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview2, menu);
        MenuItem item = menu.findItem(R.id.search2);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search Here !!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ProcessSearch(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                ProcessSearch(query);
                return true;
            }
        });
        return true;
    }

    private void ProcessSearch(String query) {
        FirebaseRecyclerOptions<Upcomingmovie> options =
                new FirebaseRecyclerOptions.Builder<Upcomingmovie>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Upcoming Movie").orderByChild("movie_Name").startAt(query).endAt(query+"\uf8ff"), Upcomingmovie.class)
                        .build();

        upcoming_movie_adapter =new UpcomingMovieAdapter(options);
        upcoming_movie_adapter.startListening();
        recyclerView.setAdapter(upcoming_movie_adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_wishlist:{
                startActivity(new Intent(UpcomingMovie.this,WishList.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}