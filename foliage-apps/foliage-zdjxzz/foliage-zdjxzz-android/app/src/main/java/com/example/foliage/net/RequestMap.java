package com.example.foliage.net;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Description：请求报文
 * Created by liang.qfzc@gmail.com on 2018/6/27.
 */

public class RequestMap {

    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    public RequestMap() {}

    public RequestMap(ConcurrentHashMap<String, String> map) {
        this.map = map;
    }

}
