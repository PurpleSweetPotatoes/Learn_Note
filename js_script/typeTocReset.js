var content = document.getElementsByClassName('typora-export-content')[0];
if (content != null) {
    content.style = 'height: 100vh; display: flex';

    var write = document.getElementById('write');
    write.style = 'flex:1; overflow:auto';

    var toc = document.getElementsByClassName('md-toc')[0];
    toc.style = 'overflow:auto';

    write.removeChild(toc);
    content.insertBefore(toc, write);
}
