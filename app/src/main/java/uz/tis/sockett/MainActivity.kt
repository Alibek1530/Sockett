package uz.tis.sockett

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.security.cert.X509Certificate
import java.text.DateFormat
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class MainActivity : AppCompatActivity() {

    lateinit var mSocket: Socket

    lateinit var btn: Button
    lateinit var btn1: Button
    lateinit var btnsms: Button
    lateinit var txt: TextView

    var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.btn)
        btn1 = findViewById(R.id.btn1)
        btnsms = findViewById(R.id.btnsms)
        txt = findViewById(R.id.text)
        setSocket()
        getListen()
    }

    fun setSocket() {
        val myHostnameVerifier = HostnameVerifier { _, _ ->
            return@HostnameVerifier true
        }

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, null)

        val okHttpClient = OkHttpClient.Builder()
            .hostnameVerifier(myHostnameVerifier)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .build()

        val options = IO.Options()
        options.path = "/socket.io/"
        options.forceNew = true
        options.callFactory = okHttpClient
        options.webSocketFactory = okHttpClient
        options.reconnection = true

        mSocket = IO.socket("https://juft.albison.software", options)
        mSocket.connect()

        Toast.makeText(this, "aa    ${mSocket.connected()}", Toast.LENGTH_SHORT).show()

        btn.setOnClickListener {
            Toast.makeText(this, "con:     ${mSocket.connected()}", Toast.LENGTH_SHORT).show()
            mSocket.emit("enter-chat-list",EntirChatList("61345c28ede9b08c54feb75e"))
        }
        btn1.setOnClickListener {

//
            var aa = EntirChatRoom("61345c28ede9b08c54feb75e", "61345ee6ede9b08c54feb7ec")
            Toast.makeText(this, "con:     ${mSocket.connected()}", Toast.LENGTH_SHORT).show()
            mSocket.emit(
                "enter-chat-room",
                aa
            )
        }
//
        btnsms.setOnClickListener {
            Toast.makeText(this, "con:     ${mSocket.connected()}", Toast.LENGTH_SHORT).show()

            Log.d("ppo", "setSocket: ${Date()}")
            var data = AddMessage(
                "Salom Alibekasa",
                Date(), "61345c28ede9b08c54feb75e", "61345ee6ede9b08c54feb7ec"
            )



            mSocket.emit("add-message", data)

            //  mSocket.emit("add-message","Salom Alibek","2010-12-12'T'21:16:16.000'Z'","613449faede9b08c54feb66a","614037c88bba2f7721f52522")
        }


        //614037c88bba2f7721f52522  room ALI ichi
        //613449faede9b08c54feb66a  alibekninh idsi

        //614037c88bba2f7721f52522  room sabinani ichi
        //613c4d928f40b0384530cf5f  sabinaning idsi
    }


    fun getListen() {
        mSocket.on("room-created", Emitter.Listener {
            // var room1=it as RoomCreated
            Log.i("AAAAAA", "setSocket: " + it)
            runOnUiThread {
                var a = it[0] as JSONObject
                // val data = it.get(0)
                txt.text = a.toString()
                Toast.makeText(this, "sasa ${a}", Toast.LENGTH_SHORT).show()
            }
        })


        mSocket.on("update-chat-list", Emitter.Listener {
            Log.i("AAAAAA", "setSocket:2 " + it)
            runOnUiThread {
                //   val data = it.get(0)
                var a = it[0] as JSONObject
                txt.text = a.toString()
                Toast.makeText(this, "sasa2 ${a}", Toast.LENGTH_SHORT).show()
            }
        })

        mSocket.on("new-message", Emitter.Listener {
            Log.i("AAAAAA", "setSocket:3 " + it)

            runOnUiThread {
                var a = it[0] as JSONObject
                // val data = it.get(0)
                txt.text = a.toString()
                Toast.makeText(this, "sasa3 ${a}", Toast.LENGTH_SHORT).show()
            }
        })

    }


}