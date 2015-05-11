/*
 设置请求路径
 */
function getBaseUrl(){
    //return "http://115.29.196.179:8080/api/service/test";
    return "http://localhost:9000";
//    return "https://www.qmcaifu.com/api/service";
}
function getBaseImgUrl(){
    return "http://115.29.196.179:8080/";
}
function getBabaUrl(){
    //return "http://192.168.1.221:8090/api/facade/sendFubaba";
    return "https://www.qmcaifu.com/api/facade/sendFubaba";
}

//James保存到cookie
function setCookie(name,value,days)
{
    var exp = new Date();
    exp.setTime(exp.getTime() + days*24*60*60*1000);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}
//James读取cookies 
function getCookie(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");

    if(arr=document.cookie.match(reg))

        return unescape(arr[2]);
    else
        return null;
}

/*
 检查是否支持checkStorageSupport
 */
function checkStorageSupport() {

    // sessionStorage
    if (window.sessionStorage) {
        return true;
    } else {
        return false;
    }

    // localStorage
    if (window.localStorage) {
        return true;
    } else {
        return false;
    }
}
//显示图片弹窗
function showBigImg(imgUrl){
    closeBigImg();
    $("body").after('<div id="detailImg" onClick="$(this).detach();" style="display:none; overflow:hidden;"><div class="pinch-zoom" style="position:absolute; z-index:9999; background-size:contain; overflow:auto;"><img src="'+imgUrl+'" id="detailImgImg" style=""></div><div class="opacity_div" style="opacity:0.9;filter:Alpha(opacity=90); position:absolute; top:0; left:0;" id="detailImgOpacity"></div></div>');
    $("#detailImg").fadeIn();
    //background:url('+imgUrl+') center no-repeat;
}
//关闭图片弹窗
function closeBigImg(){
    $("#detailImg").fadeOut();
    $("#detailImg").detach();
}
//显示loading弹窗
function showLoading(){
    $("body").after('<div id="loading"><div id="icon_loading"><span class="cover_loading_img"><img src="images/icon_loading.gif" width="32px" /></span></div><div class="opacity_div"></div></div>');
}
//关闭loading弹窗
function closeLoading(){
    $("#loading").detach();
}
/*
 错误弹窗关闭
 */
function closeErrorTips(){
    $("#error_tips").hide();
}

/*
 显示错误弹窗
 Param:errorMessage	错误消息文本
 */
function showErrorTips(errorMessage,errorCode){
    if(errorCode==0001){
        window.location.href='./login.html';
        return;
    }
    if(arguments.length==1||errorCode.readyState==undefined||(errorCode.readyState.toString() != '0'&&errorCode.readyState.toString() != '1'&&errorCode.readyState.toString() != '2'&&errorCode.readyState.toString() != '3')){
        closeLoading();
        if($(document).find("#error_tips").length>0){
            $("#error_tips error_tips_content var").text(errorMessage);
        }else{
            $("body").after('<div id="error_tips"><div class="error_tips"><h4 class="error_tips_title">提示</h4><p class="error_tips_content"><var>'+errorMessage+'</var></p><div class="close_error_tips"><span id="btn_closeErrorTips" class="btn_PopRed cwhite" onClick="closeErrorTips();">确 定</span></div></div><div class="opacity_div"></div></div>');
        }
    }
}
//显示自动消失的提示框
function showMessageTips(message,dealy){
    closeLoading();
    //alert($(document).find("#error_tips").length);
    if($(document).find("#error_tips").length>0){
        $("#error_tips error_tips_content var").text(message);
    }else{
        $("body").after('<div id="error_tips"><div class="error_tips"><h4 class="error_tips_title">提示</h4><p class="error_tips_content"><var>'+message+'</var></p></div><div class="opacity_div"></div></div>');
    }
    setTimeout(function(){
        closeErrorTips();
    },dealy);
}
//显示带取消按钮的提示框
function showConfirmTips(confirmMessage,btn_okText,btn_cancelText){
    closeLoading();
    if($(document).find("#error_tips").length>0){
        $("#error_tips error_tips_content var").text(confirmMessage);
        $("#error_tips #btn_ok").text(btn_okText);
        $("#error_tips #btn_cancel").text(btn_cancelText);
    }else{
        if(arguments.length>1&&btn_okText!=""&&btn_cancelText!=""){
            $("body").after('<div id="error_tips"><div class="error_tips"><h4 class="error_tips_title">提示</h4><p class="error_tips_content"><var>'+confirmMessage+'</var></p><div class="close_error_tips"><span id="btn_ok" class="btn_PopRed2 cwhite" onClick="closeErrorTips();">'+btn_okText+'</span><span id="btn_cancel" class="btn_PopRed3 cwhite" onClick="closeErrorTips();">'+btn_cancelText+'</span></div></div><div class="opacity_div"></div></div>');
        }else{
            $("body").after('<div id="error_tips"><div class="error_tips"><h4 class="error_tips_title">提示</h4><p class="error_tips_content"><var>'+confirmMessage+'</var></p><div class="close_error_tips"><span id="btn_ok" class="btn_PopRed2 cwhite" onClick="closeErrorTips();">确 定</span><span id="btn_cancel" class="btn_PopRed3 cwhite" onClick="closeErrorTips();">取 消</span></div></div><div class="opacity_div"></div></div>');
        }
    }
}

/*
 获取当前时间
 */
function getNowTime(){
    var year,month,date,hours,minutes,seconds;
    var nowTime = new Date();
    year = nowTime.getFullYear();
    month = nowTime.getMonth()+1;
    date = nowTime.getDate();
    hours = nowTime.getHours();
    minutes = nowTime.getMinutes();
    seconds = nowTime.getSeconds();
    if(month<10){
        month = "0"+month;
    }
    if(date<10){
        date = "0"+date;
    }
    if(hours<10){
        hours = "0"+hours;
    }
    if(minutes<10){
        minutes = "0"+minutes;
    }
    if(seconds<10){
        seconds = "0"+seconds;
    }
    var dateStr = year.toString()+month.toString()+date.toString()+hours.toString()+minutes.toString()+seconds.toString();
    return dateStr;
}
/*
 ajaxPost请求data
 version	版本
 appVersion	客户端应用版本
 requestTime	请求时间
 bizType	业务类型
 customerId	客户ID
 deviceId	设备号
 token	登录token
 jsonArray	页面请求数据数组，[param],[value]
 */

function setRequestData(bizType,customerId,jsonArray){
    //version、appVersion、requestTime默认固定
    var version = "1.0";
    var appVersion = "HTML5";
    var requestTime = getNowTime();
    var deviceId = "";
    var token = "";
    var allData = null;
    var baseData = '{"version":"' + version
        + '","appVersion":"'+ appVersion
        + '","requestTime":"' + requestTime
        + '","bizType":"' + bizType
        + '","customerId":"' + customerId
        + '","deviceId":"' + deviceId
        + '","token":"' + token;
    if(arguments.length==2){
        allData = baseData + '"}';
    }else{
        var inputDataStr = "";
        for(var i = 0; i < jsonArray[0].length; i++){
            if(i==jsonArray[0].length-1){
                inputDataStr = inputDataStr+'"'+jsonArray[0][i]+'":"'+jsonArray[1][i]+'"';
            }else{
                inputDataStr = inputDataStr+'"'+jsonArray[0][i]+'":"'+jsonArray[1][i]+'",';
            }
        }
        allData = baseData+'","data":{'+inputDataStr+'}}' ;
    }
    return allData;
}

//网页前进
function pageNext(){
    window.history.go(1);
}
//网页后退
function pagePrev(){
    window.history.go(-1);
}

//显示银行名称
function showBankNameById(bankId){

}

//存储会话数据
function setSessionData(key,value){
    sessionStorage.setItem(key,value);
}
//获取会话数据
function getSessionData(key){
    return sessionStorage.getItem(key);
}
//删除会话数据
function removeSessionData(key){
    sessionStorage.removeItem(key);
}
//清除所有数据
function clearSessionData(){
    sessionStorage.clear();
}
//重新登录
function loginRestart(){
    $("body").html('<h1 style="text-align:center; color:#831b32; margin-top:80px; font-size:2.4rem; line-height:40px;">抱歉，您还未登录!</h1><p style="color:#999; font-size:1.4rem;">6秒后将返回登录页面,<a href="./login.html" style="color:#09c; font-weight:bold; text-decoration:underline;">立即登录</a></p>');
    var restart = setTimeout(function(){
        window.location.href="./login.html";
    },6000)
}
//获取设备信息
function getDeviceInfo(){
    return navigator.userAgent.toString();
}
/*=========slidePayInfo==========*/
function slidePayInfo(){
    $(".icon_upAndDown").on("click",function(){
        if($(this).css("background").match("icon_slideDown")){
            $(".boxSlide").slideDown();
            $(this).css("background",'url(./images/icon_slideUp.png) no-repeat center');
        }else{
            $(".boxSlide").slideUp();
            $(this).css("background",'url(./images/icon_slideDown.png) no-repeat center');
        }
    });
}