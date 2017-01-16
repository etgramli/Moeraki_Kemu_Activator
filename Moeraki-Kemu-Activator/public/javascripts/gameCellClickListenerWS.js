// Attach listeners
$(document).ready(function() {
    $('.gameCell').click(function(evt) {
        mySocket.send('setDot' + JSON.stringify($(this).attr("id")));
        return false;
    });
});

$(document).ready(function() {
    var gameIdx = ;
    var mySocket = new WebSocket('ws://localhost:9000/ws' + gameIdx);
    mySocket.onopen = function(event) {$('#socketTest').text("Verbindung zum Server hergestellt.");};
    mySocket.onclose = function(event) {$('#socketTest').text("Spiel beendet oder abgebrochen!");};
    mySocket.onerror = function(event) {alert("Leider ist ein Problem aufgetreten!");};
    mySocket.onmessage = function(event) {
        if (event.data.substring(0,7) == 'winner(') {
            alert("Der Gewinner ist " + event.substring(8, 17));
        } else if (event.data.substring(0,6) == 'alert(') {
            alert(event.data.substring(7, event.data.length - 1));
        } else {
	    // refresh fields
        var json = JSON.parse(event.data)
        var lines = json.lines;
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i];
            var cells = line.cells
            for (var j = 0; j < cells.length; j++) {
                var cell = cells[j];
                if (cell == "StartDot") {
                    $('#' + i + '-' + j).css('background-color', '#000000');
                } else if (cell == "Spieler 1") {
                    $('#' + i + '-' + j).css('background-color', '#00FF11');
                } else if (cell == "Spieler 2") {
                    $('#' + i + '-' + j).css('background-color', '#FF3300');
                }
            }
        }
        }
    };
};
