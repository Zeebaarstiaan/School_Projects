window.onload = function () {
	// ip here
	var socket = io.connect('http://127.0.0.1:8090/');
	var player_id = 0;

	socket.on('connect', function () {
		console.log('connected!');

		socket.on('player_id', function (data) {
			console.log("current player : " + data);
			$('.player'+player_id).removeClass('active')
			$('.progress'+player_id).removeClass('active')
			$('.image'+player_id).removeClass('bg')
			$('.image'+player_id+" img").attr("src","img/inactive.png")
			player_id = data;
			$('.player'+player_id).addClass('active')
			$('.progress'+player_id).addClass('active')
			$('.image'+player_id).addClass('bg')
			$('.image'+player_id+" img").attr("src","img/player"+player_id+".png")
		});
		socket.on('score', function (data) {
			level = Math.floor((data/10)+1);
			exp = (data % 10)*10

			console.log("score: "+data)
			console.log("level: "+level)
			console.log("exp: "+exp)

			$(".level"+player_id).html("Level: "+level);
			$(".bar"+player_id).css("width",exp+"%");

		});
		socket.on('button', function (data) {
			console.log("recieved: " + data);
		});
	});

};
