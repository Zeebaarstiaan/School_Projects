var firstBeatTime  = null,
    lastBeatTime   = null,
    heartBeats     = [],
    graph          = null;

window.onload = function () {
    graph = new Morris.Line({
        // ID of the element in which to draw the chart.
        element: 'heartbeatgraph',
        // Chart data records -- each entry in this array corresponds to a point on
        // the chart.
        data: [
        ],
        // The name of the data record attribute that contains x-values.
        xkey: 'time',
        // A list of names of data record attributes that contain y-values.
        ykeys: ['value'],
        // Labels for the ykeys -- will be displayed when you hover over the
        // chart.
        dateFormat: function (x) {
            var date = new Date(x);
            var hh = date.getUTCHours();
            var mm = date.getUTCMinutes();
            var ss = date.getSeconds();
            // This line gives you 12-hour (not 24) time
            if (hh > 12) {hh = hh - 12;}
            // These lines ensure you have two-digits
            if (hh < 10) {hh = "0"+hh;}
            if (mm < 10) {mm = "0"+mm;}
            if (ss < 10) {ss = "0"+ss;}
            // This formats your string to HH:MM:SS
            var t = hh+":"+mm+":"+ss;
            console.log(t);
            return t;
        },
        labels: ['Hartslag'],
        goals: [55, 100],
        goalLineColors: ['#0000ff', '#ff0000']
    });
    
    // fetch heartbeat stuff
    var socket = io.connect('http://127.0.0.1:8090/');
    var player_id = 0;

    socket.on('connect', function () {
        console.log('connected!');

        socket.on('heartbeat', function (data) {
            console.log(data);
            
            if (!lastBeatTime) { 
                lastBeatTime = new Date(data);
            } else {
                var lastBeat  = lastBeatTime,
                    newBeat   = new Date(data),
                    heartRate = Math.round(60000 / (newBeat.getTime() - lastBeat.getTime())),
                    body      = $('body');
                heartBeats.push({time: newBeat.getTime(), value: heartRate});
                graph.setData(heartBeats.slice(-20));
                lastBeatTime = newBeat;
                if (heartRate >= 100) {
                    body.attr('class', 'stress');
                } else if(heartRate <= 55) {
                    body.attr('class', 'sleep');
                } else {
                    body.attr('class', '');
                }
            }

        });
    });    
};