package com.nethesap.kaancaliskan.nethesap


import android.Manifest
import kotlinx.android.synthetic.main.activity_main.*
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

    private var result: Double? = 0.toDouble()
    private var fabcheck = 1
    private var temp = StringBuilder()
    private var divide = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner.onItemSelectedListener = this

        val dividerArray = arrayOf(getString(R.string.spinnerbaşlık), "1", "2", "3", "4 (varsayılan)", "5")

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dividerArray)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter


        temp = temp.readHistory
        if (fabcheck == 1) {
            if (temp.toString() == "") {
                fab.hide()
            } else {
                history.text = temp.toString()
            }
        }
        button.setOnClickListener {
            var a = trueEditText.text.toString()
            if (a.isEmpty()) {
                trueEditText.error = getString(R.string.hata)
                a = "1"
                result = 0.0
            }
            var b = falseEditText.text.toString()
            if (b.isEmpty()) {
                falseEditText.error = getString(R.string.hata)
                b = "1"
                result = 0.0
            } else {

                val trueQuestions = java.lang.Double.parseDouble(a)
                val wrongQuestions = java.lang.Double.parseDouble(b)

                result = trueQuestions - (wrongQuestions + wrongQuestions / divide)
            }

            resultTextView.text=(getString(R.string.netyaz) + result!!)

            val dateStyle = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val date = Date()

            if (historySwitch.isChecked) {
                try {
                    val fos = openFileOutput(getString(R.string.dosyaadı), Context.MODE_APPEND)
                    val osw = OutputStreamWriter(fos)
                    osw.append("\n"+dateStyle.format(date)+" Net: "+result)
                    osw.close()
                    fos.close()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                temp = temp.readHistory
                history.text = temp.toString()
                fab.show()
            }
        }

        fab.setOnClickListener {
            fab.hide()
            try {
                val fos = openFileOutput(getString(R.string.dosyaadı), Context.MODE_PRIVATE)
                val osw = OutputStreamWriter(fos)
                osw.write("")
                history.text = ""
                trueEditText.setText("")
                falseEditText.setText("")
                resultTextView.text = ""
                osw.close()
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Snackbar.make(resultTextView, getString(R.string.gecmissilindi), Snackbar.LENGTH_SHORT).show()
        }
        backup.setOnClickListener {
            temp = temp.readHistory

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
                val writer = FileWriter(dosya)
                writer.write(temp.toString())
                writer.flush()
                writer.close()
                Snackbar.make(resultTextView, getString(R.string.yedekkaydedildi), Snackbar.LENGTH_LONG).show()

            } catch (e: IOException) {
                e.printStackTrace()

            }
        }

    }

    private val StringBuilder.readHistory: StringBuilder
        get() {
            val temp = this@readHistory
            temp.setLength(0)
            try {
                val file = InputStreamReader(openFileInput(getString(R.string.dosyaadı)))
                val br = BufferedReader(file)
                var line = br.readLine()
                while (line != null) {
                    temp.append(line + "\n")
                    line = br.readLine()
                }
                br.close()
                file.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return temp
        }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        if (pos > 0) {
            divide = pos
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
        divide = 4
    }
}