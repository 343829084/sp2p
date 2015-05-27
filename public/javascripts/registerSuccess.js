
(function ($, window) {
var recommendPhone = $.getQueryString("mobile");
var result=isPhone(recommendPhone);
var rph=result.mobile;
shareUrl=sourceBaseUrl+"/share/share.html?mobile="+result.mobile;

$("img[data-load='load']").click(function () {
    window.location.href = sourceBaseUrl + "/share/downloadRouter.html?mobile="+rph;
})/**
 * Created by libaozhong on 2015/5/14.
 */

var b=redirect(redirectLink);
    $("#rg_ss").click(function(){
        window.location.href="/mobile/content/bestProduct";
    })
if(b){
    $("#rg_bk").show();
    $("#rg_ss").hide();
};
    $("#go_register").click(function(){
        window.location.href = sourceBaseUrl+"/share/quickRegister.html?mobile="+rph;
    });

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
