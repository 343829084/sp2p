var Utils = (function ($) {
	var sendRequest = function (type, param, url, callbackFunc) {
		return $.ajax({
			type: type,
			data : param,
			url: url,
			async: false,
			success: function (result) {
				if (result && result != "") {
					if (result.message.code == RESULT_STATUS.SUCCESS && callbackFunc) {
						callbackFunc(result.value);
					}
				}
				else {
					alert("后台出错");
				}
			},
			error : function (result) {
			}
		});
	};
	return {
		sendRequest : sendRequest
	};
})(jQuery);

var Service = (function () {
	return {
	};
})();

var Contorller = (function () {
	var predictPrice = function () {
	};

	var bindEvent = function () {
		$("#showPredictPrice").on("blur", predictPrice);
	};

	var init = function () {
		bindEvent ();
	};
	return {
		init: init
	};
})();


$(function () {
	Contorller.init();
});