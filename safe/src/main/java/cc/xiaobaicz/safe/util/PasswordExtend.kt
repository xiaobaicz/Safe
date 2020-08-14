package cc.xiaobaicz.safe.util

import cc.xiaobaicz.safe.global.Constant


//本地密码hmacmd5运算
fun localHmacMD5(pw: String) = pw.hmacMD5(Constant.KEY_LOCAL_HMD5)