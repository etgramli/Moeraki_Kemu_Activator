var mySocket = new WebSocket("localhost:9000/WS");
// Attach listeners
mySocket.onmessage = function(event) {
    $('#socketTest').text("Der Server sagt: " + event.data);
    // refresh fields
};
mySocket.onopen = function(event) {$('#socketTest').text("Verbindung zum Server hergestellt.");};
mySocket.onclose = function(event) {$('#socketTest').text("Spiel beendet oder abgebrochen!");};
mySocket.onerror = function(event) {alert("Leider ist ein Problem aufgetreten!");};

$(document).ready(function() {
    $('.gameCell').click(function(evt) {
        mySocket.send('setDotij')
        $.ajax({
            type : 'POST',
            url : "/occ",
            contentType: "text/json",
            data : JSON.stringify($(this).attr("id")),
            success : function(data) {
                var json = JSON.parse(data)
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
            },
            error : function(data) {
                alert("Server did not respond!");
            }
        });
        return false;
    });
});
