package com.tw.sysnetdemo;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import android.util.Log

/**
 * 网络工具类
 * 参考{@url https://developer.android.com/training/basics/network-ops/managing.html}
 * 需要权限:<br>
 * <p>{@link Manifest.permission#ACCESS_NETWORK_STATE}</p>
 * ConnectivityManager: Answers queries about the state of network connectivity. It also notifies applications when network connectivity changes.
 * NetworkInfo: Describes the status of a network interface of a given type (currently either Mobile or Wi-Fi).
 */
class NetUtil private constructor() {

    companion object {
        private val TAG = "NetUtil"
        private val D = BuildConfig.DEBUG
        private val sNetConnChangedReceiver = NetConnChangedReceiver()
        private val sNetConnChangedListeners = ArrayList<NetConnChangedListener>()

        /**
         * 网络接口是否可用（即网络连接是否可行）和/或连接（即是否存在网络连接，是否可以建立套接字并传递数据）
         *
         * @param context 上下文
         * @return {@code true} 网络可用
         */
        fun isNetConnected(context: Context): Boolean {
            val activeInfo = getActiveNetworkInfo(context)
            return activeInfo != null && activeInfo!!.isConnected()
        }

        /**
         * 是否移动数据连接
         *
         * @param context 上下文
         * @return {@code true} 移动数据连接
         */
        fun isMobileConnected(context: Context): Boolean {
            val activeInfo = getActiveNetworkInfo(context)
            return activeInfo != null && activeInfo!!.isConnected() && activeInfo!!.getType() == ConnectivityManager.TYPE_MOBILE
        }

        /**
         * 是否2G网络连接
         *
         * @param context 上下文
         * @return {@code true} 2G网络连接
         */
        fun is2GConnected(context: Context): Boolean {
            val activeInfo = getActiveNetworkInfo(context)
            if (activeInfo == null || !activeInfo!!.isConnected()) {
                return false
            }
            val subtype = activeInfo!!.getSubtype()
            return when (subtype) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> true
                else -> false
            }
        }

        /**
         * 是否3G网络连接
         *
         * @param context 上下文
         * @return {@code true} 3G网络连接
         */
        fun is3GConnected(context: Context): Boolean {
            val activeInfo = getActiveNetworkInfo(context)
            if (activeInfo == null || !activeInfo!!.isConnected()) {
                return false
            }
            val subtype = activeInfo!!.getSubtype()
            return when (subtype) {
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_TD_SCDMA -> true
                else -> false
            }
        }

        /**
         * 是否4G网络连接
         *
         * @param context 上下文
         * @return {@code true} 4G网络连接
         */
        fun is4GConnected(context: Context): Boolean {
            val activeInfo = getActiveNetworkInfo(context)
            if (activeInfo == null || !activeInfo!!.isConnected()) {
                return false
            }
            val subtype = activeInfo!!.getSubtype()
            return when (subtype) {
                TelephonyManager.NETWORK_TYPE_LTE, TelephonyManager.NETWORK_TYPE_IWLAN -> true
                else -> false
            }
        }

        /**
         * 获取移动网络运营商名称
         * <lu>
         * <li>中国联通</li>
         * <li>中国移动</li>
         * <li>中国电信</li>
         * </lu>
         *
         * @param context 上下文
         * @return 移动网络运营商名称
         */
        fun getNetworkOperatorName(context: Context): String {
            val tm = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.networkOperatorName
        }

        /**
         * 获取移动终端类型
         *
         * @param context 上下文
         * @return 手机制式
         * <ul>
         * <li>{@link TelephonyManager#PHONE_TYPE_NONE } : 0 手机制式未知</li>
         * <li>{@link TelephonyManager#PHONE_TYPE_GSM  } : 1 手机制式为GSM，移动和联通</li>
         * <li>{@link TelephonyManager#PHONE_TYPE_CDMA } : 2 手机制式为CDMA，电信</li>
         * <li>{@link TelephonyManager#PHONE_TYPE_SIP  } : 3</li>
         * </ul>
         */
        fun getPhoneType(context: Context): Int {
            val tm = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.phoneType
        }

        /**
         * 判断是否Wifi连接
         *
         * @param context 上下文
         * @return true 如果是wifi连接
         */
        fun isWifiConnected(context: Context): Boolean {
            val activeInfo = getActiveNetworkInfo(context)
            return activeInfo != null && activeInfo!!.isConnected() && activeInfo!!.getType() == ConnectivityManager.TYPE_WIFI
        }

        private class NetConnChangedReceiver : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                log("onReceive")
                val activeInfo = getActiveNetworkInfo(context)
                if (activeInfo == null) {
                    broadcastConnStatus(ConnectStatus.NO_NETWORK)
                } else if (activeInfo!!.isConnected()) {
                    val networkType = activeInfo!!.getType()
                    if (ConnectivityManager.TYPE_WIFI == networkType) {
                        broadcastConnStatus(ConnectStatus.WIFI)
                    } else if (ConnectivityManager.TYPE_MOBILE == networkType) {
                        broadcastConnStatus(ConnectStatus.MOBILE)
                        val subtype = activeInfo!!.getSubtype()
                        if (TelephonyManager.NETWORK_TYPE_GPRS == subtype
                            || TelephonyManager.NETWORK_TYPE_GSM == subtype
                            || TelephonyManager.NETWORK_TYPE_EDGE == subtype
                            || TelephonyManager.NETWORK_TYPE_CDMA == subtype
                            || TelephonyManager.NETWORK_TYPE_1xRTT == subtype
                            || TelephonyManager.NETWORK_TYPE_IDEN == subtype
                        ) {
                            broadcastConnStatus(ConnectStatus.MOBILE_2G)
                        } else if (TelephonyManager.NETWORK_TYPE_UMTS == subtype
                            || TelephonyManager.NETWORK_TYPE_EVDO_0 == subtype
                            || TelephonyManager.NETWORK_TYPE_EVDO_A == subtype
                            || TelephonyManager.NETWORK_TYPE_HSDPA == subtype
                            || TelephonyManager.NETWORK_TYPE_HSUPA == subtype
                            || TelephonyManager.NETWORK_TYPE_HSPA == subtype
                            || TelephonyManager.NETWORK_TYPE_EVDO_B == subtype
                            || TelephonyManager.NETWORK_TYPE_EHRPD == subtype
                            || TelephonyManager.NETWORK_TYPE_HSPAP == subtype
                            || TelephonyManager.NETWORK_TYPE_TD_SCDMA == subtype
                        ) {
                            broadcastConnStatus(ConnectStatus.MOBILE_3G)
                        } else if (TelephonyManager.NETWORK_TYPE_LTE == subtype || TelephonyManager.NETWORK_TYPE_IWLAN == subtype) {
                            broadcastConnStatus(ConnectStatus.MOBILE_4G)
                        } else {
                            broadcastConnStatus(ConnectStatus.MOBILE_UNKNOWN)
                        }
                    } else {
                        broadcastConnStatus(ConnectStatus.OTHER)
                    }
                } else {
                    broadcastConnStatus(ConnectStatus.NO_CONNECTED)
                }
            }
        }

        /**
         * 注册网络接收者
         * @param context 上下文
         */
        fun registerNetConnChangedReceiver(context: Context) {
            val filter = IntentFilter()
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(sNetConnChangedReceiver, filter)
        }

        /**
         * 取消注册网络接收者
         * * @param context 上下文
         */
        fun unregisterNetConnChangedReceiver(context: Context) {
            context.unregisterReceiver(sNetConnChangedReceiver)
            sNetConnChangedListeners.clear()
        }

        /**
         * 添加网络状态变化监听
         *
         * @param listener 网络连接状态改变监听
         */
        fun addNetConnChangedListener(listener: NetConnChangedListener) {
            val result = sNetConnChangedListeners.add(listener)
            log("addNetConnChangedListener: $result")
        }

        /**
         * 移除指定网络变化监听
         *
         * @param listener 网络连接状态改变监听
         */
        fun removeNetConnChangedListener(listener: NetConnChangedListener) {
            val result = sNetConnChangedListeners.remove(listener)
            log("removeNetConnChangedListener: $result")
        }

        private fun broadcastConnStatus(connectStatus: ConnectStatus) {
            val size = sNetConnChangedListeners.size
            if (size == 0) {
                return
            }
            for (i in 0 until size) {
                sNetConnChangedListeners[i].onNetConnChanged(connectStatus)
            }
        }

        private fun getActiveNetworkInfo(context: Context): NetworkInfo {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connMgr.activeNetworkInfo
        }

        private fun log(msg: String) {
            if (D) {
                Log.e(TAG, msg)
            }
        }

        interface NetConnChangedListener {
            fun onNetConnChanged(connectStatus: ConnectStatus)
        }

        enum class ConnectStatus {
            NO_NETWORK,
            WIFI,
            MOBILE,
            MOBILE_2G,
            MOBILE_3G,
            MOBILE_4G,
            MOBILE_UNKNOWN,
            OTHER,
            NO_CONNECTED
        }
    }
}