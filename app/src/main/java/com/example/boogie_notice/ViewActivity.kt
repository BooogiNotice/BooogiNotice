package com.example.boogie_notice

import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class ViewActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUrl: String // 현재 웹 페이지의 주소를 저장하는 변수

    val database = Firebase.database
    val myBookmark = database.getReference("bookmark_ref")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        auth = Firebase.auth
        val title = intent.getStringExtra("title").toString()

        val webView = findViewById<WebView>(R.id.webView)
        val cookieManager: CookieManager = CookieManager.getInstance()


        webView.settings.javaScriptEnabled = true // JavaScript를 사용할 수 있도록 설정
        webView.settings.setSupportMultipleWindows(true)//WebView의 설정을 변경하여 다중 창 지원을 활성화하는 역할
        webView.settings.javaScriptCanOpenWindowsAutomatically = true//WebView 내에서 자바스크립트로 인해 자동으로 새 창이 열리도록 허용
        webView.settings.builtInZoomControls = true//확대 축소하기 가능
        webView.webChromeClient

        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        cookieManager.setAcceptCookie(true)

        val URL = intent.getStringExtra("URL").toString()
        val notiURL = intent.getStringExtra("notiURL")

        if (URL!=null) {
            Log.d("Url 제대로 받음 1 : ", URL)
            webView.loadUrl(URL)

        }







       // webView.loadUrl(intent.getStringExtra("notifiURL").toString())

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

        var crawtitle:String=""
        val savebtn=findViewById<TextView>(R.id.save)
        savebtn.setOnClickListener{
            val url = currentUrl


            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val docs = Jsoup.connect(url).get()
                    withContext(Dispatchers.Main) {
                        // UI 작업을 여기서 처리합니다 (예: 출력 등)

                        if(title=="한성대학교 공지사항"){
                            crawtitle=docs.select("h2.view-title").text()



                        }else if(title=="컴퓨터공학부"){
                            crawtitle=docs.select("h2").text()


                        }

                        myBookmark.child(auth.currentUser!!.uid).push().setValue(contentsModel(currentUrl,crawtitle))



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