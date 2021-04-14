package com.example.mytunes;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.lists);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    final ArrayList<File> mysongs = fetchSongs(Environment.getExternalStorageDirectory());
                    String [] songname = new String[mysongs.size()];
                    for (int i=0;i<mysongs.size();i++){
                        songname[i]=mysongs.get(i).getName().replace(".mp3","");
                    }
                        ArrayAdapter <String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,songname);
                        listview.setAdapter(adapter);

                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, Playsong.class);
                                String currentsong = listview.getItemAtPosition(position).toString();
                                intent.putExtra("songList", mysongs);
                                intent.putExtra("currentsong", currentsong);
                                intent.putExtra("position",position);
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        if (songs!=null){
            for (File myfile: songs){
                if (myfile.isDirectory() && !myfile.isHidden()){
                    arrayList.addAll(fetchSongs(myfile));
                }
                else {
                    if (myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")){
                        arrayList.add(myfile);
                    }
                }
            }
        }
        return arrayList;
    }

}