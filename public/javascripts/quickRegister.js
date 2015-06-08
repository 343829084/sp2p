/**
 * Created by libaozhong on 2015/1/22.
 */
var success = "/share/registerSuccess.html";
(function ($, window) {
    //获取url手机号

    var recommendPhone = $.getQueryString("mobile");
    var rp="";

var result=isPhone(recommendPhone);
    rp=result.mobile;
    if(result.is){
        shareUrl= sourceBaseUrl+"/share/share.html?mobile="+rp;
        $("#recommendPhone").val(rp);
        $("#recommendPhone").attr("readonly", "true");
    }
    //获取验证码{

    $("#getveriycode").click(
        function () {
            var mobilePhoneNo = $("#phoneNum").val();
            if (mobilePhoneNo.length != 11) {
                $("#phoneNumerrorinfo").html("<span>手机号码有误！</span>");
                return;
            }
            var verifyCodeElement = $("#getveriycode");
            verifyCodeElement.removeClass("verify-color").addClass("gray-color");
            verifyCodeElement.unbind("click");
            $.getVerifyCode(mobilePhoneNo);
            $.time(verifyCodeElement, 60, mobilePhoneNo);
        }
    );

    $('#agreeTerms').click(function () {
        if ($('#agreeTerms').is(':checked')) {
            $('#register').removeAttr("disabled");

            $('#register').removeClass("gray-color");
        } else {
            $('#register').attr("disabled", "disabled");

            $('#register').addClass("gray-color");

        }
    });
    $(".eye").click(
        function(){
            if( $("#loginpwd").attr("type")=="password"){
                $(".eye").css("backgroundPosition","0 0px");
                $(".eye").css("background-size","100%");
                $("#loginpwd").attr("type","text");
            }else
            if( $("#loginpwd").attr("type")=="text"){
                $(".eye").css("backgroundPosition","0 -59px");
                $(".eye").css("background-size","100%");
                $("#loginpwd").attr("type","password");
            }

        }
    )
    //注册
    $("#register").click(
        function () {
            var mobilePhoneNo = $("#phoneNum").val();

            var pwd = $("#loginpwd").val();
            if (pwd.length < 6) {
                $("#pwderrorinfo").html("<span >密码过于简单！</span>");
                return;
            }
            if (!/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/.test(pwd)) {
                $("#pwderrorinfo").html("<span >6-20位字母和数字组合！</span>");
                return;
            }
            var verifyCode = $("#inputverifycode").val();
            if (verifyCode.length < 4) {
                $("#ipvcerrorinfo").html("<span>验证码填写错误！</span>");
                return;
            }
            if (!recommendPhone) {
                recommendPhone = $("#recommendPhone").val();
            }
            if (!validatePhoneNum(mobilePhoneNo, $("#phoneNumerrorinfo"))) {
                return;
            };
            success = "/share/registerSuccess.html?mobile="+mobilePhoneNo;
            registry(mobilePhoneNo, verifyCode, pwd, recommendPhone);
        }
    );
    //注册方法
    function registry(custmobile, verifyCode, passWord, recommendPhone) {
        console.debug(custmobile + "," + passWord + "," + verifyCode);
        var formParams = "mobilePhoneNo=" + custmobile +
            "&passWord=" + passWord +
            "&verifyCode=" + verifyCode +
            "&recommendPhone=" + recommendPhone;
        $("label").each(function () {
            var lab = $(this);
            if (lab.data('error') == 'error') {
                lab.html('');
            }
        });
        try {
            $.ajax({
                type: "POST",
                url:  "/mobile/register",
                data: {
                    name: custmobile,
                    password: passWord,
                    verifyCode:verifyCode,
                    recommended: recommendPhone
                },
                success: function (data) {
                    if (0 == data.error.code) {
                        window.location.href = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx320badb1a6f6b806&redirect_uri=http%3A%2F%2Fp2pv2.sunlights.me%2Fmobile%2Fweixin%2FopenId&response_type=code&scope=snsapi_base&state=3#wechat_redirect";

                    }if(-2 == data.error.code){
                        window.location.href = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx320badb1a6f6b806&redirect_uri=http%3A%2F%2Fp2pv2.sunlights.me%2Fmobile%2Fweixin%2FopenId&response_type=code&scope=snsapi_base&state=4#wechat_redirect";
                    //  window.location.href = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx320badb1a6f6b806&redirect_uri=http%3A%2F%2Fp2pv2.sunlights.me%2Fmobile%2Fweixin%2Fgetcode&response_type=code&scope=snsapi_base&state=4#wechat_redirect";
                    //getcode
                    }else
                    {
                        $("#phoneNumerrorinfo").html("<span>" + data.error.msg + "</span>");
                    }
                },
                error: function (XMLHttpRequest) {
                    $('#phoneNumerrorinfo').html("<span>网络繁忙！</span>");
                }
            });
        } catch (err) {
            $('#phoneNumerrorinfo').html("<span>网络繁忙！</span>");
        }
    };
    //    $.ajax({
    //        type: "POST",
    //        dataType: "json",
    //        url:  '/mobile/register',
    //        async: true,
    //        data: formParams,
    //
    //        success: function (data) {
    //            //if (data.message.code == "0100") {
    //            //    $.ajax({
    //            //            type: "POST",
    //            //            dataType: "json",
    //            //            url: apiBaseUrl + '/account/activity/register',
    //            //            success: function (data) {
    //
    //                            return location.href = success;
    //                        },
    //                        error: function (data) {
    //                            $('#phoneNumerrorinfo').html("<span>网络繁忙！</span>");
    //                        }
    //                    }
    //                );
    //                window.location.href = $.geturl(success);
    //            } else {
    //                $("#phoneNumerrorinfo").html("<span>" + data.message.summary + "</span>");
    //            }
    //        },
    //        error: function (data) {
    //            $("#phoneNumerrorinfo").html("<span>网络异常</span>");
    //        }
    //    });
    //}

    //统一验证手机号



}(jQuery, window));
