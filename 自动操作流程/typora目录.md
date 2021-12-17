result=$(cat $1 | grep -o typora-export-content )
if [[ $result ]];then
    catInfo='<script>var content = document.getElementsByClassName("typora-export-content")[0];if (content != null) {content.style = "height: 100vh; display: flex";    var write = document.getElementById("write");    write.style = "flex:1; overflow:auto;";    var toc = document.getElementsByClassName("md-toc")[0];    toc.style = "overflow:auto;padding-right:30px;";    write.removeChild(toc);    content.insertBefore(toc, write);}<\/script><\/body>'
    sed -i '' "s/<\/body>/${catInfo}/g" $1
fi