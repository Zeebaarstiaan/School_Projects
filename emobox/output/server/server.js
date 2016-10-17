// Arduino Framework
var five = require("./node_modules/johnny-five/lib/johnny-five.js"),
    board, led;

//Helpful modules
var request = require('request');

//Sense modules
var senseFetcher = require('sense_fetcher');

//Data modules
var dataStripper  = require('data_stripper');
var dataHomonizer = require('data_homonizer');

//Data analysis modules
var average           = require('average');
var max               = require('max');
var min               = require('min');
var occurance         = require('occurance');
var median            = require('median');
var standardDeviation = require('standard_deviation');

//Heartrate specific modules
var heartrateFilter = require('heartrate_filter');

//Wifi specific modules
var wifiAnalyzer         = require('wifi_analyzer');
var googleCalendarHelper = require('google_calendar_helper');

// Color values
var color_green = "00FF00";
var color_orange = "D61500"; // "FF8000"
var color_red = "7D0068"; // "FF0000";

// Active Colors
var led_color = "000000";
var led2_color = "000000";

//Test values
var testValues = [81, 80, 79, 79, 79, 79, 79, 79, 80, 80, 80, 81, 81, 80, 79, 77, 76, 74, 73, 71, 70, 69, 68, 68, 68, 67, 67, 67, 67, 67, 67, 67, 67, 67, 68, 69, 69, 69, 70, 71, 71, 71, 71, 71, 71, 70, 70, 71, 70, 70, 71, 75, 80, 86, 91, 100, 105, 104, 105, 103, 102, 97, 90, 83, 79, 75, 72, 72, 72, 73, 72, 72, 72, 72, 72, 72, 72, 71, 71, 72, 74, 75, 76, 77, 78, 79, 80, 80, 80, 83, 89, 89, 93, 101, 108, 117, 127, 141, 157, 161, 142, 137, 125, 110, 101, 91, 84, 80, 76, 72, 73, 72, 71, 71, 70, 71, 73, 77, 77, 80, 83, 92, 97, 108, 119, 132, 135, 136, 142, 145, 142, 124, 117, 106, 102, 95, 92, 91, 94, 89, 87, 89, 89, 90, 88, 86, 84, 79, 74, 73, 72, 72, 71, 72, 72, 72, 72, 72, 71, 71, 71, 71, 72, 72, 73, 74, 75, 80, 86, 92, 98, 98, 98, 97, 99, 95, 95, 91, 85, 81, 77, 77, 77, 78, 76, 77, 76, 75, 75, 75, 75, 74, 74, 74, 74, 74, 74, 75, 75, 75, 74, 73, 73, 73, 73, 73, 73, 74, 74, 74, 75, 76, 76, 78, 78, 78, 79, 79, 80, 80, 79, 80, 80, 79, 80, 78, 77, 76, 75, 73, 73, 73, 72, 72, 72, 73, 73, 72, 72, 72, 73, 72, 72, 73, 73, 72, 72, 73, 74, 75, 75, 76, 77, 76, 76, 76, 77, 77, 77, 76, 76, 76, 76, 76, 76, 77, 77, 77, 78, 82, 87, 86, 86, 85, 84, 84, 83, 84, 84, 80, 76, 76, 77, 78, 78, 78, 80, 80, 81, 82, 84, 89, 95, 95, 96, 100, 107, 106, 107, 112, 120, 122, 122, 122, 131, 134, 133, 143, 155, 155, 154, 152, 151, 170, 170, 168, 167, 171, 154, 142, 134, 135, 137, 127, 129, 133, 126, 117, 119, 117, 111, 101, 91, 90, 81, 76, 74, 74, 77, 82, 88, 98, 104, 111, 130, 136, 142, 131, 114, 104, 96, 87, 84, 78, 73, 71, 71, 72, 76, 78, 80, 83, 89, 97, 100, 101, 106, 114, 112, 109, 104, 101, 94, 87, 87, 86, 82, 77, 77, 77, 79, 84, 90, 87, 85, 83, 80, 79, 78, 78, 77, 75, 74, 75, 76, 81, 85, 91, 97, 105, 112, 119, 117, 130, 138, 140, 151, 157, 156, 140, 127, 115, 106, 103, 104, 96, 90, 74, 64, 62, 64, 67, 68, 67, 64, 67, 72, 81, 97, 110, 106, 99, 98, 96, 94, 88, 80, 79, 75, 70, 70, 69, 69, 68, 69, 70, 71, 71, 71, 72, 72, 71, 72, 72, 72, 72, 73, 74, 75, 77, 79, 80, 82, 83, 83, 84, 84, 84, 84, 83, 82, 81, 80, 79, 79, 79, 78, 78, 78, 78, 79, 79, 79, 79, 79, 79, 79, 79, 77, 77, 75, 75, 74, 73, 72, 72, 72, 71, 71, 71, 72, 73, 73, 74, 75, 75, 75, 75, 76, 77, 77, 78, 78, 79, 78, 78, 78, 78, 77, 76, 75, 74, 72, 72, 71, 71, 70, 70, 70, 70, 71, 71, 71, 71, 71, 71, 72, 72, 73, 73, 73, 74, 75, 77, 77, 76, 76, 75, 74, 73, 72, 72, 71, 70, 70, 71, 71, 71, 72, 73, 73, 73, 73, 73, 73, 72, 72, 73, 74, 75, 76, 78, 79, 80, 81, 81, 80, 79, 79, 78, 78, 76, 75, 75, 73, 73, 73, 73, 73, 72, 72, 72, 72, 72, 73, 75, 76, 76, 77, 77, 78, 78, 77, 76, 75, 74, 72, 71, 71, 70, 69, 70, 71, 72, 73, 75, 77, 82, 89, 91, 93, 98, 103, 102, 100, 99, 98, 93, 86, 86, 85, 81, 77, 77, 77, 76, 76, 75, 75, 75, 75, 75, 75, 75, 75, 75, 76, 76, 75, 76, 76, 76, 76, 76, 76, 77, 77, 76, 76, 76, 75, 75, 74, 74, 74, 75, 75, 75, 76, 77, 77, 78, 79, 80, 81, 85, 92, 93, 94, 95, 94, 94, 93, 92, 90, 85, 79, 78, 77, 75, 75, 74, 73, 72, 72, 72, 70, 69, 69, 69, 69, 69, 69, 70, 70, 70, 71, 71, 72, 72, 72, 72, 73, 72, 71, 71, 71, 71, 71, 71, 71, 70, 70, 70, 71, 71, 70, 70, 69, 70, 70, 70, 70, 71, 71, 71, 71, 71, 71, 71, 70, 71, 71, 71, 71, 71, 72, 73, 74, 74, 75, 75, 75, 75, 76, 77, 77, 78, 79, 80, 82, 82, 82, 82, 80, 79, 78, 76, 76, 75, 74, 74, 75, 76, 77, 79, 79, 80, 81, 85, 91, 92, 92, 96, 103, 102, 108, 117, 117, 113, 106, 108, 116, 119, 112, 112, 107, 99, 97, 96, 100, 105, 97, 88, 86, 84, 84, 83, 83, 82, 78, 73, 72, 74, 75, 76, 75, 75, 74, 73, 74, 75, 76, 77, 77, 78, 79, 84, 92, 91, 91, 86, 84, 82, 81, 79, 79, 75, 71, 71, 70, 73, 73, 72, 72, 71, 69, 68, 67, 67, 67, 67, 67, 67, 67, 67, 67, 67, 68, 68, 67, 67, 67, 67, 66, 66, 67, 67, 66, 66, 67, 68, 68, 70, 71, 72, 74, 75, 76, 76, 76, 75, 74, 73, 73, 73, 75, 80, 80, 81, 82, 82, 82, 81, 80, 79, 75, 70, 69, 69, 69, 69, 70, 72, 74, 75, 77, 78, 78, 77, 76, 75, 74, 74, 73, 72, 72, 71, 71, 71, 71, 71, 71, 71, 70, 70, 70, 70, 70, 71, 71, 72, 72, 72, 72, 72, 72, 72, 73, 74, 75, 75, 75, 76, 76, 76, 75, 75, 74, 73, 72, 72, 71, 71, 70, 70, 70, 71, 71, 70, 70, 70, 70, 70, 70, 71, 72, 72, 73, 75, 76, 76, 76, 75, 75, 74, 74, 73, 73, 72, 72, 72, 72, 73, 73, 77, 81, 82, 82, 82, 82, 82, 81, 80, 79, 76, 72, 72, 72, 72, 72, 72, 72, 73, 73, 73, 73, 73, 73, 73, 73, 74, 76, 76, 77, 79, 80, 81, 83, 84, 85, 85, 84, 84, 84, 83, 82, 81, 80, 79, 78, 77, 77, 77, 76, 75, 75, 74, 73, 72, 71, 72, 72, 73, 73, 74, 74, 75, 74, 75, 74, 76, 73, 72, 71, 70, 70, 70, 71, 71, 72, 71, 73, 73, 73, 74, 74, 74, 74, 75, 76, 76, 77, 78, 81, 82, 82, 84, 84, 85, 85, 87, 91, 98, 96, 95, 93, 93, 93, 91, 91, 91, 91, 91, 98, 105, 115, 123, 134, 152, 170, 169, 155, 136, 125, 116, 107, 102, 96, 90, 84, 88, 94, 96, 98, 104, 113, 115, 116, 117, 124, 126, 115, 112, 109, 101, 93, 90, 89, 87, 82, 78, 78, 79, 84, 91, 92, 91, 90, 89, 88, 86, 85, 84, 80, 76, 76, 76, 77, 78, 79, 80, 80, 80, 81, 80, 80, 81, 81, 82, 82, 83, 83, 84, 83, 83, 82, 82, 82, 82, 81, 80, 80, 79, 79, 78, 78, 77, 76, 75, 75, 75, 74, 74, 75, 75, 76, 75, 74, 74, 74, 74, 75, 75, 76, 80, 86, 88, 90, 96, 106, 107, 106, 104, 103, 96, 89, 89, 94];

function testAnalysis (values) {
    average.setValues(values);
    max.setValues(values);
    min.setValues(values);
    occurance.setValues(values);
    median.setValues(values);
    standardDeviation.setValues(values);

    console.log('average           : ' + average.getAverage());
    console.log('median            : ' + median.getMedian());
    console.log('min               : ' + min.getMin());
    console.log('max               : ' + max.getMax());
    console.log('occuring most     : ' + occurance.getMostOccuring());
    console.log('occuring ammount  : ' + occurance.getOccurance());
    console.log('standard deviation: ' + standardDeviation.getStandardDeviation());
}

function analyseHeartbeatData (data) {
    console.log(data);
    var heartRateData = dataHomonizer.getHomonizedData(
            heartrateFilter.filterData(
                dataStripper.getStrippedValues(data, 'int')
            )
        , 'int');

    senseFetcher.getHeartBeatDataHistory(function (data) {
        var heartRateDataHistory = dataHomonizer.getHomonizedData(
            heartrateFilter.filterData(
                dataStripper.getStrippedValues(data, 'int')
            )
        , 'int');

        //Do analysis
        console.log('\nStatistics last 30:\n===================')
        testAnalysis(heartRateData);
        console.log('\nStatistics all time:\n====================')
        testAnalysis(heartRateDataHistory);

        median.setValues(heartRateDataHistory);
        standardDeviation.setValues(heartRateDataHistory);
        average.setValues(heartRateData);

        var lastHeartbeatsAverage  = average.getAverage(),
            totalStandardDeviation = standardDeviation.getStandardDeviation(),
            totalMedian            = median.getMedian();

        if (lastHeartbeatsAverage < totalMedian) {
            //All is well, turn light green
            console.log('Heartrate: green');
            led_color = color_green;
        } else if (lastHeartbeatsAverage > (totalMedian + totalStandardDeviation)) {
            //All is not well, turn light red
            console.log('Heartrate: red');
            led_color = color_red;
        } else {
            //Not bad, not good
            console.log('Heartrate: orange');
            led_color = color_orange;
        }
    });
}

function analyseWifiData (data) {
    wifiAnalyzer.setValues(data);
    var hoursAtSchool = wifiAnalyzer.getHoursAtSchool();
    console.log('Hours at school: ' + hoursAtSchool);
    if (hoursAtSchool > 6) {
        //All is well, turn light green
        console.log('Hours at school: green');
        led2_color = color_green;
    } else if (hoursAtSchool > 4) {
        //Not bad, not good
        console.log('Hours at school: orange');
        led2_color = color_orange;
    } else {
        //All is not well, turn light red
        console.log('Hours at school: red');
        led2_color = color_red;
    }
}

board = new five.Board();

board.on("ready", function() {
  var led, led2;

  led = new five.Led.RGB([9, 10, 11]); // Led is wired on these pins
  led2 = new five.Led.RGB([3, 5, 6]);

  led.color("FFFFFF");
  led2.color("FFFFFF");

  setInterval(function(){
    console.log('\n\nNew Loop:\n====================')
    senseFetcher.getHeartBeatData(analyseHeartbeatData);
    senseFetcher.getWifiData(analyseWifiData);

    console.log("Setting lights");
    led.color(led_color);
    led2.color(led2_color);
  }, 10000);

  this.repl.inject({
    led: led
  });


});
