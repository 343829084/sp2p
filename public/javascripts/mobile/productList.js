var initAdvBanner = function() {
	var $scroll = $(".scroll-box"),
		windowWidth = $(window).width(),
		divWidth = $scroll.width();

	$scroll.css("margin-left",(windowWidth-divWidth)/2+"px");

	var sWidth = $("#banner ul li").width();
	var len = $("#banner ul li").length;
	var index = 0;
	var picTimer;

	var btn = "<div class='btnBg'></div><div class='btn'>";
	for(var i=0; i < len; i++) {
		btn += "<span></span>";
	}
	btn += "</div>";

	$("#banner").append(btn);

	$("#banner .btnBg").css("opacity",1);
	$(".btn").css("left",($(window).width()-$(".btn").outerWidth())/2+"px");

	//为小按钮添加鼠标滑入事件，以显示相应的内容
	$("#banner .btn span").css("opacity",1).mouseover(function() {
		index = $("#banner .btn span").index(this);
		showPics(index);
	}).eq(0).trigger("mouseover");

	//鼠标滑上焦点图时停止自动播放，滑出时开始自动播放
	$scroll.hover(function() {
		clearInterval(picTimer);
	},function() {
		picTimer = setInterval(function() {
			showPics(index);
			index++;
			if(index == len) {index = 0;}
		},3000);
	}).trigger("mouseleave");

	//显示图片函数，根据接收的index值显示相应的内容
	function showPics(index) {
		var nowLeft = -index*1920;
		$("#banner ul").stop(true,false).animate({"left":nowLeft+"px"},300);
		$("#banner .btn span").removeClass("active").eq(index).addClass("active");
		$("#banner .btn span").stop(true,false).animate({"opacity":"1"},300).eq(index).stop(true,false).animate({"opacity":"1"},300);
	}

	$(window).resize(function(){
		var windowWidth = $(window).width(),
			divWidth = $scroll.width();
		$scroll.css("margin-left",(windowWidth-divWidth)/2+"px");
		$(".btn").css("left",($(window).width()-$(".btn").outerWidth())/2+"px");
	});
};

var TOUCH_POINT = {
	START:0,
	END:0
};

var isUp = false;

var productListTouchMove = function (){
	document.getElementById("product-list").addEventListener('touchstart', function (event){
		$("#product-list").css({
			"transition": ""
		}) ;
		if (event.targetTouches.length == 1) {
			TOUCH_POINT.START = event.targetTouches[0].screenY;
		}
	});

	document.getElementById("product-list").addEventListener('touchmove', function(event) {
		//console.log(event.targetTouches[0].screenY + "  screenY");
		//console.log(event.targetTouches[0].clientY + "  clientY");
		//console.log(event.targetTouches[0].pageY + "  pageY");
		//console.log("/n");
		console.log(event.targetTouches[0].screenY  + " screenY");
		console.log(TOUCH_POINT.START + " TOUCH_POINT.START");
		console.log($("#product-list").css("top") + "  top");
		console.log(event.targetTouches[0].screenY - TOUCH_POINT.START);
		if (event.targetTouches.length == 1 && parseInt($("#product-list").css("top")) == 0
			&& (event.targetTouches[0].screenY > TOUCH_POINT.START)) {
			$("#product-list").css("top", event.targetTouches[0].screenY-TOUCH_POINT.START) ;
		}
		//else {
		//	$("#product-list").css("top", event.targetTouches[0].screenY-TOUCH_POINT.START) ;
		//}
	});

	document.getElementById("product-list").addEventListener('touchend', function(event) {
		if (parseInt($("#product-list").css("top")) >= 0) {
			$("#product-list").css({
				"top": "0",
				"transition": "top 2s"
			}) ;
		}
	});
}


$(function () {
	initAdvBanner();
	//floatWidget();
	productListTouchMove();
});