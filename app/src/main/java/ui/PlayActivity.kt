package ui

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Toast
import bean.CommonData
import com.bumptech.glide.Glide
import com.dl7.player.media.IjkPlayerView
import com.google.gson.Gson
import com.mdjf.tucao.R
import com.orhanobut.logger.Logger
import mainfrags.videoPullParseXml
import web.RetrofitSingleton


class PlayActivity : AppCompatActivity() {
    lateinit var player: IjkPlayerView
    lateinit var hid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        hid = intent.getStringExtra("hid")


        findViewById(R.id.animView).startAnimation(AnimationUtils.loadAnimation(this,R.anim.test))

        player = findViewById(R.id.player) as IjkPlayerView
        player.enableDanmaku()

        RetrofitSingleton.observePlayDetail(hid)?.subscribe({
            val json = it.string()
            Logger.d("获取到view信息是：\n$json")
            //数据返回如下，需要判断code是否等于200
            val commonData = Gson().fromJson(json,CommonData::class.java)
            Glide.with(this).load(commonData.result.thumb).into(player.mPlayerThumb)

//            "video":[{"type":"189","vid":"2147111588282758","title":"『4月』不正经的魔术讲师与禁忌教典 11『动漫国』"}]
//            <?xml version="1.0" encoding="UTF-8"?><video><result>succ</result><timelength>0</timelength><src>30</src></video>
            RetrofitSingleton.observePlayUrl(commonData.result.video[0].type,commonData.result.video[0].vid)
                    ?.subscribe({
                        val str = it.string()
                        val video = videoPullParseXml(str,this@PlayActivity)
                        player.init().setVideoPath(video?.durls?.get(0)?.url)
                        Toast.makeText(this,"播放地址已就绪",Toast.LENGTH_LONG).show()
                    },{
                        e ->
                        Logger.d(e.toString() + "：" + e.message)
                    })
        },{
            e ->
            Logger.d(e.toString() + "：" + e.message)
        })

    }

    override fun onResume() {
        super.onResume()
        player.onResume()
    }

    override fun onPause() {
        super.onPause()
        player.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        player.configurationChanged(newConfig)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (player.handleVolumeKey(keyCode)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (player.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

}
