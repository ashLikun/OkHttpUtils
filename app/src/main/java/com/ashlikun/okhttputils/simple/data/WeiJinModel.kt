package com.ashlikun.okhttputils.simple.data

import com.ashlikun.okhttputils.retrofit.FieldNo

/**
 * 作者　　: 李坤
 * 创建时间: 2017/12/27　13:59
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
class WeiJinModel {
    /**
     * marketId : 61129
     * creditorId : 119332
     * investId : 8696
     * investType : 个人消费借款
     * investCode : V170600010083
     * investLevel : B
     * creditorStatus : 0
     * transferAPR : 0.11
     * residualPeriod : 30
     * transferAmt : 855.28
     * investmentBaoName : 保
     * transferDate : 00:00:22
     * hold : false
     */
    var marketId = 0
    var creditorId = 0
    var investId = 0
    var investType: String? = null
    var investCode: String? = null
    var investLevel: String? = null
    var creditorStatus: String? = null
    var transferAPR = 0.0
    var residualPeriod = 0
    var transferAmt = 0.0
    var investmentBaoName: String? = null
    var transferDate: String? = null
    var isHold = false
    var adwwww: Adwwww = Adwwww()
}

data class Adwwww(val value: String = "")