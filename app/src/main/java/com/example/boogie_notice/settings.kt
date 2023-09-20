package com.example.boogie_notice

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class settings: Fragment(R.layout.fragment_settings) {

    companion object{
        fun newInstance(): settings {
            return settings()
        }

    }

    private lateinit var auth: FirebaseAuth


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val database = Firebase.database
        auth = Firebase.auth

        val hansungemailref = database.getReference("hansungemailref")
        val computeremailref = database.getReference("computeremailref")

        val userEmail = auth.currentUser!!.getEmail().toString()
        val username = userEmail.substringBefore('@')

        val keyword_ref = database.getReference("keyword_ref")
        val keywordtext = view.findViewById<EditText>(R.id.keywordtext)
        val keywordbtn = view.findViewById<Button>(R.id.keywordbtn)


        keywordbtn.setOnClickListener {
            keyword_ref.child(auth.currentUser?.uid.toString()).push().setValue(keywordtext.text.toString())
            Toast.makeText(context, "\n  키워드 등록 완료  ", Toast.LENGTH_SHORT).show()
            // EditText를 비워줍니다.
            keywordtext.text = null
        }



        val currentloginemail = view.findViewById<TextView>(R.id.username)

        currentloginemail.text = "${username}님"

        val checkbox1 = view.findViewById<CheckBox>(R.id.hasungcheck)

        checkbox1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hansungemailref.child(auth.currentUser?.uid.toString()).push().setValue(userEmail)
                Toast.makeText(context, "\n  한성대 공지 이메일 수신동의  ", Toast.LENGTH_SHORT).show()

            }
            // 체크박스가 체크되어 있을 때 수행할 작업
            else {
                hansungemailref.child(auth.currentUser?.uid.toString()).removeValue()

            }
        }


        val checkbox2 = view.findViewById<CheckBox>(R.id.computercheck)

        checkbox2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                computeremailref.child(auth.currentUser?.uid.toString()).push().setValue(userEmail)
                Toast.makeText(context, "\n  컴공 공지 이메일 수신동의  ", Toast.LENGTH_SHORT).show()

            } else {
                computeremailref.child(auth.currentUser?.uid.toString()).removeValue()

            }
        }


        val checkbox3 = view.findViewById<Button>(R.id.discodebutton)

        checkbox3.setOnClickListener{
            val intent = Intent(context, ViewActivity::class.java)
            intent.putExtra("URL", "https://discord.gg/XcAMJ7CXeV")
            startActivity(intent)

        }
    }
}