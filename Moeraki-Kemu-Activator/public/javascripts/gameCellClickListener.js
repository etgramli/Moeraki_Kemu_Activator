var list = document.getElementsByTagName("th");
function changeinnerhtml() {
this.innerHTML = "onclick'd";
}
for (i = 0, len = list.length; i < len; i++){
    list[i].onclick = changeinnerhtml;
}
