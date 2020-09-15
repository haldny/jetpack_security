package br.com.haldny.jetsec

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.jetsecdemo.R
import kotlinx.android.synthetic.main.activity_shared_prefs.*

class SharedPrefsActivity : AppCompatActivity() {

    private val savedValueKey = "saved_value"
    private lateinit var securedSharedPrefs: SharedPreferences
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_prefs)
        init()
        listeners()
    }

    private fun init() {

        //val masterKey = MasterKey.Builder(this, "master_key").build()

        val masterKey = MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

/*        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "_androidx_security_master_key_",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setKeySize(256)
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setUserAuthenticationRequired(true)
            setUserAuthenticationValidityDurationSeconds(5)
        }.build()


        val masterKey = MasterKey.Builder(this)
            .setKeyGenParameterSpec(keyGenParameterSpec)
            .build()*/

        sharedPrefs = getSharedPreferences("values", Context.MODE_PRIVATE)

        securedSharedPrefs = EncryptedSharedPreferences.create(
            this,
            "values_secured",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        setUi()
    }

    private fun listeners() {
        bt_save.setOnClickListener {
            if (isSavingValid()) {
                sharedPrefs.edit().putString(savedValueKey, et_data.text.toString()).apply()
                setUi()
            }
        }

        bt_save_encrypted.setOnClickListener {
            if (isSavingValid()) {
                securedSharedPrefs.edit().putString(savedValueKey, et_data.text.toString()).apply()
                setUi()
            }
        }
    }

    private fun setUi() {
        tv_normal_value.text = getString(R.string.value_x, sharedPrefs.getString(savedValueKey, ""))
        tv_encrypted_value.text = getString(R.string.value_jetpack_x, securedSharedPrefs.getString(savedValueKey, ""))
    }

    private fun isSavingValid(): Boolean {
        return if (et_data.text.toString().isNotEmpty())
            true
        else {
            Toast.makeText(this, getString(R.string.enter_any_value), Toast.LENGTH_LONG).show()
            false
        }
    }
}
