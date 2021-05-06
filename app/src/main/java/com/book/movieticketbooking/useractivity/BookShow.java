
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

import com.book.movieticketbooking.theatreactivity.model.Addshow;
import com.book.movieticketbooking.useractivity.adapter.BookShowAdapter;
import com.book.movieticketbooking.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class BookShow extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookShowAdapter book_show_adapter;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book__show);

        getSupportActionBar().setTitle("Book Show");
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

        recyclerView = (RecyclerView)findViewById(R.id.recview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<Addshow> options = new FirebaseRecyclerOptions.Builder<Addshow>().
                setQuery(FirebaseDatabase.getInstance().getReference("Add Show"), Addshow.class).build();

        book_show_adapter = new BookShowAdapter(options);
        recyclerView.setAdapter(book_show_adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        book_show_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        book_show_adapter.stopListening();
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
        FirebaseRecyclerOptions<Addshow> options =
                new FirebaseRecyclerOptions.Builder<Addshow>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Add Show").orderByChild("movie_Name").startAt(query).endAt(query+"\uf8ff"), Addshow.class)
                        .build();

        book_show_adapter = new BookShowAdapter(options);
        book_show_adapter.startListening();
        recyclerView.setAdapter(book_show_adapter);
    }



    /*
     TODO: 06-09-2020
           Book_Show_List adapter = new Book_Show_List(Book_Show.this,addShows);
           listView.setAdapter(adapter);
           adapter.getFilter().filter(newText);
    */
}