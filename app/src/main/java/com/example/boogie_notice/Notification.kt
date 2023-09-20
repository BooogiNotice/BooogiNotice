package com.example.boogie_notice

import android.Manifest
import android.app.*
import android.app.Notification
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Notification : Service() {
    private var notificationId = 2
    val database = Firebase.database
    private lateinit var auth: FirebaseAuth
    private val contentModel1 = mutableListOf<recentcontentsmedel>()
    private val contentModel2 = mutableListOf<recenturl>()
    private val contentModel3 = mutableListOf<recentcontentsmedel>()
    private val contentModel4 = mutableListOf<recenturl>()
    private val contentModel5 = mutableListOf<recentcontentsmedel>()

    private val channelID = "channelID"
    private val channelName = "boogienotice"

    private val computeralermtitle = mutableListOf<String>()
    private val hansungalermtitle = mutableListOf<String>()
    val dataList = mutableListOf<String>()

    private val comgongkeywordalermtitle = mutableListOf<String>()
    private val hansungkeywordalermtitle = mutableListOf<String>()
    val result = mutableListOf<String>()




    val Computer_Noticeref = database.getReference("Notice_List").child("Computer_Notice")

    val Computer_NoticeURLref = database.getReference("Notice_List").child("Computer_Notice")

    val Hansung_Noticeref = database.getReference("Notice_List").child("Hansung_Notice")

    val Hansung_NoticeURLref = database.getReference("Notice_List").child("Hansung_Notice")

    val Keyword_ref = database.getReference("keyword_ref")

    var contentModel1size = 0
    var contentModel3size= 0


    inner class LocalBinder : Binder() {
        fun getService() = this@Notification
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        auth = Firebase.auth

        // 백그라운드 서비스가 시작될 때 실행할 작업을 수행합니다.
        // 데이터베이스에서 데이터를 비동기적으로 가져옵니다.
        startForeground(1,createNotification("","https://www.naver.com/","").build())


        fetchDataFromFirebase()
        return START_REDELIVER_INTENT

        //return super.onStartCommand(intent, flags, startId)
    }

    var a = 0//초기 리사이클러뷰 항목개수
    var b = 0//삭제후 리사이클러뷰 항목개수
    var c=0
    var d=0


    private fun fetchDataFromFirebase2() {
        Keyword_ref.child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener{  //keyword_ref에 있는 키워드를 dataList라는 배열에다가 가져다둠.
                override fun onDataChange(snapshot: DataSnapshot) {

                    for(childsnapshot in snapshot.children){
                        dataList.add(childsnapshot.value.toString())

                    }
                    Log.d("keyword", dataList.toString())



                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })




    }
        private fun fetchDataFromFirebase() {




        Computer_NoticeURLref.child("ID_URL")//URL 가져오기
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataModel in snapshot.children) {
                        val key = dataModel.key // 해당 데이터의 키값 읽어오기

                        val model=recenturl(key.toString(),dataModel.value.toString())

                        contentModel2.add(model)
                    }
                    Log.d("NewURL", "새로운 컴공 URL 갯수 : "+contentModel2.size.toString())
                    c=contentModel2.size

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Bookmark", "DBerror")
                }
            })
        Hansung_NoticeURLref.child("ID_URL")//URL 가져오기
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataModel in snapshot.children) {
                        val key = dataModel.key // 해당 데이터의 키값 읽어오기

                        val model=recenturl(key.toString(),dataModel.value.toString())
                        contentModel4.add(model)
                    }

                    Log.d("NewURL", "새로운 한성대 url 갯수 : "+contentModel4.size.toString())
                    d=contentModel4.size

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Bookmark", "DBerror")
                }
            })


        Computer_Noticeref.child("ID_Title")//데이터 베이스에서 계정별로의 저장된 북마크 목록 불러와서 어뎁터에 실시간 최신화
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(5000) // 5초 딜레이



                        for (dataModel in snapshot.children) {

                           // computeralermtitle.add(dataModel.value.toString())

                            val key = dataModel.key // 해당 데이터의 키값 읽어오기

                            val model = recentcontentsmedel(key.toString(), dataModel.value.toString())

                            contentModel1.add(model)

                        }

                        contentModel1size = contentModel1.size//새로읽어온 공지사항개수
                        Log.d("새로받아온 컴공 공지 갯수 ", contentModel1size.toString() + "--" + a.toString())

                        if (contentModel1size > a && a != 0 && contentModel2.size != 0) {
                            //전에 가져온공지사항개수보다 그다음 읽어온 공지사항개수가 1개라도 더많을시에 그리고 초기값이 0이면 맨처음 루프이므로 한번건너뛰어야하므로 0이아닐시에 알림 올림
                            Log.d("URL 포문을 돌수있는 개수 ", contentModel2.size.toString())

                                Log.d("비교할 공지사항 제목 :  ", contentModel1[contentModel1.size-1].titleText)
                            fetchDataFromFirebase2()

                            for(j in 0 until dataList.size){
                                    Log.d("비교할 키워드  ",dataList[j])

                                    if(contentModel1[contentModel1.size-1].titleText.contains(dataList[j])) {
                                        Log.d("키워드 같은가????:  ", contentModel5[contentModel1.size-1].titleText+" == "+dataList[j])

                                        contentModel5.add(contentModel1[contentModel1.size-1])
                                        Log.d("키워드 매칭 성공한 공지제목 :  ", contentModel5[contentModel1.size-1].titleText)

                                    }
                                }


                            //contentModel1.forEach { title -> dataList.forEach{ keyword->
                             //  if(title.contains(keyword)){result.add(title)} }}

                            if(contentModel5.size>0){

                                for(i in 0 until contentModel5.size) {

                                    Log.d("resultkeyword", contentModel5[i].titleText)
                                }
                                for (i in 0 until contentModel5.size) {

                                    if (contentModel5[contentModel5.size - 1].key == contentModel2[i].key) {// 제목키값에 일치하는 키값을가진URL 찾기
                                        updateNotification(createNotification("컴공 키워드 새로운 공지",
                                            contentModel2[i].url,
                                            contentModel5[contentModel5.size-1].titleText
                                            //computeralermtitle[computeralermtitle.size - 1]
                                        ).build())

                                    }
                                }


                                }
                            else{

                                for (i in 0 until contentModel2.size) {
                                    Log.d(
                                        "새로받아온 컴공 공지 제목키값과 URL 키값비교 :  ",
                                        contentModel1[contentModel1.size - 1].key.toString() + "=같은가??=" + contentModel2[i].key.toString()
                                    )
                                    if (contentModel1[contentModel1.size - 1].key == contentModel2[i].key) {// 제목키값에 일치하는 키값을가진URL 찾기
                                        updateNotification(createNotification("컴공 새로운 공지",
                                            contentModel2[i].url,
                                            contentModel1[contentModel1.size-1].titleText
                                            //computeralermtitle[computeralermtitle.size - 1]
                                        ).build()
                                        )
                                        Log.d("새로받아온 컴공 공지 갯수 ", contentModel1size.toString())

                                    }

                                }

                            }



                        }
                        a = contentModel1size//처음 공지사항갯수


                        for (i in 0 until contentModel1.size) {//상자 비우기
                            contentModel1.removeAt(0) // Remove the element at index 0

                        }
                        for (i in 0 until contentModel5.size) {//상자 비우기
                            contentModel5.removeAt(0) // Remove the element at index 0

                        }
/*
                        for (i in 0 until computeralermtitle.size) {
                            computeralermtitle.removeAt(0) // Remove the element at index 0

                        }


 */

                        for (i in 0 until contentModel2.size) {
                            contentModel2.removeAt(0) // Remove the element at index 0

                        }

                    }

                    for (i in 0 until dataList.size) {//상자 비우기
                        dataList.removeAt(0) // Remove the element at index 0

                    }
                }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Bookmark", "DBerror")
                    }

            })





        Hansung_Noticeref.child("ID_Title")//데이터 베이스에서 계정별로의 저장된 북마크 목록 불러와서 어뎁터에 실시간 최신화
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(5000) // 5초 딜레이
                        for (dataModel in snapshot.children) {
                            hansungalermtitle.add(dataModel.value.toString())
                            val key = dataModel.key // 해당 데이터의 키값 읽어오기

                            val model = recentcontentsmedel(key.toString(), dataModel.value.toString())

                            contentModel3.add(model)

                        }
                        contentModel3size = contentModel3.size

                        if (contentModel3size > b && b != 0) {//전보다 하나더 추가되었는지
                            for (i in 0 until contentModel4.size) {
                                Log.d(
                                    "새로받아온 한성대 공지 제목키값과 URL 키값비교 :  ",
                                    contentModel3[contentModel3.size - 1].key.toString() + "=같은가??=" + contentModel4[i].key.toString()
                                )
                                if (contentModel3[contentModel3.size - 1].key == contentModel4[i].key) {// 제목키값에 일치하는 키값을가진URL 찾기
                                        updateNotification(createNotification("한성대 새로운 공지", contentModel4[i].url, hansungalermtitle[hansungalermtitle.size - 1]
                                        ).build()
                                    )
                                    Log.d("새로받아온  한성대 공지 갯수 ", contentModel3size.toString())


                                }
                            }
                        }
                        b = contentModel3size

                        for (i in 0 until contentModel3.size) {//상자 비우기
                            contentModel3.removeAt(0) // Remove the element at index 0

                        }


                        for (i in 0 until hansungalermtitle.size) {
                            hansungalermtitle.removeAt(0)
                        }

                        for (i in 0 until contentModel4.size) {
                            contentModel4.removeAt(0) // Remove the element at index 0

                        }


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Bookmark", "DBerror")
                }

            })
    }

    override fun onCreate() {
        super.onCreate()
        // 백그라운드 서비스가 생성될 때 실행할 초기화 작업을 수행합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // 백그라운드 서비스가 종료될 때 실행할 정리 작업을 수행합니다.
    }

    private fun createChannel() {


        val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description="description text of this boogienotice"
        channel.enableVibration(true)
        channel.enableLights(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val notificationmng=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationmng.createNotificationChannel(channel)


    }

    private fun updateNotification(notification: Notification){
        val uniqueId = notificationId++

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            NotificationManagerCompat.from(this).notify(uniqueId, notification)
            Log.d("알림 아이디 증가여부 : ",uniqueId.toString())
            //startForeground(uniqueId,notification)

        }
    }







    fun createNotification(title:String,URL:String,text:String) : NotificationCompat.Builder{
        val intent = Intent(this@Notification, ViewActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP  or Intent.FLAG_ACTIVITY_SINGLE_TOP
        Log.d("접속할 알림의 URL",URL)
        intent.putExtra("URL", URL)
        val pendingIntent = with(TaskStackBuilder.create(this)){
            addNextIntentWithParentStack(intent)
            getPendingIntent(0,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)

        }

        val builer=NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.img_1)// 작은 아이콘 설정
            .setContentText(text)
            .setContentTitle(title)
            .setContentIntent(pendingIntent) // PendingIntent 설정
            .setAutoCancel(true)



        return builer
    }

}