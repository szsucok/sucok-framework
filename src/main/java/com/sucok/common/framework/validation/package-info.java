package com.sucok.common.framework.validation;


/*
用于Form的验证
*******javax.validation************
约束注解名称	约束注解说明
@Null	验证对象是否为空
@NotNull	验证对象是否为非空
@AssertTrue	验证 Boolean 对象是否为 true
@AssertFalse	验证 Boolean 对象是否为 false
@Min	验证 Number 和 String 对象是否大等于指定的值
@Max	验证 Number 和 String 对象是否小等于指定的值
@DecimalMin	验证 Number 和 String 对象是否大等于指定的值，小数存在精度
@DecimalMax	验证 Number 和 String 对象是否小等于指定的值，小数存在精度
@Size	验证对象（Array,Collection,Map,String）长度是否在给定的范围之内
@Digits	验证 Number 和 String 的构成是否合法
@Past	验证 Date 和 Calendar 对象是否在当前时间之前
@Future	验证 Date 和 Calendar 对象是否在当前时间之后
@Pattern	验证 String 对象是否符合正则表达式的规则


******* com.bwoil.common.framework.data.validation  *******
@BankCard 验证对象是否银行卡号
@Chinese 验证对象是否中文字符
@Email 验证对象是否邮件
@IdCard 验证对象是否身份证
@Length 验证对象是否长度
@LoginPassword 验证对象是否登录密码
@Mobile 验证对象是否手机号
@NotBlank 验证对象是否不为空
@PayPassword 验证对象是否支付密码
@URL 验证对象是否url地址



*/