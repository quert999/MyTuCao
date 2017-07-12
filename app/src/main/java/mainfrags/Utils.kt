package mainfrags

import android.content.Context
import android.util.Xml
import bean.Durl
import bean.Video
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

fun <T> Observable<ResponseBody>.sanitizeHtml(transform: Document.() -> T): Observable<T> = this
        .map {
            response ->
            Jsoup.parse(response.string()).transform()
        }
        .observeOn(AndroidSchedulers.mainThread())

fun videoPullParseXml(str: String, context: Context): Video? {
    var video: Video? = null
    var durl: Durl? = null
    val xmlParser = Xml.newPullParser()
    try {
        xmlParser.setInput(str.byteInputStream(charset("utf-8")), "utf-8")
        //获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
        var evtType = xmlParser.eventType
        //一直循环，直到文档结束
        while (evtType != XmlPullParser.END_DOCUMENT) {
            when (evtType) {
                XmlPullParser.START_TAG -> {
                    val tag = xmlParser.name
                    if (tag == "video") {
                        video = Video(null, null, mutableListOf<Durl>())
                    } else if (video != null) {
                        if (tag != "durl" && durl == null){
                            if (tag == "result") video.result = xmlParser.nextText()
                            if (tag == "timelength") video.timeLength = xmlParser.nextText().toLong()
                        }else{
                            if (tag == "durl"){
                                durl = Durl(null,null,null)
                            }else if (durl != null){
                                if (tag == "order") durl.order = xmlParser.nextText().toInt()
                                if (tag == "length") durl.length = xmlParser.nextText().toInt()
                                if (tag == "url") durl.url = xmlParser.nextText()
                            }
                        }
                    }
                }


                XmlPullParser.END_TAG ->
                    if (xmlParser.name == "video" && video != null) {
                        //此时已经读取到video
                    }else if (xmlParser.name == "durl" && durl != null){
                        video?.durls?.add(durl)
                        durl = null
                    }
                else -> {
                }
            }
            //如果xml没有结束，则导航到下一个river节点
            evtType = xmlParser.next()
        }
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
    } catch (e1: IOException) {
        e1.printStackTrace()
    }
    return video
}