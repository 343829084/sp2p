function countdown (prodItems) {
    var item = null;
    var tempArray = [];
    for (var i = 0; i < prodItems.length; i++) {
        item = prodItems[i];
        var countTime = new Date(item.sellTime);
        var now = new Date ();
        var leftTime = countTime.getTime() - now.getTime();
        var leftSec = parseInt(leftTime/1000);
        var day1=Math.floor(leftSec/(60*60*24));
        var hour=Math.floor((leftSec-day1*24*60*60)/3600);
        var minute=Math.floor((leftSec-day1*24*60*60-hour*3600)/60);
        var second=Math.floor(leftSec-day1*24*60*60-hour*3600-minute*60);
        //console.log(hour + " " + minute + " " + second);

        tempArray.push(hour + " " + minute + " " + second);
    }
    postMessage(tempArray);
}


onmessage = function (event) {
    var prodItems = event.data;
    setInterval(function (){
        countdown(prodItems);
    }, 1000)
    //var item = null;
    //for (var i = 0; i < prodItems.length; i++) {
    //    item = prodItems[i];
    //    var countTime = new Date(item.sellTime);
    //    var now = new Date ();
    //    var leftTime = countTime.getTime() - now.getTime();
    //    var leftSec = parseInt(leftTime/1000);
    //    var day1=Math.floor(leftSec/(60*60*24));
    //    var hour=Math.floor((leftSec-day1*24*60*60)/3600);
    //    var minute=Math.floor((leftSec-day1*24*60*60-hour*3600)/60);
    //    var second=Math.floor(leftSec-day1*24*60*60-hour*3600-minute*60);
    //    console.log(hour + " " + minute + " " + second);
    //}
    console.log(event);
    //postMessage("back");
};

