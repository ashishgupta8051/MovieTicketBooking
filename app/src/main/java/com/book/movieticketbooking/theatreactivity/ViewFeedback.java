package com.book.movieticketbooking.theatreactivity;

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

import com.book.movieticketbooking.useractivity.model.Userfeedback;
import com.book.movieticketbooking.theatreactivity.adapter.UserFeedbackAdapter;
import com.book.movieticketbooking.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class ViewFeedback extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserFeedbackAdapter user_feedback_adapter;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSplash)));
        getWindow().setStatusBarColor((ContextCompat.getColor(this,R.color.colorSplash)));

        getSupportActionBar().setTitle("View Feedback");
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

        FirebaseRecyclerOptions<Userfeedback> options = new FirebaseRecyclerOptions.Builder<Userfeedback>().
                setQuery(FirebaseDatabase.getInstance().getReference("User Feedback"), Userfeedback.class).build();

        user_feedback_adapter = new UserFeedbackAdapter(options);
        recyclerView.setAdapter(user_feedback_adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user_feedback_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        user_feedback_adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}