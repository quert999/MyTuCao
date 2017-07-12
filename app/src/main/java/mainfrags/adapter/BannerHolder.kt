package mainfrags.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import bean.Banner
import com.bigkoo.convenientbanner.holder.Holder
import com.bumptech.glide.Glide
import com.mdjf.tucao.R

class BannerHolder: Holder<Banner> {
    lateinit var rootView: View

    override fun createView(context: Context): View {
        rootView = LayoutInflater.from(context).inflate(R.layout.item_banner, null)
        return rootView
    }

    override fun UpdateUI(context: Context, position: Int, banner: Banner) {
        val bannerImg = rootView.findViewById(R.id.img_banner) as ImageView
        val bg = rootView.findViewById(R.id.bg)
        Glide.with(context).load(banner.imgUrl).into(bannerImg)
        bannerImg.setOnClickListener {
            view ->
            if (banner.hid != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val p1: Pair<View, String> = Pair.create(bannerImg, "cover")
                    val p2: Pair<View, String> = Pair.create(bg, "bg")
                    val options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(context as Activity, p1, p2).toBundle()
                } else {
                }
            } else {
            }
        }
        banner.title?.let {
            val titleLinear = rootView.findViewById(R.id.linear_title)
            titleLinear.visibility = View.VISIBLE
            val titleText = rootView.findViewById(R.id.text_title) as TextView
            titleText.text = it
        }
    }

}
