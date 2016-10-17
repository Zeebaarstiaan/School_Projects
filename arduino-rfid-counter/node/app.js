var express = require('express');
var app = express();
var http = require('http');
var server = http.createServer(app);
var io = require('socket.io').listen(server);

var SerialPort = require('serialport').SerialPort;
var serialPort = new SerialPort('/dev/tty.usbmodem1421');

app.use(express.static(__dirname + '/public'));
app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));

app.get('/', function(req, res){
		res.render('index.html');
});

serialPort.on("open", function(){
	console.log('open serial communication');

	io.sockets.on('connection', function (socket) {

		var buffer = new Buffer(0);
		var cleanData = ''; // this stores the clean data
		var readData = '';  // this stores the buffer

		serialPort.on('data', function (data) {
			buffer = Buffer.concat( [buffer, data] );

			if( buffer.toString().match(/\n/) ) {
				readData = buffer.toString();
				if (readData.indexOf('B') >= 0 && readData.indexOf('A') >= 0) {
					cleanData = readData.substring(readData.indexOf('A') + 1, readData.indexOf('B'));
					console.log(cleanData);
					socket.emit('player_id', cleanData);
				} else if (readData.indexOf('X') >= 0 && readData.indexOf('Z') >= 0) {
					cleanData = readData.substring(readData.indexOf('Z') + 1, readData.indexOf('X'));
					console.log(cleanData);
					socket.emit('score', cleanData);
				} else {
					console.log( "length " + buffer.length );
					socket.emit('button', buffer.toString());
					console.log( buffer.toString() );
				}
				buffer = new Buffer(0);
			}


		});

	});

});

server.listen(8090);

console.log("Listening to port 8090");
