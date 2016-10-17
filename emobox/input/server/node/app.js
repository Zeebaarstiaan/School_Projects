var express = require('express');
var app = express();
var http = require('http');
var server = http.createServer(app);
var io = require('socket.io').listen(server);
var request = require('request');

var SerialPort = require('serialport').SerialPort;
var serialPort = new SerialPort('/dev/tty.usbmodemfa131', {baudrate: 115200});

app.use(express.static(__dirname + '/public'));
app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));

app.get('/', function(req, res){
		res.render('index.html');
});

serialPort.on("open", function(){
    
    console.log('open serial communication');
    var buffer = new Buffer(0);
    var cleanData = ''; // this stores the clean data
    var readData = '';  // this stores the buffer
    var sensorBuffer = new Array();

    serialPort.on('data', function (data) {
        var readData = parseInt(data.toString()),
            nowDate  = new Date();
        if (readData > 20 && readData < 200) {
            console.log("hartslag: " + readData);
            sensorBuffer.push({
                            'value': readData,
                            'date' : Math.round(nowDate.getTime() / 1000) 
                        });
            if (sensorBuffer.length >= 20) {
                submitData(sensorBuffer);
                sensorBuffer = new Array();
            }
        }
    });
});

function submitData(data) {
    console.log("=====\ndata verzonden\n=====");
    console.log(data);
    request.post('https://api.sense-os.nl/sensors/422419/data.json', {
        'headers': {
            'Accept': 'application/json',
            'X-SESSION_ID': '8964527b64ab92bbc5.06744125'
        },
        'json': {
            'data': data
        }
    });
}

server.listen(8090);
console.log("Listening to port 8090");