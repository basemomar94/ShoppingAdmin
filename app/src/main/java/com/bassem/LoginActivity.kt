package com.bassem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.bassem.shoppingadmin.MainActivity
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var binding: ActivityLoginBinding? = null
    var db: FirebaseFirestore? = null
    override fun onStart() {
        super.onStart()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val sharedPreferences = getSharedPreferences("log", MODE_PRIVATE)
        val isLog = sharedPreferences.getBoolean("log", false)
        if (isLog) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //
        binding!!.loginBtu.setOnClickListener {
            loading()
            val username = binding!!.mailSignin.text.toString().trim().lowercase()
            val password = binding!!.passSigin.text.toString().trim().lowercase()
            errorEmpty(username, binding!!.mailSignLayout)
            errorEmpty(password, passSignLayout)
            if (username.isNotEmpty() && password.isNotEmpty()){
                adminLogin(username, password)

            } else {
                //Toast.makeText(this, "please check your login info", Toast.LENGTH_SHORT).show()
                normal()

            }
        }


    }

    private fun adminLogin(username: String, password: String) {
        db = FirebaseFirestore.getInstance()
        db?.collection("admins")?.document(username)?.get()?.addOnCompleteListener {
            if (it.isSuccessful) {
                val firebasePassword = it.result?.getString("password")
                if (firebasePassword == password) {
                    saveLogin()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "please check your login info", Toast.LENGTH_SHORT).show()
                    normal()
                }
            } else {
                normal()
                Toast.makeText(this, "please check your login info", Toast.LENGTH_SHORT).show()

            }

        }

    }

    private fun saveLogin() {
        val sharedPreferences = getSharedPreferences("log", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("log", true)
        editor.apply()

    }

    private fun loading() {
        binding!!.loginBtu.visibility = View.GONE
        binding!!.progressBar2.visibility = View.VISIBLE
    }

    private fun normal() {
        binding!!.loginBtu.visibility = View.VISIBLE
        binding!!.progressBar2.visibility = View.GONE
    }

    private fun errorEmpty(text: String, layout: TextInputLayout) {
        if (text.isEmpty()) {
            layout.error = "*required"

        } else {
            layout.isErrorEnabled = false
        }
    }


}