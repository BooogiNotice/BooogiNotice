package com.example.boogie_notice

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(){
    private lateinit var hansungFragment: Hansung
    private lateinit var computerFragment: Comgong
    private val items= mutableListOf<contentsModel>()
    private var isdouble=false
    override fun onBackPressed() {

        if(isdouble==true){
            finish()
        }

        Toast.makeText(this," 종료하시려면 더블클릭 ", Toast.LENGTH_SHORT).show()
        isdouble=true

        Handler().postDelayed(Runnable {
            isdouble=false
        },2000)
    }

    private fun requestSinglePermission(permission: String) { // 한번에 하나의 권한만 요청하는 예제
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) // 권한 유무 확인
            return
        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { // 권한 요청 컨트랙트
                isGranted: Boolean ->
            if(isGranted){

            }

        }
        if (shouldShowRequestPermissionRationale(permission)) { // 권한 설명 필수 여부 확인
// you should explain the reason why this app needs the permission.

            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage(getString(R.string.req_permission_reason, permission))
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(permission) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
            Log.d("알림 동의함 ",requestPermLauncher.launch(permission).toString())

        } else {
// should be called in onCreate()
            requestPermLauncher.launch(permission) // 권한 요청 시작
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val intentnoti = Intent(this, Notification::class.java)
        startForegroundService(intentnoti)


        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val isServiceStarted = sharedPreferences.getBoolean("isServiceStarted", false)

        if (!isServiceStarted) {
            val intentnoti = Intent(this, Notification::class.java)
            startForegroundService(intentnoti)

            // 서비스가 시작되었음을 저장
            val editor = sharedPreferences.edit()
            editor.putBoolean("isServiceStarted", true)
            editor.apply()
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestSinglePermission(Manifest.permission.POST_NOTIFICATIONS)
        }



        val logout=findViewById<TextView>(R.id.logoutBTN)
        logout.setOnClickListener{
            Firebase.auth.signOut()
            val intent=Intent(this, JoinActiviy::class.java)
            Toast.makeText(this," 다시 로그인 해주세요 ", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }





        items.add(
            contentsModel("https://www.hansung.ac.kr/hansung/8385/subview.do",
                "한성대학교 공지사항")
        )
        items.add(
            contentsModel("http://cse.hansung.ac.kr/news?searchCondition=%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD",
                "컴퓨터공학부")
        )



        val bottomnav=findViewById<BottomNavigationView>(R.id.bottommenu)

        bottomnav.setOnNavigationItemSelectedListener(onBottomNavItemselect)
    }



    private val onBottomNavItemselect=BottomNavigationView.OnNavigationItemSelectedListener {

        when (it.itemId) {
            R.id.hansungnoti -> {
                hansungFragment= Hansung.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,hansungFragment).commit()



            }
            R.id.computersicence -> {
                computerFragment= Comgong.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,computerFragment).commit()

            }

            R.id.mypage -> {


                intent=Intent(this, BookMark::class.java)
                startActivity(intent)


            }
        }
        true

    }

}
