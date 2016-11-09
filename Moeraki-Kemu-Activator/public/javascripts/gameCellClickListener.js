var list = document.getElementsByTagName("th");
function changeinnerhtml() {
    var classname = this.className;
    this.innerHTML = "onclick'd";
}
for (i = 0, len = list.length; i < len; i++){
    list[i].onclick = changeinnerhtml;
}
