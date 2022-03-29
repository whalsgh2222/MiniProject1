package com.example.miniproject1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ListView trailListView;
    DBHelper dbHelper;
    SQLiteDatabase database;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbHelper = new DBHelper(this, 1);
        database = dbHelper.getWritableDatabase();
        trailListView = (ListView) findViewById(R.id.trailListView);

        //돌아가기버튼
        Button returnBtn = (Button) findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //Clear버튼
        Button clearBtn = (Button) findViewById(R.id.clearButton);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.onUpgrade(database,1,1);
                displayList();
            }
        });
        //trailListView.setAdapter(adapter);

        //item 클릭 이벤트 처리
        trailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        //item 롱클릭 이벤트 처리
        trailListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("산책로 메뉴");
                int newi = i;
                alert.setPositiveButton("산책로 삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("확인", "*** getTotalDist: " + adapter.list.get(newi).getTotalDistance());
                        adapter.removeItemInList(newi);
                        adapter.notifyDataSetChanged();
                        dbHelper.Delete(adapter.list.get(newi-1).getJsonList());

                    }
                });
                alert.setNegativeButton("산책로명 수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder alert2 = new AlertDialog.Builder(view.getContext());
                        alert2.setTitle("산책로명 변경");
                        final EditText et = new EditText(view.getContext());
                        alert2.setView(et);
                        alert2.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String result = et.getText().toString();
                                adapter.list.get(newi).setName(et.getText().toString());
                            }
                        });
                        alert2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alert2.show();
                    }
                });
                alert.show();
                return true;
            }
        });

        displayList();
    }

    public void displayList(){
        //Dbhelper의 읽기모드 객체를 가져와 SQLiteDatabase에 담아 사용준비


        //Cursor라는 그릇에 목록을 담아주기
        Cursor cursor = database.rawQuery("SELECT * FROM Trails", null);

        adapter = new ListViewAdapter();

        //목록의 개수만큼 순회하여 adapter에 있는 list배열에 add
        while (cursor.moveToNext()) {
            // 0 : 이름 , 1 : 제이슨리스트 , 2 : 총 길이 , 3 : 주소 , 4 : 주소
            adapter.addItemToList(cursor.getString(0), cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
        }

        //리스트뷰의 어댑터 대상을 여태 설계한 adapter로 설정
        trailListView.setAdapter(adapter);

    }

}
