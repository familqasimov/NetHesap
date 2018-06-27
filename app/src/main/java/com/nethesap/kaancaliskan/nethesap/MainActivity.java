package com.nethesap.kaancaliskan.nethesap;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.*;
import android.support.design.widget.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Double net;
    int tespit=1;
    StringBuilder temp=new StringBuilder();
    int numara=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn=findViewById(R.id.buton);
        final Button yedek=findViewById(R.id.yedek);
        final FloatingActionButton mat=findViewById(R.id.mat);
        final TextInputEditText text1=findViewById(R.id.dogru);
        final TextInputEditText text2=findViewById(R.id.yanlis);
        final TextView netgoster= findViewById(R.id.net);
        final TextView netgecmis= findViewById(R.id.netgecmis);
        final Switch secgec=findViewById(R.id.secgec);
        final Spinner spinner=findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);
        String[] sayılar=new String[]{getString(R.string.spinnerbaşlık),"1","2","3","4 (varsayılan)","5"};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sayılar);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        temp = gecmisoku(temp);
            if(tespit==1){
                if (temp.toString().equals("")) {
                    mat.hide();
                }
                else{
                    netgecmis.setText(temp.toString());
                }
            }
            btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                String a=text1.getText().toString();
                if(a.isEmpty()){
                    text1.setError(getString(R.string.hata));
                    a="1";
                    net=0.0;
                }
                String b=text2.getText().toString();
                if(b.isEmpty()){
                    text2.setError(getString(R.string.hata));
                    b="1";
                    net=0.0;
                }
                else{

                    Double sayı1 = Double.parseDouble(a);
                    Double sayı2 = Double.parseDouble(b);

                    net = sayı1 - (sayı2 + (sayı2 / numara));}

                netgoster.setText(getString(R.string.netyaz)+ net);

                DateFormat tarihbicimi = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date tarih= new Date();

                if(secgec.isChecked()){
                    try
                    {
                        FileOutputStream fos=openFileOutput(getString(R.string.dosyaadı), Context.MODE_APPEND);
                        OutputStreamWriter osw = new OutputStreamWriter(fos);
                        osw.append("\n" + tarihbicimi.format(tarih) + " Net: " + net);
                        //this file is in the data/data/com.kaancaliskan.nethesap/files
                        osw.close();
                        fos.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    temp=gecmisoku(temp);
                        netgecmis.setText(temp.toString());
                        mat.show();}
            }
        });

        mat.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                mat.hide();
                try
                {
                    FileOutputStream fos=openFileOutput(getString(R.string.dosyaadı), Context.MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write("");
                    netgecmis.setText("");
                    text1.setText("");
                    text2.setText("");
                    netgoster.setText("");
                    osw.close();
                    fos.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                Snackbar.make(netgoster, getString(R.string.gecmissilindi), Snackbar.LENGTH_SHORT).show();
            }
        });
        yedek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                temp=gecmisoku(temp);

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }
                        else
                        {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }
                    }
                    File dizin = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"NetHesap");
                    File dosya=new File(dizin, getString(R.string.dosyaadı));

                try {
                    dizin.mkdirs();
                        FileWriter yazıcı = new FileWriter(dosya);
                        yazıcı.write(temp.toString());
                        yazıcı.flush();
                        yazıcı.close();
                        Snackbar.make(netgoster, getString(R.string.yedekkaydedildi), Snackbar.LENGTH_LONG).show();

                }catch (IOException e){
                    e.printStackTrace();

                }

            }
        });

    }
    public StringBuilder gecmisoku (StringBuilder temp){
        this.temp=temp;
        temp=new StringBuilder();
        try

        {
            FileInputStream fis;
            fis = openFileInput(getString(R.string.dosyaadı));

            int c;
            while ((c = fis.read()) != -1) {
                temp.append(Character.toString((char) c));
            }
            fis.close();

        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (pos>0){
            numara=pos;
    }}
    public void onNothingSelected(AdapterView<?> arg0) {
        numara =4;
    }
}
