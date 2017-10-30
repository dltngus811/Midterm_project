package com.midterm_project;

import android.app.Activity;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends TabActivity {

    static int countChkbox = 0; // 체크박스 invisible 상태였다가 추가하면 텍스트에 맞춰서 추가하는걸로
    final int REQ_CODE_SELECT_IMAGE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 일기장부분 구성
        final LinearLayout baseLayout = (LinearLayout)findViewById(R.id.diary);
        final TextView diary_title = (TextView)findViewById(R.id.diary_title);
        final TextView diary_contents = (TextView)findViewById(R.id.diary_contents);
        Button diary_save = (Button)findViewById(R.id.diary_save);
        Button diart_load = (Button)findViewById(R.id.diary_load);

        // 갤러리 부분 구성
        LinearLayout picture = (LinearLayout)findViewById(R.id.picture);
        ImageView picture_view = (ImageView)findViewById(R.id.picture_view);
        Button picture_load = (Button)findViewById(R.id.picture_load);

        // 계획 부분 구성
        final CheckBox[] chkBox = { (CheckBox)findViewById(R.id.chk1),
                        (CheckBox)findViewById(R.id.chk2),
                        (CheckBox)findViewById(R.id.chk3),
                        (CheckBox)findViewById(R.id.chk4),
                        (CheckBox)findViewById(R.id.chk5),
                        (CheckBox)findViewById(R.id.chk6),
                        (CheckBox)findViewById(R.id.chk7),
                        (CheckBox)findViewById(R.id.chk8),
                        (CheckBox)findViewById(R.id.chk9),
                        (CheckBox)findViewById(R.id.chk10)};

        final LinearLayout plan = (LinearLayout)findViewById(R.id.plan);
        final Button plan_add = (Button)findViewById(R.id.plan_add);

        TabHost tabHost = getTabHost();

        final TabSpec tabSpecDiary = tabHost.newTabSpec("Diary").setIndicator("일기장");
        tabSpecDiary.setContent(R.id.diary);
        tabHost.addTab(tabSpecDiary);

        TabSpec tabSpecPicture = tabHost.newTabSpec("Picture").setIndicator("사진");
        tabSpecPicture.setContent(R.id.picture);
        tabHost.addTab(tabSpecPicture);

        TabSpec tabSpecPlan = tabHost.newTabSpec("Plan").setIndicator("오늘 계획");
        tabSpecPlan.setContent(R.id.plan);
        tabHost.addTab(tabSpecPlan);

        tabHost.setCurrentTab(0);

        final EditText edittext = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        diary_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() + diary_title.getText().toString() + ".txt"));
                    bw.write(diary_contents.getText().toString());
                    bw.close();
                    Toast.makeText(MainActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                }
            }
        });

        diart_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(getFilesDir() + "exam.txt"));
                    String readStr = "";
                    String str = null;

                    while (((str = br.readLine()) != null)) {
                        readStr += str + "\n";
                    }
                    diary_contents.setText(readStr);
                    br.close();

                }catch(FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "파일을 찾을 수 없음.", Toast.LENGTH_SHORT).show();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
        plan_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countChkbox > 10) {
                    Toast toast = Toast.makeText(getApplicationContext(),"체크박스 갯수 초과",Toast.LENGTH_LONG);
                    toast.show();
                }

                else {
                    builder.setTitle("계획 이름을 입력하세요");
                    builder.setView(edittext);
                    builder.setPositiveButton("추가",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    chkBox[countChkbox].setText(edittext.getText().toString());
                                    chkBox[countChkbox].setVisibility(View.VISIBLE);
                                    countChkbox++;
                                    Toast toast = Toast.makeText(getApplicationContext(), "추가하였습니다.", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            });
                    builder.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            });
                    builder.show();
                }

                // 위에 builder.setView 로 뷰를 set하고 다시 추가버튼을 누르면 종료됨.
                plan.removeView(view);
            }
        });


        picture_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getBaseContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();

        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());

                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView)findViewById(R.id.picture_view);

                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);

                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        return imgName;
    }
}