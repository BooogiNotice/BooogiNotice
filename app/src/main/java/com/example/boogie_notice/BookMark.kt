package com.example.boogie_notice

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class BookMark : AppCompatActivity() {

    private lateinit var BookmarksFragment: Bookmarks
    private lateinit var SettingsFragment: settings
    private lateinit var auth: FirebaseAuth

    /*

    private lateinit var auth: FirebaseAuth
    private val contentModel = mutableListOf<contentsModel>()
    private val keyModel = mutableListOf<String>()
    private val emailkeyModel = mutableListOf<String>()
    private lateinit var notificationHelper1:Notification


     */

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_mark)
        auth = Firebase.auth

        val logout = findViewById<TextView>(R.id.logoutBTN2)
        logout.setOnClickListener {

            Firebase.auth.signOut()
            val intent = Intent(this, JoinActiviy::class.java)
            startActivity(intent)
        }
        /*

        val database = Firebase.database
        val myBookmark = database.getReference("bookmark_ref")
        val hansungemailref = database.getReference("hansungemailref")
        val computeremailref = database.getReference("computeremailref")

        val rVadapter = RVadapter(contentModel)
        val recyclerView = findViewById<RecyclerView>(R.id.bookmarkrv)


        recyclerView.adapter = rVadapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.setHasFixedSize(true)
        val userEmail = auth.currentUser!!.getEmail().toString()


        val currentloginemail = findViewById<TextView>(R.id.useremail)
        val username = userEmail.substringBefore('@')

        currentloginemail.text = "${username}님"



        notificationHelper1 = Notification()



        val logout = findViewById<TextView>(R.id.logoutBTN2)
        logout.setOnClickListener {

            Firebase.auth.signOut()
            val intent = Intent(this, JoinActiviy::class.java)
            startActivity(intent)
        }



        var a = 0//초기 리사이클러뷰 항목개수
        var b = 0//삭제후 리사이클러뷰 항목개수
        myBookmark.child(auth.currentUser?.uid.toString())//데이터 베이스에서 계정별로의 저장된 북마크 목록 불러와서 어뎁터에 실시간 최신화
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataModel in snapshot.children) {
                        val key = dataModel.key // 해당 데이터의 키값 읽어오기
                        keyModel.add(key.toString())
                        if (a != b) {
                            break

                        }
                        contentModel.add(dataModel.getValue(contentsModel::class.java)!!)


                    }
                    a = contentModel.size
                    b = a
                    rVadapter.notifyDataSetChanged()//데이터 변경 최신화 동기화해주기

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Bookmark", "DBerror")
                }

            })


        rVadapter.itemClick = object : RVadapter.ItemClick {
            override fun OnClick(view: View, position: Int) {
                val intent = Intent(baseContext, ViewActivity::class.java)
                intent.putExtra("URL", contentModel[position].url)
                intent.putExtra("title", contentModel[position].titleText)

                startActivity(intent)

            }

        }

        rVadapter.itemLongClick = object : RVadapter.OnItemLongClickListener {

            override fun onItemLongClick(view: View, position: Int) {

                myBookmark.child(auth.currentUser?.uid.toString()).child(keyModel[position])
                    .removeValue()
                keyModel.removeAt(position)
                Log.d("key", "남아있는 키값들 =====${keyModel}")
                contentModel.removeAt(position)

                Toast.makeText(baseContext, "${position + 1}번 삭제완료", Toast.LENGTH_SHORT).show()
                b = contentModel.size

                //Log.d("key","키값=====${keyModel[position]}")


            }

        }
        computeremailref.child(auth.currentUser?.uid.toString())//데이터 베이스에서 계정별로의 저장된 북마크 목록 불러와서 어뎁터에 실시간 최신화
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataModel in snapshot.children) {
                        val key = dataModel.key // 해당 데이터의 키값 읽어오기
                        emailkeyModel.add(key.toString())
                        if (a != b) {
                            break

                        }


                    }
                    a = contentModel.size
                    b = a
                    rVadapter.notifyDataSetChanged()//데이터 변경 최신화 동기화해주기

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Bookmark", "DBerror")
                }

            })


        val checkbox1 = findViewById<CheckBox>(R.id.hasungcheckBox)

        checkbox1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hansungemailref.child(auth.currentUser?.uid.toString()).push().setValue(userEmail)
                Toast.makeText(this@BookMark, "\n  한성대 공지 이메일 수신동의  ", Toast.LENGTH_SHORT).show()

            }
            // 체크박스가 체크되어 있을 때 수행할 작업
            else {
                hansungemailref.child(auth.currentUser?.uid.toString()).removeValue()

            }
        }


        val checkbox2 = findViewById<CheckBox>(R.id.computercheckBox)

        checkbox2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                computeremailref.child(auth.currentUser?.uid.toString()).push().setValue(userEmail)
                Toast.makeText(this@BookMark, "\n  컴공 공지 이메일 수신동의  ", Toast.LENGTH_SHORT).show()

            } else {
                computeremailref.child(auth.currentUser?.uid.toString()).removeValue()

            }
        }


        val checkbox3=findViewById<CheckBox>(R.id.discode)

        checkbox3.setOnCheckedChangeListener{_,isChecked->
            if(isChecked){
                val intent = Intent(baseContext, ViewActivity::class.java)
                intent.putExtra("URL", "https://discord.gg/XcAMJ7CXeV")
                startActivity(intent)

            }else{

            }

        }

 */
        val bottomnav=findViewById<BottomNavigationView>(R.id.bottommenu2)
        val TextView=findViewById<TextView>(R.id.textView)

        bottomnav.setOnNavigationItemSelectedListener(onBottomNavItemselect)


    }



    private val onBottomNavItemselect= BottomNavigationView.OnNavigationItemSelectedListener {
        val TextView=findViewById<TextView>(R.id.textView)


        when (it.itemId) {
            R.id.bookmark -> {
                BookmarksFragment= Bookmarks.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.FragmentcontainerView2,BookmarksFragment).commit()
                TextView.text="Bookmark"



            }
            R.id.settings -> {
                SettingsFragment= settings.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.FragmentcontainerView2,SettingsFragment).commit()
                TextView.text="Settings"

            }


        }
        true

    }


}

