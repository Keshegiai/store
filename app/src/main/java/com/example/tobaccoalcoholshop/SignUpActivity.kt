package com.example.tobaccoalcoholshop

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tobaccoalcoholshop.data.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SignUpActivity : AppCompatActivity() {

    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var edConfirmPassword: EditText
    private lateinit var btnCreateUser: Button
    private lateinit var btnBack: Button
    private val CREDENTIAL_SHARED_PREF = "our_shared_pref"
    private val USERS_KEY = "users"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        edUsername = findViewById(R.id.ed_username)
        edPassword = findViewById(R.id.ed_password)
        edConfirmPassword = findViewById(R.id.ed_confirm_pwd)
        btnCreateUser = findViewById(R.id.btn_create_user)
        btnBack = findViewById(R.id.btn_back)

        btnBack.setOnClickListener {
            finish()
        }

        btnCreateUser.setOnClickListener {
            val username = edUsername.text.toString()
            val password = edPassword.text.toString()
            val confirmPassword = edConfirmPassword.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Все поля должны быть заполнены и не должны быть пустыми", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.contains(" ") || password.contains(" ") || confirmPassword.contains(" ")) {
                Toast.makeText(this, "Имя пользователя и пароли не должны содержать пробелов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val trimmedUsername = username.trim()
            val trimmedPassword = password.trim()
            val trimmedConfirmPassword = confirmPassword.trim()

            if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty() || trimmedConfirmPassword.isEmpty()) {
                Toast.makeText(this, "Поля не должны состоять только из пробелов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (trimmedUsername.length < 4) {
                Toast.makeText(this, "Имя пользователя должно содержать минимум 4 символа", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (trimmedPassword.length < 6) {
                Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (trimmedPassword != trimmedConfirmPassword) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE)
            val gson = Gson()

            val usersJson = prefs.getString(USERS_KEY, "[]")
            val userListType = object : TypeToken<MutableList<User>>() {}.type
            val users: MutableList<User> = gson.fromJson(usersJson, userListType) ?: mutableListOf()

            if (users.any { it.username.equals(trimmedUsername, ignoreCase = true) }) {
                Toast.makeText(this, "Пользователь с таким именем уже существует", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            users.add(User(trimmedUsername, trimmedPassword))

            val editor = prefs.edit()
            editor.putString(USERS_KEY, gson.toJson(users))
            editor.apply()

            Toast.makeText(this, "Аккаунт успешно создан", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}