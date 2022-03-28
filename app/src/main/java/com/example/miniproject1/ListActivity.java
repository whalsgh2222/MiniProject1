package com.example.miniproject1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListViewAdapter adapter = new ListViewAdapter();
        
        //돌아가기버튼
        Button returnBtn = (Button) findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ListView trailListView = findViewById(R.id.trailListView);
        trailListView.setAdapter(adapter);

    }

    /* 리스트뷰 어댑터 */
    public  class ListViewAdapter extends BaseAdapter {
        ArrayList<Trail> items = new ArrayList<Trail>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Trail item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final Trail trail = items.get(position);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.activity_list_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView trailName = (TextView) convertView.findViewById(R.id.trailName);
            TextView trailInfo = (TextView) convertView.findViewById(R.id.trailInfo);

            trailName.setText(trail.getName());
            trailInfo.setText(trail.getName());
            //Log.d(TAG, "getView() - [ "+position+" ] "+bearItem.getName());

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            return convertView;  //뷰 객체 반환
        }
    }

}
