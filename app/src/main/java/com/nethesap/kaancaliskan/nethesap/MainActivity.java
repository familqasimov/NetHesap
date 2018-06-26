package com.nethesap.kaancaliskan.nethesap;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.Keep;
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

public class MainActivity extends AppCompatActivity {
    @Keep

    Double net;
    final String dosyaadı="gecmis.nh";
    final String hata="Boş Bırakılamaz!";
    final String netyaz="Netiniz: ";
    int tespit=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn=findViewById(R.id.buton);
        final Button yedek=findViewById(R.id.yedek);
        final FloatingActionButton mat=findViewById(R.id.mat);
        final TextInputEditText text1=findViewById(R.id.dogru);
        final TextInputEditText text2=findViewById(R.id.yanlis);
        final TextInputEditText bos=findViewById(R.id.bos);
        final TextView view= findViewById(R.id.net);
        final TextView netgecmis= findViewById(R.id.netgecmis);
        final Switch secgec=findViewById(R.id.secgec);

        //önceden kaydedilen geçmişi dosyadan çekiyoruz
        try
        {
            FileInputStream fis;
            try {
                fis = openFileInput(dosyaadı);
            }catch (FileNotFoundException a){
                a.printStackTrace();
                mat.hide();
                //eğer ilk açılış ise direkt floatingactionbutton gizleniyor
                tespit=0;
            }
            fis=openFileInput(dosyaadı);
            int c;
            StringBuilder temp= new StringBuilder();
            while ((c = fis.read()) != -1)
            {
                temp.append(Character.toString((char) c));
            }
            if(tespit==1){
                if (temp.toString().equals("")) {
                    mat.hide();
                    //eğer dosya boş ise floatingactionbutton gizleniyor
                }
                else
                {

                    netgecmis.setText(temp.toString());
                    //boş değilse textview a yazıyoruz
                }}

            fis.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                String a=text1.getText().toString();
                //burada eğer herhangi biri boş bırakılırsa hata verilecek böylece lanet olası nullpointer dan kurtuluyacağız
                if(a.isEmpty()){
                    text1.setError(hata);
                    a="1";
                    net=0.0;
                }
                String b=text2.getText().toString();
                if(b.isEmpty()){
                    text2.setError(hata);
                    b="1";
                    net=0.0;
                }
                String d=bos.getText().toString();
                if(d.isEmpty()){
                    bos.setError(hata);
                    d="1";
                    //bu d neden hiç kullanılmadı diyorda diğerlerine demiyor, hiçbir fikrim yok...
                    net=0.0;
                }
                else{

                    Double sayı1 = Double.parseDouble(a);
                    Double sayı2 = Double.parseDouble(b);
                    Double sayı3 = Double.parseDouble(d);
                    //yazılanları alıyoruz

                    net = sayı1 - (sayı2 + (sayı2 / 4)) - sayı3;}
                //neti hesaplıyoruz

                view.setText(netyaz+ net);
                //neti yazdırıyoruz

                DateFormat tarihbicimi = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date tarih= new Date();
                //buradan geçmişi kaydederken kullandığımız tarihi alacağız

                if(secgec.isChecked()){
                    try
                    {
                        FileOutputStream fos=openFileOutput(dosyaadı, Context.MODE_APPEND);
                        OutputStreamWriter osw = new OutputStreamWriter(fos);
                        osw.append("\n" + tarihbicimi.format(tarih) + " Net: " + net);
                        //hesapladığımız sonucu dosyaya yazdırıyoruz
                        //bu dosya data/data/com.kaancaliskan.nethesap/files içinde
                        osw.close();
                        fos.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        FileInputStream fis;
                        fis = openFileInput(dosyaadı);

                        int c;
                        StringBuilder temp= new StringBuilder();
                        while ((c = fis.read()) != -1)
                        {
                            temp.append(Character.toString((char) c));
                        }
                        netgecmis.setText(temp.toString());
                        //bu blokta dosyaya yazdırdığımız sonucu okuyup textview a yazıyoruz
                        mat.show();
                        fis.close();

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }}
            }
        });

        mat.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                mat.hide();
                try
                {
                    //bu blok ise floatingactionbutton a bastığımızda ne var ne yok temizliyor
                    FileOutputStream fos=openFileOutput(dosyaadı, Context.MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write("");
                    netgecmis.setText("");
                    text1.setText("");
                    text2.setText("");
                    bos.setText("");
                    view.setText("");
                    osw.close();
                    fos.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                //kullanıcıyı haberdar etmek farz
                Snackbar.make(view, "Geçmiş silindi!", Snackbar.LENGTH_SHORT).show();
            }
        });
        yedek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    FileInputStream fis=openFileInput(dosyaadı);
                    int c;
                    StringBuilder temp= new StringBuilder();
                    while ((c = fis.read()) != -1)
                    {
                        temp.append(Character.toString((char) c));
                    }

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        {
                            Snackbar.make(view,"Lütfen ayarlardan depolama iznini veriniz!", Snackbar.LENGTH_LONG).show();
                        }
                        else
                        {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }
                    }
                    File dizin = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"NetHesap");
                    File dosya=new File(dizin, dosyaadı);

                    dizin.mkdirs();

                        FileWriter yazıcı = new FileWriter(dosya);
                        yazıcı.write(temp.toString());
                        yazıcı.flush();
                        yazıcı.close();

                        Snackbar.make(view, "Yedek, Documents dosyası içine kaydedildi!", Snackbar.LENGTH_LONG).show();

                }catch (IOException e){
                    e.printStackTrace();

                }

            }
        });

    }
}
