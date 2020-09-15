package br.com.haldny.jetsec

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.example.jetsecdemo.R
import kotlinx.android.synthetic.main.activity_files.*
import java.io.*
import java.lang.StringBuilder

class FilesActivity : AppCompatActivity() {

    private lateinit var normalFile: File
    private lateinit var secretFile: File
    private lateinit var encryptedFile: EncryptedFile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)
        init()
        listeners()
    }

    private fun init() {
        normalFile = File(filesDir, "normal")

        secretFile = File(filesDir, "secret")

        encryptedFile = EncryptedFile.Builder(
            this,
            secretFile,
            MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        )
            .setKeysetAlias("file_key")
            .setKeysetPrefName("secret_file_shared_prefs")
            .build()

    }

    private fun listeners() {
        bt_read_file.setOnClickListener {
            printFileContent(normalFile)
        }

        bt_show_content.setOnClickListener {
            printFileContent(secretFile)
        }

        bt_read_file_encrypted.setOnClickListener {
            encryptedFile.openFileInput().use { inputstream ->
                tv_result.text = String(inputstream.readBytes(), Charsets.UTF_8)
            }
        }

        bt_save_file.setOnClickListener {
            if (!normalFile.exists())
                try {
                    normalFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            try {
                OutputStreamWriter(FileOutputStream(normalFile)).let {
                    it.write(et_data.text.toString())
                    it.close()
                }

                Toast.makeText(this, getString(R.string.file_saved), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        bt_save_encrypted.setOnClickListener {
            secretFile.delete()

            encryptedFile.openFileOutput().use { outputstream ->
                outputstream.write(et_data.text.toString().toByteArray())

                Toast.makeText(this, getString(R.string.file_saved), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun printFileContent(file: File) {
        if (file.exists()) {
            val strBuilder = StringBuilder()
            BufferedReader(FileReader(file)).readLines().forEach {
                strBuilder.append(it)
            }
            tv_result.text = strBuilder.toString()
        } else
            Toast.makeText(this, getString(R.string.file_not_found), Toast.LENGTH_SHORT).show()
    }
}
