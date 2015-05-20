package com.comp4521.bookscan.MainLayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookscan.R;


public class BookDetails extends ActionBarActivity {

    private ImageView bookcover;
    private TextView bookname;
    private TextView bookAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        bookcover = (ImageView) findViewById(R.id.bookcover);
        bookname = (TextView) findViewById(R.id.bookname);
        bookAuthor = (TextView) findViewById(R.id.bookAuthor);

        Intent intent = getIntent();
        bookname.setText("Book name: " +intent.getStringExtra("Name"));
        bookAuthor.setText("Author: " + intent.getStringExtra("Author"));

        int coverid= intent.getIntExtra("Cover",0);
        bookcover.setImageResource(coverid);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
