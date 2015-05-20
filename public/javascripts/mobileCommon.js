/**
 * Created by libaozhong on 2015/5/6.
 */
var url=window.location.href;
if(url.indexOf("bestProduct")>-1){
    clearFocus();
    if( $("#menu_Tree").hasClass("ui-icon-gold-unselected")){
        $("#menu_Tree").removeClass("ui-icon-gold-unselected");
        $("#menu_Tree").addClass("ui-icon-gold-selected");
    }else if( $("#menu_Tree").hasClass("ui-icon-gold-selected")){
        $("#menu_Tree").addClass("ui-icon-gold-unselected");
        $("#menu_Tree").removeClass("ui-icon-gold-selected");
    }
}
if(url.indexOf("moneyMatters")>-1){
    clearFocus();
    if(  $("#menu_Two").hasClass("ui-icon-list-unselected")){
        $("#menu_Two").removeClass("ui-icon-list-unselected");
        $("#menu_Two").addClass("ui-icon-list-selected");
    }else if(  $("#menu_Two").hasClass("ui-icon-list-selected")){
        $("#menu_Two").addClass("ui-icon-list-unselected");
        $("#menu_Two").removeClass("ui-icon-list-selected");
    }
}
if(url.indexOf("property")>-1){
    clearFocus();
    if( $("#menu_Tree").hasClass("ui-icon-gold-unselected")){
        $("#menu_Tree").removeClass("ui-icon-gold-unselected");
        $("#menu_Tree").addClass("ui-icon-gold-selected");
    }else if( $("#menu_Tree").hasClass("ui-icon-gold-selected")){
        $("#menu_Tree").addClass("ui-icon-gold-unselected");
        $("#menu_Tree").removeClass("ui-icon-gold-selected");
    }
}
if(url.indexOf("me")>-1){
    clearFocus();
    if( $("#menu_Four").hasClass("ui-icon-more-unselected")){
        $("#menu_Four").removeClass("ui-icon-more-unselected");
        $("#menu_Four").addClass("ui-icon-more-selected");
    }else if( $("#menu_Four").hasClass("ui-icon-more-selected")){
        $("#menu_Four").addClass("ui-icon-more-unselected");
        $("#menu_Four").removeClass("ui-icon-more-selected");
    }
}

function clearFocus(){
    if( $("#menu_Tree").hasClass("ui-icon-gold-selected")) {
        $("#menu_Tree").addClass("ui-icon-gold-unselected");
        $("#menu_Tree").removeClass("ui-icon-gold-selected");
    }
    if(  $("#menu_Two").hasClass("ui-icon-list-selected")){
        $("#menu_Two").addClass("ui-icon-list-unselected");
        $("#menu_Two").removeClass("ui-icon-list-selected");
    }
    if($("#menu_One").hasClass("ui-icon-home-selected")){
        $("#menu_One").addClass("ui-icon-home-unselected");
        $("#menu_One").removeClass("ui-icon-home-selected");
    }
    if( $("#menu_Four").hasClass("ui-icon-more-selected")){
        $("#menu_Four").addClass("ui-icon-more-unselected");
        $("#menu_Four").removeClass("ui-icon-more-selected");
    }
};