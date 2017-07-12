package ui

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import com.mdjf.tucao.R
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import mainfrags.BangumiFragment
import mainfrags.RecFragment
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import web.RetrofitSingleton
import com.mdjf.tucao.R.id.toolbar
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle

//#onCreate rush

class MainActivity : AppCompatActivity() {
    val mTitleDataList = arrayListOf("推荐","新番","影剧","游戏","动画","频道")

    var viewPager: ViewPager?= null
    var magicIndicator: MagicIndicator?= null
    lateinit var toolBar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.addLogAdapter(AndroidLogAdapter())


        /**       设置toolBar左侧图标和侧边栏联动      **/
        toolBar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolBar)
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolBar, 0, 0)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        /**       设置toolBar左侧图标和侧边栏联动      **/

        magicIndicator = findViewById(R.id.magic_indicator) as MagicIndicator?
        viewPager = findViewById(R.id.viewpager) as ViewPager?
        viewPager?.adapter = MyAdapter(supportFragmentManager)
        viewPager?.offscreenPageLimit = 6
        initIndicator()
    }

    class MyAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        var frags: MutableList<Fragment> = mutableListOf()
        init {
            frags.add(RecFragment())
            frags.add(BangumiFragment())
            frags.add(RecFragment())
            frags.add(RecFragment())
            frags.add(RecFragment())
            frags.add(RecFragment())
        }
        override fun getItem(position: Int): Fragment {
            return frags[position]
        }

        override fun getCount(): Int {
            return frags.size
        }
    }

    fun initIndicator(){
        val magicIndicator = findViewById(R.id.magic_indicator) as MagicIndicator
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mTitleDataList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {

                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.normalColor = Color.parseColor("#145b7d")
                colorTransitionPagerTitleView.selectedColor = Color.WHITE
                colorTransitionPagerTitleView.text = mTitleDataList[index]
                colorTransitionPagerTitleView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        viewPager?.currentItem = index
                    }
                })
                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.setColors(Color.WHITE)
                indicator.mode = LinePagerIndicator.MODE_MATCH_EDGE
                return indicator
            }
        }
        commonNavigator.isAdjustMode = true
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, viewPager)
    }






















}
