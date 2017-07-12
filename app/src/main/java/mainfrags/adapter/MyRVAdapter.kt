package mainfrags.adapter

import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import bean.Banner
import bean.Channel
import bean.Result
import com.bigkoo.convenientbanner.ConvenientBanner
import com.bumptech.glide.Glide
import com.mdjf.tucao.R
import ui.PlayActivity

class MyRVAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val bannerData: MutableList<Banner> = mutableListOf()
    val recommendData: MutableList<Pair<Channel, List<Result>>> = mutableListOf()
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, positionTemp: Int) {
        val context = holder?.itemView?.context

        if (holder is MyHolder) {
            var position = positionTemp
            if (bannerData.size > 0) position--
            val myHolder = holder
            myHolder.topTitle.text = recommendData[position].first.name
            recommendData[position].second.forEachIndexed {
                index, result ->
                val onClickListener = object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        val intent = Intent(context, PlayActivity::class.java)
                        intent.putExtra("hid", result.hid)
                        context?.startActivity(intent)
                    }
                }
                when (index) {
                    0 -> {
                        Glide.with(context).load(result.thumb)
                                .into(myHolder.itemImg1)
                        myHolder.itemTitle1.text = result.title
                        myHolder.playTimes1.text = result.play.toString()
                        myHolder.card1.setOnClickListener(onClickListener)
                    }
                    1 -> {
                        Glide.with(context).load(result.thumb)
                                .into(myHolder.itemImg2)
                        myHolder.itemTitle2.text = result.title
                        myHolder.playTimes2.text = result.play.toString()
                        myHolder.card2.setOnClickListener(onClickListener)
                    }
                    2 -> {
                        Glide.with(context).load(result.thumb)
                                .into(myHolder.itemImg3)
                        myHolder.itemTitle3.text = result.title
                        myHolder.playTimes3.text = result.play.toString()
                        myHolder.card3.setOnClickListener(onClickListener)
                    }
                    3 -> {
                        Glide.with(context).load(result.thumb)
                                .into(myHolder.itemImg4)
                        myHolder.itemTitle4.text = result.title
                        myHolder.playTimes4.text = result.play.toString()
                        myHolder.card4.setOnClickListener(onClickListener)
                    }
                }
            }
            for (index in recommendData[position].second.size..3) {
                when (index) {
                    0 -> {
                        myHolder.ll1.visibility = View.GONE
                        myHolder.card1.visibility = View.INVISIBLE
                    }
                    1 -> {
                        myHolder.card2.visibility = View.INVISIBLE
                    }
                    2 -> {
                        myHolder.ll2.visibility = View.GONE
                        myHolder.card3.visibility = View.INVISIBLE
                    }
                    3 -> {
                        myHolder.card4.visibility = View.INVISIBLE
                    }
                }
            }
            myHolder.loadMore.text = "加载更多${recommendData[position].first.name}内容"
        }


        if (holder is BannerHolder){
            holder.banner.setPages({ BannerHolder() }, bannerData)
                    .setPageIndicator(intArrayOf(R.drawable.indicator_white_circle, R.drawable.indicator_pink_circle))
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .startTurning(3000)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==11)
            return MyHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_recommend, parent, false))
        else
            return BannerHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_header_layout, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        //10  -- header-item
        //11  -- normal-item
        if (bannerData.size > 0){
            if (position == 0)
                return 10
            else
                return 11
        }else{
            return 11
        }
    }

    override fun getItemCount(): Int {
        return recommendData.size
    }

    class MyHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var topTitle: TextView
        var itemImg1: ImageView
        var playTimes1: TextView
        var itemTitle1: TextView
        var itemImg2: ImageView
        var playTimes2: TextView
        var itemTitle2: TextView
        var itemImg3: ImageView
        var playTimes3: TextView
        var itemTitle3: TextView
        var itemImg4: ImageView
        var playTimes4: TextView
        var itemTitle4: TextView
        var loadMore: TextView
        var card1: CardView
        var card2: CardView
        var card3: CardView
        var card4: CardView
        var ll1: LinearLayout
        var ll2: LinearLayout

        init {
            topTitle = itemView!!.findViewById(R.id.top_title) as TextView
            itemImg1 = itemView.findViewById(R.id.item_img1) as ImageView
            playTimes1 = itemView.findViewById(R.id.play_times1) as TextView
            itemTitle1 = itemView.findViewById(R.id.item_title1) as TextView
            itemImg2 = itemView.findViewById(R.id.item_img2) as ImageView
            playTimes2 = itemView.findViewById(R.id.play_times2) as TextView
            itemTitle2 = itemView.findViewById(R.id.item_title2) as TextView
            itemImg3 = itemView.findViewById(R.id.item_img3) as ImageView
            playTimes3 = itemView.findViewById(R.id.play_times3) as TextView
            itemTitle3 = itemView.findViewById(R.id.item_title3) as TextView
            itemImg4 = itemView.findViewById(R.id.item_img4) as ImageView
            playTimes4 = itemView.findViewById(R.id.play_times4) as TextView
            itemTitle4 = itemView.findViewById(R.id.item_title4) as TextView
            loadMore = itemView.findViewById(R.id.load_more) as TextView
            card1 = itemView.findViewById(R.id.card1) as CardView
            card2 = itemView.findViewById(R.id.card2) as CardView
            card3 = itemView.findViewById(R.id.card3) as CardView
            card4 = itemView.findViewById(R.id.card4) as CardView
            ll1 = itemView.findViewById(R.id.ll1) as LinearLayout
            ll2 = itemView.findViewById(R.id.ll2) as LinearLayout
        }
    }

    class BannerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var banner: ConvenientBanner<Banner> = itemView.findViewById(R.id.banner) as ConvenientBanner<Banner>
    }
}