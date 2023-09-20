package com.example.boogie_notice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Bookmarks: Fragment(R.layout.fragment_bookmarks) {
        private lateinit var auth: FirebaseAuth


    companion object{
        fun newInstance(): Bookmarks {
            return Bookmarks()
        }

    }

    private val contentModel = mutableListOf<contentsModel>()
    private val keyModel = mutableListOf<String>()
    private val emailkeyModel = mutableListOf<String>()
    private lateinit var notificationHelper1:Notification

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        val database = Firebase.database
        val myBookmark = database.getReference("bookmark_ref")
        val computeremailref = database.getReference("computeremailref")

        val rVadapter = RVadapter(contentModel)
        val recyclerView = view.findViewById<RecyclerView>(R.id.bookmarksrv)
        auth = Firebase.auth


        recyclerView.adapter = rVadapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.setHasFixedSize(true)
       val userEmail = auth.currentUser!!.getEmail().toString()


        val currentloginemail = view.findViewById<TextView>(R.id.user)
        val username = userEmail.substringBefore('@')

        currentloginemail.text = "${username}님"



        notificationHelper1 = Notification()





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
                val intent = Intent(context, ViewActivity::class.java)
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

                Toast.makeText(context, "${position + 1}번 삭제완료", Toast.LENGTH_SHORT).show()
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

    }
}