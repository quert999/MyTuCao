package mainfrags

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mdjf.tucao.R
import com.orhanobut.logger.Logger
import com.trello.rxlifecycle2.components.support.RxFragment
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import mainfrags.adapter.MyRVAdapter
import web.RetrofitSingleton

class BangumiFragment : RxFragment() {

    lateinit var srl: SwipeRefreshLayout
    lateinit var rv: RecyclerView


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_recommend, container, false)
        srl = view?.findViewById(R.id.swipeRefresh) as SwipeRefreshLayout
        srl.setColorSchemeResources(R.color.colorPrimary)
        srl.isRefreshing = true
        rv = view.findViewById(R.id.recommendRecycler) as RecyclerView
        rv.layoutManager = LinearLayoutManager(activity)
        rv.adapter = MyRVAdapter()
        srl.setOnRefreshListener { loadData() }
        loadData()

        return view
    }

    fun loadData(){
        RetrofitSingleton.observeBangumi()
                ?.bindToLifecycle(this)
                ?.subscribe({
                    (rv.adapter as MyRVAdapter).bannerData.clear()
                    (rv.adapter as MyRVAdapter).bannerData.addAll(it.banners)
                    (rv.adapter as MyRVAdapter).recommendData.clear()
                    (rv.adapter as MyRVAdapter).recommendData.addAll(it.recommends)
                    rv.adapter.notifyDataSetChanged()
                    srl.isRefreshing = false
                }, {
                    e ->
                    srl.isRefreshing = false
                    Logger.d(e.toString() + "：" + e.message)
                })
    }
}