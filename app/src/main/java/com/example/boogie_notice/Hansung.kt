package com.example.boogie_notice

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.jsoup.Jsoup




class Hansung : Fragment(R.layout.fragment_hansung) {

    companion object{
        fun newInstance(): Hansung {
            return Hansung()
        }

    }

    private lateinit var auth: FirebaseAuth

    private lateinit var currentUrl: String // 현재 웹 페이지의 주소를 저장하는 변수

    val database = Firebase.database
    val myBookmark = database.getReference("bookmark_ref")
    val Computer_Noticeref = database.getReference("Notice_List").child("Hansung_Notice")



    val cookieManager: CookieManager = CookieManager.getInstance()





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth


        val webView=view.findViewById<WebView>(R.id.hansungwebView)

        webView!!.settings.javaScriptEnabled = true // JavaScript를 사용할 수 있도록 설정
        webView.settings.setSupportMultipleWindows(true)//WebView의 설정을 변경하여 다중 창 지원을 활성화하는 역할
        webView.settings.javaScriptCanOpenWindowsAutomatically=true//WebView 내에서 자바스크립트로 인해 자동으로 새 창이 열리도록 허용
        webView.settings.builtInZoomControls=true//확대 축소하기 가능
        webView.webChromeClient

        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true

        webView.webViewClient = object : WebViewClient() {//웹뷰안에서 페이지 이동 시에도 유지시켜줌
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {//WebView 안에서 페이지가 로드될 때 호출되는 메서드
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

            webView.clearCache(true)

            view?.loadUrl(url.toString())
            return true
        }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // 페이지가 로드된 후의 실제 주소를 가져옴
                currentUrl = view.url.toString()
                // currentUrl을 이용하여 원하는 처리 수행
            }
        }


        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                // 페이지 로딩 진행 상태를 추적
                if (newProgress == 100) {
                    // 페이지 로딩 완료 후, 실제 주소를 가져옴
                    currentUrl = view.url.toString()
                    // currentUrl을 이용하여 원하는 처리 수행
                }
            }
        }
        webView!!.loadUrl("https://www.hansung.ac.kr/hansung/8385/subview.do")


        cookieManager.setAcceptCookie(true)
        var crawtitle:String=""
        val savebtn=view.findViewById<TextView>(R.id.save)
        savebtn.setOnClickListener{




            val url = currentUrl

            CoroutineScope(Dispatchers.IO).apply {
                launch {
                    try {
                        val docs = Jsoup.connect(url).get()
                        withContext(Dispatchers.Main) {
                            // UI 작업을 여기서 처리합니다 (예: 출력 등)


                            crawtitle=docs.select("h2.view-title").text()

                            myBookmark.child(auth.currentUser!!.uid).push().setValue(contentsModel(currentUrl,crawtitle))
                            Computer_Noticeref.child("ID_Title").push().setValue(crawtitle)

                            Toast.makeText(context,crawtitle+"\n  !!저장 완료!!  ",Toast.LENGTH_SHORT).show()


                            println(crawtitle)
                        }
                    } catch (e: Exception) {
                        // 에러 처리
                        e.printStackTrace()
                    }

                }
            }

        }




    }



}
