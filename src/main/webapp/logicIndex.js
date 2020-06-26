function updateHall() {
    var url = "http://localhost:8081/cinema/updateHall?id=1";
    $.ajax({
        type: "GET",
        url: url,
        success: [function ($data) {
            setValues($data);
        }],
        dataType: 'json'
    });
    return false;

}
function setValues($data) {
    for (let row = 1; row <= 3; row++) {
        for (let place = 1; place <= 3; place++) {
            var number = row.toString() + place.toString();
            var occupied = $data[number];
            var placeId = ((row - 1) * 3 + place).toString();
            if (occupied) {
                document.getElementById(placeId).disabled = true;
            } else {
                document.getElementById(placeId).disabled = false;
            }
        }
    }
}
setTimeout(updateHall, 0);
setInterval(updateHall, 1000);
function pay() {
    var placeNumber = "";
    var radios = document.getElementsByName('place');
    for (var i = 0; i !== radios.length; i++) {
        var radio = radios[i];
        if (radio.checked) {
            placeNumber = document.getElementById((i + 1).toString()).getAttribute('value');
        }
    }
    if (placeNumber !== "") {
        window.location.href = "../cinema/payment.html?placeNumber=" + placeNumber;
    }
}
