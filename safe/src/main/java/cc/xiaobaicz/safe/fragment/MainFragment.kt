package cc.xiaobaicz.safe.fragment

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
import androidx.core.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import cc.xiaobaicz.recyclerview.extend.config
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.databinding.FragmentMainBinding
import cc.xiaobaicz.safe.databinding.ItemAccountBinding
import cc.xiaobaicz.safe.db.entity.Account
import cc.xiaobaicz.safe.model.MainViewModel
import cc.xiaobaicz.safe.util.dp
import cc.xiaobaicz.safe.util.setOnIntervalClickListener
import cc.xiaobaicz.safe.util.setOnOnceClickListener
import cc.xiaobaicz.safe.widget.ContentViewBehavior
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
    private val data: MutableList<Any> = ArrayList()

    private val bind by lazy {
        FragmentMainBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind.model = vm
        return bind.root
    }

    //配置View
    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(bind.toolbar)
        
        lifecycleScope.launch { 
            val size = safeRegion()
            bind.toolbar.also {
                it.layoutParams.height = 56.dp.toInt() + size.t
                it.updatePadding(top = size.t)
            }
            bind.listAccount.updatePaddingRelative(bottom = size.b)
        }

        //列表配置
        bind.listAccount.config(data) {
            val format = SimpleDateFormat.getDateInstance(DateFormat.SHORT)
            addType<Account, AccountViewHolder>(R.layout.item_account) { d, h, _, _ ->
                h.bind.account = d
                h.bind.format = format
                h.bind.executePendingBindings()
                h.bind.root.setOnOnceClickListener(gotoAccountDetail(d))
            }
        }

        //tab状态变化
        vm.tabStatus.observe(viewLifecycleOwner, Observer {
            initTabItem() //初始化状态
            when (it.sortType) { //选中状态
                SortType.HOT -> selectTabItem(bind.btnSortHot, bind.ivSortHot, it.isAsc)
                SortType.DOMAIN -> selectTabItem(bind.btnSortDomain, bind.ivSortDomain, it.isAsc)
                SortType.TIME -> selectTabItem(bind.btnSortTime, bind.ivSortTime, it.isAsc)
            }
        })

        //账户信息监听
        vm.accounts.observe(viewLifecycleOwner, Observer {
            data.clear()
            data.addAll(it)
            bind.listAccount.adapter?.notifyDataSetChanged()
            if (it.isEmpty()) {
                showSnackbar(bind.container, getString(R.string.snackbar_account_empty))
            }
        })

        vm.selectAccountAll()
    }

    //添加事件
    override fun onSetListener() {
        //添加账户
        bind.fabAdd.setOnOnceClickListener(gotoAccountDetail())

        //清空文本
        bind.btnClear.setOnIntervalClickListener {
            bind.etKeyword.setText("")
        }

        //文本变化
        bind.etKeyword.addTextChangedListener(onTextChanged = { text, _, _, _->
            if (text != null && text.isNotEmpty()) {
                vm.selectAccountForKeyword(text)
            } else {
                vm.selectAccountAll()
            }
        })

        //Tab选择
        bind.btnSortHot.setOnIntervalClickListener(233) {
            vm.selectTab(SortType.HOT)
        }
        bind.btnSortDomain.setOnIntervalClickListener(233) {
            vm.selectTab(SortType.DOMAIN)
        }
        bind.btnSortTime.setOnIntervalClickListener(233) {
            vm.selectTab(SortType.TIME)
        }

        //设置
        bind.btnSetting.setOnIntervalClickListener {
            gotoSetting()
        }

        //退出提示
        var lastTime = 0L
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (SystemClock.elapsedRealtime() - lastTime < 1000) {
                //退出
                requireActivity().finish()
            } else {
                lastTime = SystemClock.elapsedRealtime()
                showSnackbar(bind.container, getString(R.string.snackbar_double_click))
            }
        }
    }

    private fun gotoAccountDetail(account: Account? = null) = { _: View, restore: ()->Unit->
        val bundle = Bundle()
        bundle.putParcelable("account", account)
        findNavController().navigate(R.id.action_mainFragment_to_accountDetailFragment, bundle)
        restore()
    }

    private fun gotoSetting() {
        findNavController().navigate(R.id.action_mainFragment_to_settingFragment)
    }

    //选中TabItem
    private fun selectTabItem(btn: TextView, iv: ImageView, isAsc: Boolean) {
        btn.isSelected = true
        iv.imageTintList = null
        iv.isSelected = isAsc
    }

    //初始化TabItem的状态
    private fun initTabItem() {
        bind.btnSortHot.isSelected = false
        bind.btnSortDomain.isSelected = false
        bind.btnSortTime.isSelected = false

        val colorList = ColorStateList.valueOf(requireContext().getColor(R.color.tab_item_icon_def))
        bind.ivSortHot.imageTintList = colorList
        bind.ivSortDomain.imageTintList = colorList
        bind.ivSortTime.imageTintList = colorList

        bind.ivSortHot.isSelected = false
        bind.ivSortDomain.isSelected = false
        bind.ivSortTime.isSelected = false
    }

    /**
     * 排序方式
     */
    enum class SortType(val value: String, val sort: ((List<Account>, Boolean) -> List<Account>)) {
        /**
         * 热度
         */
        HOT("hot", { d, isAsc ->
            d.sortedWith(Comparator { o1, o2 ->
                return@Comparator if (isAsc) {
                    (o1.hot - o2.hot).toInt()
                } else {
                    (o2.hot - o1.hot).toInt()
                }
            })
        }),

        /**
         * 域
         */
        DOMAIN("domain", { d, isAsc ->
            if (isAsc) {
                d
            } else {
                d.asReversed()
            }
        }),

        /**
         * 时间
         */
        TIME("last_time", { d, isAsc ->
            d.sortedWith(Comparator { o1, o2 ->
                return@Comparator if (isAsc) {
                    (o1.lastTime - o2.lastTime).toInt()
                } else {
                    (o2.lastTime - o1.lastTime).toInt()
                }
            })
        })
    }

    class AccountViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bind = ItemAccountBinding.bind(view)
    }

    /**
     * 列表关于工具栏的协调器
     */
    class RecyclerViewBehavior : CoordinatorLayout.Behavior<View> {

        constructor() : super()
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

        override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
            return dependency.id == R.id.layer_tools
        }

        override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
            child.translationY = dependency.y + dependency.height
            return true
        }

    }

    /**
     * 工具栏关于标题栏的协调器
     */
    class ToolsLayerBehavior : ContentViewBehavior {

        //偏移量
        private var offset = 0

        //预备偏移量
        private var preOffset = 0

        //工具栏高度
        private var h = 0

        //列表偏移量
        private var listOffset = 0
        //预备列表偏移量
        private var preListOffset = 0

        constructor() : super()
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

        override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
            return target is RecyclerView && axes == ViewCompat.SCROLL_AXIS_VERTICAL
        }

        override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
            preOffset = offset + dy //设置预备便宜
            h = child.height    //工具栏高度

            if (preOffset > h) {    //预备偏移是否超过高度
                if (offset != h) {  //工具栏是否已固定
                    //偏移剩余距离
                    preOffset = h - offset
                    offset(child, preOffset, consumed)
                    offset = h
                } else {
                    //工具栏固定
                    //列表继续偏移 （标题栏空位） 逻辑跟preOffset > h一致
                    preListOffset = listOffset + dy
                    if (preListOffset > toolbarHeight) {
                        if (listOffset != toolbarHeight) {
                            preListOffset = toolbarHeight - listOffset
                            offset(target, preListOffset, consumed)
                            listOffset = toolbarHeight
                        }
                        return
                    }
                    listOffset = preListOffset
                    offset(target, dy, consumed)
                }
                return
            }

            if (preOffset < 0) {    //预备偏移是否超过下限
                if (offset != 0) {  //工具栏是否已固定
                    //偏移剩余距离
                    preOffset = -offset
                    offset(child, preOffset, consumed)
                    offset = 0
                }
                return
            }

            //工具栏固定隐藏
            //列表恢复偏移 （标题栏空位） 逻辑跟preOffset < 0一致
            if (listOffset != 0) {
                preListOffset = listOffset + dy
                if (preListOffset < 0) {
                    if (listOffset != 0) {
                        preListOffset = -listOffset
                        offset(target, preListOffset, consumed)
                        listOffset = 0
                    }
                    return
                }
                listOffset = preListOffset
                offset(target, dy, consumed)
                return
            }

            offset = preOffset  //设置偏移距离

            offset(child, dy, consumed)
        }

        //偏移
        private fun offset(child: View, dy: Int, consumed: IntArray) {
            child.translationY -= dy
            consumed[1] = dy
        }

    }

}