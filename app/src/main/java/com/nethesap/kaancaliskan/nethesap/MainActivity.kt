package com.nethesap.kaancaliskan.nethesap


import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.*
import android.support.design.widget.*
import android.view.*
import android.widget.*
import java.io.*
import java.text.*
import java.util.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    internal var net: Double? = null
    internal var tespit = 1
    internal var temp = StringBuilder()
    internal var numara = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.buton)
        val yedek = findViewById<Button>(R.id.yedek)
        val mat = findViewById<FloatingActionButton>(R.id.mat)
        val text1 = findViewById<TextInputEditText>(R.id.dogru)
        val text2 = findViewById<TextInputEditText>(R.id.yanlis)
        val netgoster = findViewById<TextView>(R.id.net)
        val netgecmis = findViewById<TextView>(R.id.netgecmis)
        val secgec = findViewById<Switch>(R.id.secgec)
        val spinner = findViewById<Spinner>(R.id.spinner)

        spinner.onItemSelectedListener = this
        val sayılar = arrayOf(getString(R.string.spinnerbaşlık), "1", "2", "3", "4 (varsayılan)", "5")

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sayılar)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter


        temp = gecmisoku(temp)
        if (tespit == 1) {
            if (temp.toString() == "") {
                mat.hide()
            } else {
                netgecmis.text = temp.toString()
            }
        }
        btn.setOnClickListener {
            var a = text1.text.toString()
            if (a.isEmpty()) {
                text1.error = getString(R.string.hata)
                a = "1"
                net = 0.0
            }
            var b = text2.text.toString()
            if (b.isEmpty()) {
                text2.error = getString(R.string.hata)
                b = "1"
                net = 0.0
            } else {

                val sayı1 = java.lang.Double.parseDouble(a)
                val sayı2 = java.lang.Double.parseDouble(b)

                net = sayı1 - (sayı2 + sayı2 / numara)
            }

            netgoster.text = getString(R.string.netyaz) + net!!

            val tarihbicimi = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val tarih = Date()

            if (secgec.isChecked) {
                try {
                    val outstring="\n"+tarihbicimi.format(tarih)+" Net: "+net
                    File("gecmis.nh").bufferedWriter().use { out -> out.write(outstring) }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                temp = gecmisoku(temp)
                netgecmis.text = temp.toString()
                mat.show()
            }
        }

        mat.setOnClickListener {
            mat.hide()
            try {
                val fos = openFileOutput(getString(R.string.dosyaadı), Context.MODE_PRIVATE)
                val osw = OutputStreamWriter(fos)
                osw.write("")
                netgecmis.text = ""
                text1.setText("")
                text2.setText("")
                netgoster.text = ""
                osw.close()
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Snackbar.make(netgoster, getString(R.string.gecmissilindi), Snackbar.LENGTH_SHORT).show()
        }
        yedek.setOnClickListener {
            temp = gecmisoku(temp)

            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                } else {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }
            }
            val dizin = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "NetHesap")
            val dosya = File(dizin, getString(R.string.dosyaadı))

            try {
                dizin.mkdirs()
                val yazıcı = FileWriter(dosya)
                yazıcı.write(temp.toString())
                yazıcı.flush()
                yazıcı.close()
                Snackbar.make(netgoster, getString(R.string.yedekkaydedildi), Snackbar.LENGTH_LONG).show()

            } catch (e: IOException) {
                e.printStackTrace()

            }
        }

    }

    fun gecmisoku(temp: StringBuilder): StringBuilder {

        this.temp = temp
        val temp = StringBuilder()
        val dizin = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "NetHesap")
        val dosya = File(dizin, getString(R.string.dosyaadı))

        try {

            val bufferedReader: BufferedReader = dosya.bufferedReader()

            val inputString = bufferedReader.use { it.readText() }

            temp.append(inputString)
        }catch (e: Exception){
            e.printStackTrace()
        }

        return temp
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        if (pos > 0) {
            numara = pos
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
        numara = 4
    }
}
