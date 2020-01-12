#!/bin/bash
#-*- coding=utf-8 -*-

ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

# 10进制转为62进制
def base62_encode(num, alphabet=ALPHABET):
    """Encode a number in Base X
    `num`: The number to encode
    `alphabet`: The alphabet to use for encoding
    """
    if (num == 0):
        return alphabet[0]
    arr = []
    base = len(alphabet)
    while num:
        rem = num % base
        num = num // base
        arr.append(alphabet[rem])
    arr.reverse()
    return ''.join(arr)

# 62进制转为10进制
def base62_decode(string, alphabet=ALPHABET):
    """Decode a Base X encoded string into the number
    Arguments:
    - `string`: The encoded string
    - `alphabet`: The alphabet to use for encoding
    """
    base = len(alphabet)
    strlen = len(string)
    num = 0
    idx = 0
    for char in string:
        power = (strlen - (idx + 1))
        num += alphabet.index(char) * (base ** power)
        idx += 1
    return num

# mid转换为id
def mid2id(mid):
    mid = str(mid)[::-1]
    size = int(len(mid) / 7) if len(mid) % 7 == 0 else int(len(mid) / 7 + 1)
    result = []
    for i in range(size):
        s = mid[i * 7: (i + 1) * 7][::-1]
        s = base62_encode(int(s))
        s_len = len(s)
        if i < size - 1 and len(s) < 4:
            s = '0' * (4 - s_len) + s
        result.append(s)
    result.reverse()
    return ''.join(result)

# id转换为mid
def id2mid(id):
    id = str(id)[::-1]
    size = int(len(id) / 4) if len(id) % 4 == 0 else int(len(id) / 4 + 1)
    result = []
    for i in range(size):
        s = id[i * 4: (i + 1) * 4][::-1]
        s = str(base62_decode(str(s)))
        s_len = len(s)
        if i < size - 1 and s_len < 7:
            s = (7 - s_len) * '0' + s
        result.append(s)
    result.reverse()
    return ''.join(result)

# 运行入口
if __name__ == '__main__':
    print ('mdi2id: ' + mid2id('4404101091169383'))
    print ('id2mid: ' + id2mid('I1IGF4Ud1'))
    print('something')
    wait = input('PRESS ENTER TO CONTINUE.')
    print('something')