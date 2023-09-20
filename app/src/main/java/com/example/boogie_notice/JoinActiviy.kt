package com.example.boogie_notice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JoinActiviy : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_activiy)
        auth = Firebase.auth
        val database = Firebase.database

        val autologinref = database.getReference("autologinref")

        val email = findViewById<TextView>(R.id.emailArea)
        val password = findViewById<TextView>(R.id.passwordArea)
        val autologin = findViewById<CheckBox>(R.id.autologin)
        val joinbtn = findViewById<Button>(R.id.joinBTN)
        var yesorno=false

        autologin.setOnCheckedChangeListener { _, isChecked ->
            yesorno = isChecked
        }


        joinbtn.setOnClickListener{

            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val intent= Intent(this, MainActivity::class.java)
                        if(yesorno){
                            autologinref.child(auth.currentUser?.uid.toString()).removeValue()
                            autologinref.child(auth.currentUser?.uid.toString()).push().setValue("true")
                            Toast.makeText(this@JoinActiviy,"\n  자동 로그인 동의 ",Toast.LENGTH_SHORT).show()

                        }
                        else{
                            autologinref.child(auth.currentUser?.uid.toString()).removeValue()
                            Toast.makeText(this," 자동로그인 미사용 ",Toast.LENGTH_SHORT).show()
                            autologinref.child(auth.currentUser?.uid.toString()).push().setValue("false")
                        }

                        Toast.makeText(this," 회원가입 성공 ",Toast.LENGTH_SHORT).show()

                        startActivity(intent)

                    } else {
                        Toast.makeText(this," 이메일 형식이 올바르지않습니다. ",Toast.LENGTH_SHORT).show()


                    }
                }

        }
        val loginBTN=findViewById<Button>(R.id.loginBTN)
        loginBTN.setOnClickListener{
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent= Intent(this, MainActivity::class.java)
                        if(yesorno){
                            autologinref.child(auth.currentUser?.uid.toString()).removeValue()
                            autologinref.child(auth.currentUser?.uid.toString()).push().setValue("true")
                            Toast.makeText(this@JoinActiviy,"\n  자동 로그인 동의 ",Toast.LENGTH_SHORT).show()

                        }
                        else{
                            autologinref.child(auth.currentUser?.uid.toString()).removeValue()
                            Toast.makeText(this," 자동로그인 미사용 ",Toast.LENGTH_SHORT).show()
                            autologinref.child(auth.currentUser?.uid.toString()).push().setValue("false")


                        }

                        Toast.makeText(this," 로그인 성공 ",Toast.LENGTH_SHORT).show()

                        startActivity(intent)

                    } else {
                        Toast.makeText(this,"비밀번호 혹은 이메일이 올바르지않습니다.",Toast.LENGTH_SHORT).show()

                    }
                }

        }

    }
}