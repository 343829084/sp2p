
(function ($, window) {
var recommendPhone = $.getQueryString("mobile");
var result=isPhone(recommendPhone);
var rph=result.mobile;

$("img[data-load='load']").click(function () {
    window.location.href = sourceBaseUrl + "/share/downloadRouter.html?mobile="+rph;
})/**
 * Created by libaozhong on 2015/5/14.
 */

var b=redirect(redirectLink);
    $("#rg_ss").click(function(){
        window.location.href="/mobile/content/moneyMatters";
    })
if(b){
    $("#rg_bk").show();
    $("#rg_ss").hide();
};

   if(status =='4'){
       $("#rg_bk").show();
        $("#go_register").click(function(){
            window.location.href = "/mobile/login?mobile="+rph;
        });
    }else{
        $("#go_register").click(function () {
            window.location.href = "/mobile/quickRegister?mobile=" + rph;
        });
    }
    function show(){
        if($("#go_register").hasClass("bk-img")){
            $("#go_register").removeClass("bk-img");
            $("#go_register").addClass("bk-img-two");
        }else if($("#go_register").hasClass("bk-img-two")){
            $("#go_register").removeClass("bk-img-two");
            $("#go_register").addClass("bk-img");
        }
    }

    if($("#go_register")[0]){
        setInterval(show,1000);
    }
}(jQuery, window));
