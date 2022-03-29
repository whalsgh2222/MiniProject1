package com.example.miniproject1;
//l7xxaf0e68fd185f445596200b488c1177af

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.json.JSONException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, TMapView.OnClickListenerCallback {
    ListViewAdapter adapter;
    String API_Key = "l7xxaf0e68fd185f445596200b488c1177af";

    // T Map View
    TMapView tMapView = null;

    // T Map GPS
    TMapGpsManager tMapGPS = null;

    // 신규 경로
    Trail newTrail = null;

    DBHelper dbHelper;

    boolean isRecording = false;
    boolean isDrawing = false;

    private String address1 = "aa";
    private String address2 = "bb";

    //기록용 좌표 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        adapter = new ListViewAdapter();
        ListView trailListView = findViewById(R.id.trailListView);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(MainActivity.this, 1);

        TMapData tmapdata = new TMapData();

        // T Map View
        tMapView = new TMapView(this);

        // API Key
        tMapView.setSKTMapApiKey(API_Key);

        // Initial Setting
        tMapView.setZoomLevel(17);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        // T Map View Using Linear Layout
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        linearLayoutTmap.addView(tMapView);

        // Request For GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // GPS using T Map
        tMapGPS = new TMapGpsManager(this);

        // Initial Setting
        tMapGPS.setMinTime(1000);
        tMapGPS.setMinDistance(10);
        tMapGPS.setProvider(tMapGPS.NETWORK_PROVIDER);
        //tMapGPS.setProvider(tMapGPS.GPS_PROVIDER);

        tMapGPS.OpenGps();


        //기록버튼 클릭이벤트
        Button recBtn = (Button) findViewById(R.id.recordButton);
        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //기록 새로 시작하기
                if (!isRecording && !isDrawing) {
                    clearTrail();
                    Toast.makeText(getApplicationContext(), "기록 시작", Toast.LENGTH_SHORT).show();
                    isRecording = true;
                    recBtn.setText("기록 종료");
                    newTrail = new Trail();
                    newTrail.coorList.add(tMapGPS.getLocation());
                    TMapPoint point1 = newTrail.coorList.get(0);
                    Log.d("TmapTest", "" + point1.getLatitude());
                    Log.d("TmapTest", "" + point1.getLongitude());
                    tmapdata.convertGpsToAddress(point1.getLatitude(), point1.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                        @Override
                        public void onConvertToGPSToAddress(String addr) {
                            address1 = addr;
                            Log.d("TmapTest", "*** updatePositionInfo - addr: " + addr);
                        }
                    });
                }
                //기록 중에 기록을 종료하기
                else if (isRecording) {
                    Toast.makeText(getApplicationContext(), "기록 종료", Toast.LENGTH_SHORT).show();
                    isRecording = false;
                    recBtn.setText("기록 시작");
                    newTrail.coorList.add(tMapGPS.getLocation());
                    drawTrail(newTrail);
                    //dbHelper.insert("새산책로", newTrail.coorList,);
                    newTrail.calTotalDistance();
                    try {
                        TMapPoint point2 = newTrail.coorList.get(newTrail.coorList.size() - 1);
                        Log.d("TmapTest", "" + point2.getLatitude());
                        Log.d("TmapTest", "" + point2.getLongitude());

                        tmapdata.convertGpsToAddress(point2.getLatitude(), point2.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                            @Override
                            public void onConvertToGPSToAddress(String addr) {
                                address2 = addr;
                                Log.d("TmapTest", "*** updatePositionInfo - addr: " + addr);
                            }
                        });
                        //address = tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude());

                    } catch (Exception e) {
                        Log.d("error", "*** Exception: " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    //Toast.makeText(getApplicationContext(), "총 거리 : " + newTrail.totalDistance + "km", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "시작주소 : " + address1 , Toast.LENGTH_SHORT).show();


                    try {
                        dbHelper.insert("신규 산책로", newTrail.coorList, newTrail.totalDistance, address1, address2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    newTrail = null;
                } else {
                    Toast.makeText(getApplicationContext(), "문제", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //터치로 경로 그리기
        Button drawBtn = (Button) findViewById(R.id.drawTrailButton);
        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //그리기 시작
                if (!isDrawing && !isRecording) {
                    clearTrail();
                    Toast.makeText(getApplicationContext(), "그리기 시작", Toast.LENGTH_SHORT).show();
                    isDrawing = true;
                    newTrail = new Trail();
                    drawBtn.setText("그리기 종료");
                    newTrail.coorList.add(tMapGPS.getLocation());
                    TMapPoint point1 = newTrail.coorList.get(0);
                    Log.d("TmapTest", "" + point1.getLatitude());
                    Log.d("TmapTest", "" + point1.getLongitude());
                    tmapdata.convertGpsToAddress(point1.getLatitude(), point1.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                        @Override
                        public void onConvertToGPSToAddress(String addr) {
                            address1 = addr;
                            Log.d("TmapTest", "*** updatePositionInfo - addr: " + addr);
                        }
                    });
                }
                //그리기 종료
                else if (isDrawing) {
                    Toast.makeText(getApplicationContext(), "그리기 종료", Toast.LENGTH_SHORT).show();
                    isDrawing = false;
                    drawBtn.setText("터치로 경로 그리기");
                    drawTrail(newTrail);
                    newTrail.calTotalDistance();

                    //dbHelper.onUpgrade(dbHelper.getWritableDatabase(),1,1);
                    try {
                        TMapPoint point2 = newTrail.coorList.get(newTrail.coorList.size() - 1);
                        Log.d("TmapTest", "" + point2.getLatitude());
                        Log.d("TmapTest", "" + point2.getLongitude());

                        tmapdata.convertGpsToAddress(point2.getLatitude(), point2.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                            @Override
                            public void onConvertToGPSToAddress(String addr) {
                                address2 = addr;
                                Log.d("TmapTest", "*** updatePositionInfo - addr: " + addr);
                            }
                        });
                        //address = tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude());

                    } catch (Exception e) {
                        Log.d("error", "*** Exception: " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    //Toast.makeText(getApplicationContext(), "총 거리 : " + newTrail.totalDistance + "km", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "시작주소 : " + address1 , Toast.LENGTH_SHORT).show();


                    try {
                        dbHelper.insert("신규 산책로", newTrail.coorList, newTrail.totalDistance, address1, address2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), "디비저장\n" + dbHelper.getResult() , Toast.LENGTH_LONG).show();


                    newTrail = null;
                } else {
                    Toast.makeText(getApplicationContext(), "문제", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });

        //목록 확인 클릭 이벤트, 새 액티비티 호출
        Button listBtn = (Button) findViewById(R.id.listButton);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
            }
        });

        //목록 확인 클릭 이벤트, 새 액티비티 호출
        Button clearBtn = (Button) findViewById(R.id.clearButton);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecording && !isDrawing) {
                    clearTrail();
                }
            }
        });

    }

    @Override
    public void onLocationChange(Location location) {
        //원래 2줄만 있던 코드, 좌표 변경 시 좌표 기록을 해보자
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
        //만약 좌표를 기록 중이라면,

        if (isRecording) {
            //Toast.makeText(getApplicationContext(), "좌표 기록 중", Toast.LENGTH_SHORT).show();
            newTrail.coorList.add(tMapGPS.getLocation());
            drawTrail(newTrail);
        }
        /*TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();

        Bitmap markerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.marker_blue);
        tMapMarkerItem.setIcon(markerIcon); // 마커 아이콘 지정

        tMapMarkerItem.setTMapPoint(new TMapPoint(location.getLongitude(), location.getLatitude()));
        tMapMarkerItem.setName("marker");
        tMapView.addMarkerItem("marker", tMapMarkerItem);*/

    }

    // 기록 종료된 산책로 그리기
    public void drawTrail(Trail inTrail) {
        ArrayList<TMapPoint> tempList = inTrail.coorList;

        TMapPolyLine tMapPolyLine = new TMapPolyLine();
        tMapPolyLine.setLineColor(Color.BLUE);
        tMapPolyLine.setLineWidth(2);
        for (int i = 0; i < tempList.size(); i++) {
            tMapPolyLine.addLinePoint(tempList.get(i));
        }
        tMapView.addTMapPolyLine("Line1", tMapPolyLine);
    }

    public void clearTrail() {
        Toast.makeText(getApplicationContext(), "지우기 성공", Toast.LENGTH_SHORT).show();
        tMapView.removeAllTMapPolyLine();
        tMapView.removeAllTMapCircle();
    }

    //터치로 경로 그리기
    @Override
    public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
        if (isDrawing) {
            newTrail.coorList.add(tMapPoint);
            //Toast.makeText(getApplicationContext(), "터치기록", Toast.LENGTH_SHORT).show();

            TMapCircle tMapCircle = new TMapCircle();
            tMapCircle.setCenterPoint(tMapPoint);
            tMapCircle.setRadius(3);
            tMapCircle.setCircleWidth(3);
            tMapCircle.setLineColor(Color.RED);
            tMapCircle.setAreaColor(Color.RED);
            tMapCircle.setAreaAlpha(3);
            drawTrail(newTrail);
            tMapView.addTMapCircle("circle1", tMapCircle);
        } else {
            //Toast.makeText(getApplicationContext(), "기록안하는중", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
        return false;
    }

}