package com.example.boogie_notice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Splash : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth
        val database = Firebase.database
        val autologinref = database.getReference("autologinref")
        var yesorno=false
        var a = 0//


        if(auth.currentUser?.uid==null){
            Handler().postDelayed({

                startActivity(Intent(this, JoinActiviy::class.java))

                Toast.makeText(this," 회원가입 및 로그인을 해주세요 ",Toast.LENGTH_SHORT).show()
                finish()
            },3000)

        }
        else{
            autologinref.child(auth.currentUser?.uid.toString())//데이터 베이스에서 계정별로의 저장된 북마크 목록 불러와서 어뎁터에 실시간 최신화
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataModel in snapshot.children) {
                            val value = dataModel.getValue(String::class.java)
                            Log.d("로그인여부",value.toString())
                            if (a !=0) {
                                break

                            }
                            if(value.toString()=="true"&&auth.currentUser?.uid!=null){
                                yesorno=true
                                Log.d("yesorno1",yesorno.toString())
                                Handler().postDelayed({

                                    val intent = Intent(baseContext, MainActivity::class.java) // MainActivity 대신 시작하려는 액티비티 클래스를 넣어주세요
                                    startActivity(intent)
                                    Toast.makeText(baseContext," 로그인 성공 ",Toast.LENGTH_SHORT).show()
                                    ++a


                                    finish()
                                },3000)

                            }
                            else{
                                yesorno=false
                                Log.d("yesorno2",yesorno.toString())
                                Handler().postDelayed({
                                    //auth.signOut()
                                    startActivity(Intent(baseContext, JoinActiviy::class.java))

                                    Toast.makeText(baseContext," 회원가입 및 로그인을 해주세요 ",Toast.LENGTH_SHORT).show()
                                    ++a
                                    finish()
                                },3000)

                            }

                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("autologinref", "DBerror")
                    }

                })



        }



    }
}