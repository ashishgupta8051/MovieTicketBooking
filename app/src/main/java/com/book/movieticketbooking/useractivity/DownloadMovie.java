package com.book.movieticketbooking.useractivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.book.movieticketbooking.theatreactivity.model.Downloadmovies;
import com.book.movieticketbooking.useractivity.adapter.DownloadMoviesAdapter;
import com.book.movieticketbooking.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class DownloadMovie extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DownloadMoviesAdapter download_movies_adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download__movie);
        getSupportActionBar().setTitle("Download Movie");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSplash)));
        getWindow().setStatusBarColor((ContextCompat.getColor(this,R.color.colorSplash)));
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
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

        recyclerView = (RecyclerView)findViewById(R.id.rec_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<Downloadmovies> options = new FirebaseRecyclerOptions.Builder<Downloadmovies>().
                setQuery(FirebaseDatabase.getInstance().getReference("Download Movie"), Downloadmovies.class).build();

        download_movies_adapter = new DownloadMoviesAdapter(options);
        recyclerView.setAdapter(download_movies_adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        download_movies_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        download_movies_adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchview, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search Here !!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ProcessSearch(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                ProcessSearch(query);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void ProcessSearch(String query) {
        FirebaseRecyclerOptions<Downloadmovies> options = new FirebaseRecyclerOptions.Builder<Downloadmovies>().
                setQuery(FirebaseDatabase.getInstance().getReference("Download Movie").orderByChild("movieName").startAt(query)
                        .endAt(query+ "\uf8ff"), Downloadmovies.class).build();

        download_movies_adapter = new DownloadMoviesAdapter(options);
        download_movies_adapter.startListening();
        recyclerView.setAdapter(download_movies_adapter);

    }


}