package web

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by Administrator on 2017/6/7.
 */

interface ApiService {
    @GET("/")
    @Headers("Cookie: tucao_verify=ok")
    fun index() : Observable<ResponseBody>

    @GET("list/{tid}/")
    @Headers("Cookie: tucao_verify=ok")
    fun list(@Path("tid") tid: String) : Observable<ResponseBody>

    @GET("api_v2/view.php?apikey=${RetrofitSingleton.API_KEY}&type=json")
    fun playDetail(@Query("hid") hid: String): Observable<ResponseBody>

    @GET("api/playurl?apikey=${RetrofitSingleton.API_KEY}")
    fun playUrl(@Query("type")type: String,@Query("vid")vid: String): Observable<ResponseBody>
}
