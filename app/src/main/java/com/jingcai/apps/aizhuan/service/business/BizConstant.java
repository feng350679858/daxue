package com.jingcai.apps.aizhuan.service.business;

/**
 * Created by lejing on 15/4/27.
 */
public interface BizConstant {
    /**
     * sys
     */
    String BIZ_SYS_01 = "sys01";//注册
    String BIZ_SYS_02 = "sys02";//获取验证码
    String BIZ_SYS_03 = "sys03";//检测用户
    String BIZ_SYS_04 = "sys04";//登陆
    String BIZ_SYS_05 = "sys05";//退出登陆
    String BIZ_SYS_06 = "sys06";//获取数据字典
    String BIZ_SYS_07 = "sys07";//发送短信

    /**
     * student
     */
    String BIZ_STU_01 = "stu01";//修改密码
    String BIZ_STU_02 = "stu02";//获取个人信息
    String BIZ_STU_03 = "stu03";//修改个人信息
    String BIZ_STU_04 = "stu04";//重置密码（忘记密码）
    String BIZ_STU_05 = "stu05";//设置校友可见
    String BTZ_STU_07 = "stu07";//重置支付密码
    String BTZ_STU_11 = "stu11";//个人信用分查询
    String BTZ_STU_12 = "stu12";

    /**
     * partjob
     */
    String BIZ_PARTTIME_JOB_01 = "partjob01";//兼职列表查询（首页）--用户搜索记录新增
    String BIZ_PARTTIME_JOB_02 = "partjob02";//取兼职详情—包括获取同校报名信息
    String BIZ_PARTTIME_JOB_03 = "partjob03";//报名历史
    String BIZ_PARTTIME_JOB_04 = "partjob04";//报名详情
    String BIZ_PARTTIME_JOB_05 = "partjob05";//兼职报名
    String BIZ_PARTTIME_JOB_06 = "partjob06";//取消报名
    String BIZ_PARTTIME_JOB_07 = "partjob07";//获取报名商家列表（联系商家）
    String BIZ_PARTTIME_JOB_08 = "partjob08";//获取已取消次数
    String BIZ_PARTTIME_JOB_09 = "partjob09";//手机端兼职列表查询（包含特殊搜索）
    String BIZ_PARTTIME_JOB_10 = "partjob10";//兼职标签详情列表查询
    String BIZ_PARTTIME_JOB_11 = "partjob11";
    String BIZ_PARTTIME_JOB_24 = "partjob24";
    String BIZ_PARTTIME_JOB_27 = "partjob27";
    String BIZ_PARTTIME_JOB_29 = "partjob29";//评论列表查询

    /**
     * school
     */
    String BIZ_SCHOOL_01 = "school01";//获取已开通省份信息
    String BIZ_SCHOOL_02 = "school02";//获取已开通城市信息
    String BIZ_SCHOOL_03 = "school03";//获取下级地区信息
    String BIZ_SCHOOL_04 = "school04";//按名称查询学校信息—包括院系信息
    String BIZ_SCHOOL_05 = "school05";//按学校id查询学院信息
    String BIZ_SCHOOL_06 = "school06";//按名称查询专业信息
    String BIZ_SCHOOL_07 = "school07";//根据定位查询区域信息
    String BIZ_SCHOOL_08 = "school08";//根据定位查询下级区域信息

    /**
     * advice
     */
    String BIZ_ADVICE_01 = "advice01";//获取消息列表
    String BIZ_ADVICE_02 = "advice02";//获取消息详情
    String BIZ_ADVICE_03 = "advice03";//投诉和建议
    String BIZ_ADVICE_04 = "advice04";//获取未读消息条数
    String BIZ_ADVICE_05 = "advice05";//获取事件消息列表

    /**
     * base
     */
    String BIZ_BASE_01   = "base01";//banner信息查询
    String BIZ_BASE_02   = "base02";//版本信息查询
    String BIZ_BASE_03   = "base03";//Loading图获取
    /**
     * base
     */
    String BIZ_BUSI_01   = "busi01";////首页标签列表查询
    String BIZ_BUSI_02   = "busi02";//首页推荐位信息列表查询

    /**
     * account
     */
    String BIZ_ACCOUNT_01 = "account01"; //账户余额查询
    String BIZ_ACCOUNT_02 = "account02"; //账户变动明细查询
    String BIZ_ACCOUNT_03 = "account03"; //提现
    String BIZ_ACCOUNT_04 = "account04"; //第三方金融渠道列表查询
    String BIZ_ACCOUNT_05 = "account05"; //钱包余额日汇总
    String BIZ_ACCOUNT_06 = "account06"; //第三方金融渠道绑定

    /**
     * game
     */
    String BTZ_GAME_09 = "game09";//身份信息验证

}