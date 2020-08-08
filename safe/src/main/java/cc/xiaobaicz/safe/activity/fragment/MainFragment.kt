package cc.xiaobaicz.safe.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            //设置安全区域
            val size = systemUiSize()
            toolbarSafeRegion(toolbar, size[1])
            list_account.updatePadding(top = list_account.paddingTop + size[1] + 56.dp.toInt(), bottom = list_account.paddingBottom + size[3])
            (fab_add.layoutParams as ViewGroup.MarginLayoutParams).apply {
                bottomMargin += size[3]
            }
        }

        //添加账户
        fab_add.setOnOnceClickListener { _, restore ->
            restore()
        }

        //列表配置
        adapter = list_account.config(data) {
            val format = SimpleDateFormat.getDateInstance(DateFormat.SHORT)
            addType(Account::class.java, R.layout.item_account) { d, h, _ ->
                h.root.apply {
                    tv_domain.text = d.domain
                    tv_account.text = d.account
                    tv_lastTime.text = format.format(Date(d.lastTime))
                }
            }
        }

        //账户信息监听
        vm.accounts.observe(viewLifecycleOwner, Observer {
            data.addAll(it)
            adapter.notifyDataSetChanged()
        })

        //获取所有账户
        vm.selectAccountAll()
    }

}