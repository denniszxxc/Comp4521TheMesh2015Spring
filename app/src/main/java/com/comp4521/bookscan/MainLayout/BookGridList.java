package com.comp4521.bookscan.MainLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.bookscan.R;


public class BookGridList extends ActionBarActivity {

    private GridView gridView;
    private Context mCtx;
    private int[] image = {
            R.drawable.cover01, R.drawable.cover02, R.drawable.cover03,
            R.drawable.cover04, R.drawable.cover05, R.drawable.cover06,
            R.drawable.cover07, R.drawable.cover08, R.drawable.cover09
    };
    private String[] imgText = {
            "別生氣，這就是人性！：你非懂不可的54個心理學法則",
            "今天學心理學了沒？",
            "英語點心",
            "生命中的美好缺憾",
            "小錢買股101招就賺夠",
            "健康，不是數字說了算",
            "自由幻夢（飢餓遊戲3)",
            "第一次做 MOCHI",
            "搭訕聖經"
    };

     private String[] imgAuthor = {
        "陳鈺","林肇賢", "曾淑貞,邱健恩", "約翰・葛林",
             "高俊權", "陳俊旭", "蘇珊·柯林斯", "方芍堯","鄭匡宇"

     };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCtx = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_grid_list);


        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < image.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("image", image[i]);
            item.put("text", imgText[i]);
            items.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,
                items, R.layout.grid_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});

        gridView = (GridView)findViewById(R.id.main_page_gridview);
        gridView.setNumColumns(3);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "Your choice is " + imgText[position],
//                        Toast.LENGTH_SHORT).show();
//                // switching to book detail

                Intent intent;
                intent = new Intent(mCtx , BookDetails.class);
                intent.putExtra("Name", imgText[position]);
                intent.putExtra("Cover", image[position]);
                intent.putExtra("Author", imgAuthor[position]);
                startActivity(intent);
            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_grid_list, menu);
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
            Log.i("BookGridList", "Swithc to fragment main");
            Intent intent;
            intent = new Intent(mCtx , MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
