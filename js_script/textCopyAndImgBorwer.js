var eles = document.getElementsByTagName('*');
for (var i = 0; i < eles.length; i++) {
    eles[i].style.userSelect = 'text';
}

// 移除放大视图
var restore = function () {
    document.body.removeChild(document.getElementById("overlay"));
}

var bbimg = function () {
  var zoom = parseInt(this.style.zoom) || 50;
  zoom += event.wheelDelta / 12;
  if (zoom > 20 && zoom <= 100) this.style.zoom = zoom + '%';
  return false;
}

var imgs = document.getElementsByTagName('img');

// 遍历图片元素
for (i = 0; i < imgs.length; i++) {
    var img = imgs[i];
    if (img.onclick != null || img.width < 80 || img.height < 80) {
        continue
    }
    // 添加事件
    img.onclick = function() {

        var overlay = document.createElement("div");
        overlay.setAttribute("id","overlay");
        overlay.setAttribute("class","overlay");
        overlay.style.cssText="background-color:#000;opacity:1;filter:alpha(opacity=100);position: fixed;top:0;left:0;width:100%;height:100%;z-index: 1000;overflow:auto;display: flex";
        document.body.appendChild(overlay);

        var img = document.createElement("img");
        img.setAttribute("id","expand")
        img.setAttribute("class","overlayimg");
        img.src = this.getAttribute("src");
        img.style.cssText = "zoom:50%;margin:auto;"
        img.onmousewheel = bbimg
        document.getElementById("overlay").appendChild(img);

        img.onclick = restore;
    }
}