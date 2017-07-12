package web

import bean.*
import com.mdjf.tucao.BuildConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mainfrags.sanitizeHtml
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Administrator on 2017/6/7.
 */
object RetrofitSingleton {
    const val API_KEY = "25tids8f1ew1821ed"


    private var sApiService: ApiService? = null
    private var sApiService1: ApiService? = null
    private var sRetrofit: Retrofit? = null
    private var sRetrofit1: Retrofit? = null
    private var sOkHttpClient: OkHttpClient? = null

    init {
        initOkHttp()
        initRetrofit()
        sApiService = sRetrofit?.create(ApiService::class.java)
        sApiService1 = sRetrofit1?.create(ApiService::class.java)
    }

    private fun initRetrofit() {
        sRetrofit = Retrofit.Builder()
                .baseUrl("http://www.tucao.tv/")
                .client(sOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        sRetrofit1 = Retrofit.Builder()
                .baseUrl("http://api.tucao.tv/")
                .client(sOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    private fun initOkHttp() {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            // https://drakeet.me/retrofit-2-0-okhttp-3-0-config
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
            builder.addInterceptor(loggingInterceptor)
        }
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(20, TimeUnit.SECONDS)
        builder.writeTimeout(20, TimeUnit.SECONDS)
        //错误重连
        builder.retryOnConnectionFailure(true)
        sOkHttpClient = builder.build()
    }

    fun observeRecommend(): Observable<Index>? {
        return sApiService?.index()
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.sanitizeHtml {
                    val banners = parseRecBanner(this)
                    val recommends = parseRecRecommend(this)
                    Index(banners,recommends)
                }
    }

    val HID_PATTERN = "/play/h([0-9]+)/".toRegex()
    val SINGLE_ID_PATTERN = "/list/([0-9]+)/".toRegex()

    private fun parseRecBanner(doc: Document): MutableList<Banner> {
        val banners = mutableListOf<Banner>()
        val index_pos9 = doc.select("div.index_pos9").first()
        val ul = index_pos9.child(1)
        ul.children().forEach {
            // <li><a href="http://www.tucao.tv/play/h4070217/" target="_blank"><img src="http://www.tucao.tv/uploadfile/2017/0102/thumb_296_190_20170102034446261.jpg" alt="【合集】 FLIP FLAPPERS 01~13话【SweetSub&amp;LoliHouse】"><p>对你而言，世界是怎样的呢——。获得了打开大门的钥匙的两位女主人公，帕皮卡和可可娜。少~~</p><b>【合集】 FLIP FLAPPERS 01~13话【SweetSub&amp;LoliHouse】</b><i class="time">--:--</i></a></li>
            val aElement = it.child(0)
            val linkUrl = aElement.attr("href")
            val imgElement = aElement.child(0)
            val imgUrl = imgElement.attr("src")
            val hid: String? = HID_PATTERN.find(linkUrl)?.groupValues?.get(1)
            banners.add(Banner(imgUrl, linkUrl, hid))
        }

        return banners
    }


    private fun parseRecRecommend(doc: Document): List<Pair<Channel,List<Result>>>{
        val returnList = mutableListOf<Pair<Channel,List<Result>>>()
        val channelList = mutableListOf<Channel>()
        val title_red = doc.select("h2.title_red").takeLast(5)
        title_red.forEach {
            val channel: Channel = Channel(SINGLE_ID_PATTERN.find(it.child(1).attr("href"))!!.groupValues[1].toInt(),it.child(1).text())
            channelList.add(channel)
        }
        val resultsList = mutableListOf<List<Result>>()
        val lists_tip = doc.select("div.lists.tip").takeLast(5)
        lists_tip.forEach {
            val result = it.child(0).children().filter {
                it is Element
            }.map {
                it.child(0)         //<liclass="new_l2">
            }.fold(mutableListOf<Result>()){
                total,aElement      ->
                val hid: String = HID_PATTERN.find(aElement.attr("href"))!!.groupValues[1]
                val imgUrl = aElement.child(0).attr("src")
                val title = aElement.child(1).text()
                val play = aElement.child(2).text().replace(",","").toInt()
                total.add(Result(hid = hid,title = title,thumb = imgUrl,play = play))
                total
            }
            resultsList.add(result)
        }
        for (index in channelList.indices){
            returnList.add(channelList[index] to resultsList[index])
        }
        return returnList
    }

    fun observeBangumi(): Observable<Bangumi>? {
        return sApiService?.list("24")
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.sanitizeHtml {
                    val banners = parseBangumiBanner(this)
                    val recommends = parseBangumiRecommend(this)
                    Bangumi(banners, recommends)
                }
    }


    fun parseBangumiBanner(doc: Document): MutableList<Banner>{
        val returnList = mutableListOf<Banner>()
        doc.select("div.newcatfoucs").first().child(0).children()
                .forEach {
                    // <a href="http://www.tucao.tv/play/h4070393/" target="_blank" style="display: none;"><img src="http://www.tucao.tv/uploadfile/2017/0117/20170117080016696.png" alt="【1月】废天使加百列/珈百璃的堕落 02【动漫国】"><div class="title">【1月】废天使加百列/珈百璃的堕落 02【动漫国】</div></a>

                    val linkUrl = it.attr("href")
                    val title = it.child(1).text()
                    val imgUrl = it.child(0).attr("src")
                    val id = HID_PATTERN.find(linkUrl)!!.groupValues[1]
                    returnList.add(Banner(imgUrl = imgUrl,hid = id,title = title,linkUrl = linkUrl))
                }
        return returnList
    }

    fun parseBangumiRecommend(doc: Document): List<Pair<Channel,List<Result>>>{
        val returnList = mutableListOf<Pair<Channel,List<Result>>>()
        val channels = mutableListOf<Channel>()
        val resultList = mutableListOf<List<Result>>()
        val title_red = doc.select("h2.title_red").takeLast(4)
        val lists_tip = doc.select("div.lists.tip").takeLast(4)
        title_red.forEach {
            val linkUrl = it.child(0).child(0).attr("href")
            val id = SINGLE_ID_PATTERN.find(linkUrl)!!.groupValues[1].toInt()
            val channel = Channel.find(id)!!
            channels.add(channel)
        }
        lists_tip.forEach {
            val ul = it.child(0)
            val singleResult = ul.children()
                    .fold(mutableListOf<Result>()){
                        total,element       ->
                        // element格式 <a href="http://www.tucao.tv/play/h4072689/" target="_blank">
                        // <img src="http://www.tucao.tv/uploadfile/2017/0611/thumb_140_90_20170611115640511.gif" alt="【4月】剑姬神圣谭  09话" desc="道歉的时候要露出胸部！这不是常识吗？喵森的脑洞小剧场萌死了 你一边妒忌松冈一…" user="林小萌" update="2017-06-11 23:57:20">
                        // <b>【4月】剑姬神圣谭 09话 </b>
                        // <em class="play">5,657</em><em class="tm">--</em><span></span><p class="info">(<i class="comment">0</i>/<i class="fav"></i>)</p></a>
                        val href = element.child(0)

                        val hid = HID_PATTERN.find(href.attr("href"))!!.groupValues[1]
                        val imgUrl = href.child(0).attr("src")
                        val title = href.child(1).text()
                        val play = href.child(2).text().replace(",","").toInt()
                        total.add(Result(hid = hid,title = title,thumb = imgUrl,play = play))
                        total
                    }
            resultList.add(singleResult)
        }
        for (index in channels.indices){
            returnList.add(channels[index] to resultList[index])
        }
        return returnList
    }


    fun observePlayDetail(hid: String): Observable<ResponseBody>?{
        return sApiService?.playDetail(hid)
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
    }


    fun observePlayUrl(type: String,vid: String): Observable<ResponseBody>?{
        return sApiService1?.playUrl(type,vid)
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
    }



}