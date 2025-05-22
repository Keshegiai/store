package com.example.tobaccoalcoholshop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tobaccoalcoholshop.data.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoginActivity : AppCompatActivity() {

    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button

    private val CREDENTIAL_SHARED_PREF = "our_shared_pref"
    private val USERS_KEY = "users"
    private val LOGGED_IN_USER_KEY = "logged_in_username"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edUsername = findViewById(R.id.ed_username)
        edPassword = findViewById(R.id.ed_password)
        btnLogin = findViewById(R.id.btn_login)
        btnSignUp = findViewById(R.id.btn_signup)

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val username = edUsername.text.toString()
            val password = edPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Имя пользователя и пароль не должны быть пустыми", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.contains(" ") || password.contains(" ")) {
                Toast.makeText(this, "Имя пользователя и пароль не должны содержать пробелов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val trimmedUsername = username.trim()
            val trimmedPassword = password.trim()

            if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
                Toast.makeText(this, "Имя пользователя и пароль не должны состоять только из пробелов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val prefs = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE)
            val gson = Gson()

            val usersJson = prefs.getString(USERS_KEY, "[]")
            val userListType = object : TypeToken<List<User>>() {}.type
            val users: List<User> = gson.fromJson(usersJson, userListType) ?: emptyList()

            val user = users.find { it.username == trimmedUsername && it.password == trimmedPassword }

            if (user != null) {
                val editor = prefs.edit()
                editor.putString(LOGGED_IN_USER_KEY, user.username)
                editor.apply()

                Toast.makeText(this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Неверное имя пользователя или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
}