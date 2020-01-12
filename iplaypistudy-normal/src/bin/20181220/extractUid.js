function idx(c) {
    c = c.charCodeAt();
    if (c >= 48 && c <= 57) return c - 48;
    if (c >= 97 && c <= 122) return c - 97 + 10;
    return c - 56 + 36;
}

function extractUid(url) {
    url = url.replace(/\.\w+$/g, '');
    // 提取文件名
    var hash = url.match(/[0-9a-zA-Z]{32}$/);
    if (hash === null) return '';
    // 截取前8位
    hash = hash[0].slice(0, 8);
    var uid = 0;
    // 16进制或者62进制
    if (hash[0] == '0' && hash[1] == '0') k = 62;
    else k = 16;
    // 每一个数字都转为10进制
    for (i = 0; i < 8; i++) uid = uid * k + idx(hash[i]);
    return uid;
}

// 博客内容:https://www.playpi.org/2018122001.html