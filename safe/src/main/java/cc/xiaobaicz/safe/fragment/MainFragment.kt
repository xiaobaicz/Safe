package cc.xiaobaicz.safe.fragment

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.recyclerview.extend.AdapterX
import cc.xiaobaicz.recyclerview.extend.config
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.db.entity.Account
import cc.xiaobaicz.safe.model.MainViewModel
import cc.xiaobaicz.safe.util.dp
import cc.xiaobaicz.safe.util.setOnOnceClickListener
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_account.view.*
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 首页
 * 账号列表
 */
class MainFragment : BaseFragment() {

    private val vm by viewModels<MainViewModel>()

    //数据源
    private val data = ArrayList<Account>()

    //适配器
    private lateinit var adapter: AdapterX<Account>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    //添加事件
    override fun onSetListener() {
        //添加账户
        fab_add.setOnOnceClickListener(gotoAccountDetail())

        //清空文本
        btn_clear.setOnClickListener {
            et_keyword.setText("")
        }

        //文本变化
        et_keyword.addTextChangedListener(onTextChanged = { text, _, _, _->
            vm.selectAccountForKeyword(text)
        })

        //Tab选择
        btn_sort_hot.setOnClickListener {
            vm.selectTab(SortType.HOT)
        }
        btn_sort_domain.setOnClickListener {
            vm.selectTab(SortType.DOMAIN)
        }
        btn_sort_time.setOnClickListener {
            vm.selectTab(SortType.TIME)
        }

        //手动 显示 or 隐藏 工具栏
        btn_search.setOnClickListener {
            val lp = layer_tools.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = lp.behavior as ToolsBehavior
            behavior.startAnimator(layer_tools, !behavior.isShow)
        }

        //退出提示
        var lastTime = 0L
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (SystemClock.elapsedRealtime() - lastTime < 1000) {
                //退出
                requireActivity().finish()
            } else {
                lastTime = SystemClock.elapsedRealtime()
                showSnackbar(container, "双击退出")
            }
        }
    }

    private fun gotoAccountDetail(account: Account? = null) = { _: View, restore: ()->Unit->
        val bundle = Bundle()
        bundle.putParcelable("account", account)
        findNavController().navigate(R.id.action_mainFragment_to_accountDetailFragment, bundle)
        restore()
    }

    //配置View
    override fun onConfigView() {
        lifecycleScope.launch {
            //设置安全区域
            val size = systemUiSize()
            toolbarSafeRegion(toolbar, size[1])
            list_account.updatePadding(top = list_account.paddingTop + size[1] + 56.dp.toInt(), bottom = list_account.paddingBottom + size[3])
            (layer_tools.layoutParams as ViewGroup.MarginLayoutParams).apply {
                topMargin += size[1]
            }
            (fab_add.layoutParams as ViewGroup.MarginLayoutParams).apply {
                bottomMargin += size[3]
            }
        }

        //列表配置
        adapter = list_account.config(data) {
            val format = SimpleDateFormat.getDateInstance(DateFormat.SHORT)
            addType(Account::class.java, R.layout.item_account) { d, h, _ ->
                h.root.apply {
                    tv_domain.text = d.domain
                    tv_account.text = d.account
                    tv_lastTime.text = format.format(Date(d.lastTime))
                    setOnOnceClickListener(gotoAccountDetail(d))
                }
            }
        }

        //tab状态变化
        vm.tabStatus.observe(viewLifecycleOwner, Observer {
            initTabItem() //初始化状态
            when (it.sortType) { //选中状态
                SortType.HOT -> selectTabItem(btn_sort_hot, iv_sort_hot, it.isAsc)
                SortType.DOMAIN -> selectTabItem(btn_sort_domain, iv_sort_domain, it.isAsc)
                SortType.TIME -> selectTabItem(btn_sort_time, iv_sort_time, it.isAsc)
            }
        })

        //账户信息监听
        vm.accounts.observe(viewLifecycleOwner, Observer {
            if (data != it) { //数据是否一致，不一致则更新数据
                data.clear()
                data.addAll(it)
                adapter.notifyDataSetChanged()
                if (it.isEmpty()) {
                    showSnackbar(container, "暂无账户数据，请添加")
                }
            }
        })
    }

    //选中TabItem
    private fun selectTabItem(btn: TextView, iv: ImageView, isAsc: Boolean) {
        btn.isSelected = true
        iv.imageTintList = null
        iv.isSelected = isAsc
    }

    //初始化TabItem的状态
    private fun initTabItem() {
        btn_sort_hot.isSelected = false
        btn_sort_domain.isSelected = false
        btn_sort_time.isSelected = false

        val colorList = ColorStateList.valueOf(requireContext().getColor(R.color.tab_item_icon_def))
        iv_sort_hot.imageTintList = colorList
        iv_sort_domain.imageTintList = colorList
        iv_sort_time.imageTintList = colorList

        iv_sort_hot.isSelected = false
        iv_sort_domain.isSelected = false
        iv_sort_time.isSelected = false
    }

    /**
     * 排序方式
     */
    enum class SortType(val value: String) {
        /**
         * 热度
         */
        HOT("hot"),

        /**
         * 域
         */
        DOMAIN("domain"),

        /**
         * 时间
         */
        TIME("last_time")
    }

    /**
     * 工具栏协调器
     */
    class ToolsBehavior : CoordinatorLayout.Behavior<View> {
        //工具栏是否显示
        internal var isShow = false
        private set
        //是否可用 （动画状态）
        private var isAvailable = true

        constructor() : super()
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

        override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
            return true
        }

        override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
            if (isAvailable) {
                val isDown = dyConsumed + dyUnconsumed < 0
                val isUp = dyConsumed + dyUnconsumed > 0
                if (isDown && isShow) {
                    //下拉状态 且 工具栏显示，则隐藏
                    startAnimator(child, false)
                } else if (isUp && !isShow) {
                    //上拉状态 且 工具栏隐藏，则显示
                    startAnimator(child, true)
                }
            }
        }

        //开启 工具栏 展示/隐藏 动画
        internal fun startAnimator(child: View, visible: Boolean, block: (()->Unit)? = null) {
            if (isAvailable) {
                isAvailable = false //不可用状态 防止多次调用
                val start = if (visible) 1f else 0f
                val end = if (visible) 0f else 1f
                val animator = ValueAnimator.ofFloat(start, end)
                animator.duration = 666L
                animator.addUpdateListener {
                    val v = it.animatedValue as Float
                    val offset = 112.dp * v
                    child.translationY = -offset
                }
                animator.doOnEnd {
                    isShow = !isShow
                    isAvailable = true
                    child.clearFocus()
                    block?.invoke()
                }
                animator.start()
            }
        }
    }

}