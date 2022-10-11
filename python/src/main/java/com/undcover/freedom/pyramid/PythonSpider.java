package com.undcover.freedom.pyramid;

import android.content.Context;
import android.net.Uri;

import com.chaquo.python.PyObject;
import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PythonSpider extends Spider {
    PyObject app;
    PyObject pySpider;
    boolean loadSuccess = false;
    private String cachePath;
    private String name;

    public PythonSpider() {
        this("/storage/emulated/0/plugin/");
    }

    public PythonSpider(String cache) {
        this("", cache);
    }

    public PythonSpider(String name, String cache) {
        this.cachePath = cache;
        this.name = name;
    }

    public void init(Context context, String url) {
        app = PythonLoader.getInstance().pyApp;
        PyObject retValue = app.callAttr("downloadPlugin", cachePath, url);
        Uri uri = Uri.parse(url);
        String extInfo = uri.getQueryParameter("extend");
        if (null == extInfo) extInfo = "";
        String path = retValue.toString();
        File file = new File(path);
        if (file.exists()) {
            pySpider = app.callAttr("loadFromDisk", path);

            List<PyObject> poList = app.callAttr("getDependence", pySpider).asList();
            for (PyObject po : poList) {
                String api = po.toString();
                String depUrl = PythonLoader.getInstance().getUrlByApi(api);
                if (!depUrl.isEmpty()) {
                    String tmpPath = app.callAttr("downloadPlugin", cachePath, depUrl).toString();
                    if (!new File(tmpPath).exists()) {
                        PyToast.showCancelableToast(api + "加载失败!");
                        return;
                    } else {
                        PyLog.d(api + ": 加载插件依赖成功！");
                    }
                }
            }
            app.callAttr("init", pySpider, extInfo);
            loadSuccess = true;
            PyLog.d(name + ": 下載插件成功！");
        } else {
            PyToast.showCancelableToast(name + "下载插件失败");
        }
    }

    public String getName() {
        if (name.isEmpty()) {
            PyObject po = app.callAttr("getName", pySpider);
            return po.toString();
        } else {
            return name;
        }
    }

    public JSONObject map2json(HashMap<String, String> extend) {
        JSONObject jo = new JSONObject();
        try {
            if (extend != null) {
                for (String key : extend.keySet()) {
                    jo.put(key, extend.get(key));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    public JSONObject map2json(Map extend) {
        JSONObject jo = new JSONObject();
        try {
            if (extend != null) {
                for (Object key : extend.keySet()) {
                    jo.put(key.toString(), extend.get(key));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    public JSONArray list2json(List<String> array) {
        JSONArray ja = new JSONArray();
        if (array != null) {
            for (String str : array) {
                ja.put(str);
            }
        }
        return ja;
    }

    public String paramLog(Object... obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("request params:[");
        for (Object o : obj) {
            sb.append(o).append("-");
        }
        sb.append("]");
        return sb.toString();
    }

    public Object[] proxyLocal(Map param) {
        PyLog.nw("localProxy", map2json(param).toString());
        List<PyObject> poList = app.callAttr("localProxy", pySpider, map2json(param).toString()).asList();
        int code = poList.get(0).toInt();
        String type = poList.get(1).toString();
        String action = poList.get(2).toString();
        String content = poList.get(3).toString();
        InputStream is = null;
        try {
            JSONObject actionJo = new JSONObject(action);
            String url = actionJo.optString("url");
            String header = actionJo.optString("header");
            String newParam = actionJo.optString("param");
            String contentType = actionJo.optString("type");
            if (contentType.equals("stream")) {
                is = PythonLoader.getInstance().getFileStream(url, newParam, header);
            } else {
                if (content.isEmpty()) {
                    content = PythonLoader.getInstance().getFileString(url, header);
                }
                content = replaceLocalUrl(content);
                is = new ByteArrayInputStream(content.getBytes());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Object[]{code, type, is};
    }

    public String replaceLocalUrl(String content) {
        String tmp = content.replace("http://127.0.0.1:UndCover/proxy", PythonLoader.getInstance().localProxyUrl());
        return tmp;
    }

    /**
     * 首页数据内容
     *
     * @param filter 是否开启筛选
     * @return
     */
    public String homeContent(boolean filter) {
        PyLog.nw("homeContent" + "-" + name, paramLog(filter));
        PyObject po = app.callAttr("homeContent", pySpider, filter);
        String rsp = po.toString();
        PyLog.nw("homeContent" + "-" + name, rsp);
        return rsp;
    }

    /**
     * 首页最近更新数据 如果上面的homeContent中不包含首页最近更新视频的数据 可以使用这个接口返回
     *
     * @return
     */
    public String homeVideoContent() {
        PyLog.nw("homeVideoContent" + "-" + name, "");
        PyObject po = app.callAttr("homeVideoContent", pySpider);
        String rsp = po.toString();
        PyLog.nw("homeVideoContent" + "-" + name, rsp);
        return rsp;
    }

    /**
     * 分类数据
     *
     * @param tid
     * @param pg
     * @param filter
     * @param extend
     * @return
     */
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        PyLog.nw("categoryContent" + "-" + name, paramLog(tid, pg, filter, map2json(extend).toString()));
        PyObject po = app.callAttr("categoryContent", pySpider, tid, pg, filter, map2json(extend).toString());
        String rsp = po.toString();
        PyLog.nw("categoryContent" + "-" + name, rsp);
        return rsp;
    }

    /**
     * 详情数据
     *
     * @param ids
     * @return
     */
    public String detailContent(List<String> ids) {
        PyLog.nw("detailContent" + "-" + name, paramLog(list2json(ids).toString()));
        PyObject po = app.callAttr("detailContent", pySpider, list2json(ids).toString());
        String rsp = po.toString();
        PyLog.nw("detailContent" + "-" + name, rsp);
        return rsp;
    }

    /**
     * 搜索数据内容
     *
     * @param key
     * @param quick
     * @return
     */
    public String searchContent(String key, boolean quick) {
        PyLog.nw("searchContent" + "-" + name, paramLog(key, quick));
        PyObject po = app.callAttr("searchContent", pySpider, key, quick);
        String rsp = po.toString();
        PyLog.nw("searchContent" + "-" + name, rsp);
        return rsp;
    }

    /**
     * 播放信息
     *
     * @param flag
     * @param id
     * @return
     */
    public String playerContent(String flag, String id, List<String> vipFlags) {
        PyLog.nw("playerContent" + "-" + name, paramLog(flag, id, list2json(vipFlags).toString()));
        PyObject po = app.callAttr("playerContent", pySpider, flag, id, list2json(vipFlags).toString());
        String rsp = replaceLocalUrl(po.toString());
        PyLog.nw("playerContent" + "-" + name, rsp);
        return rsp;
    }

    /**
     * webview解析时使用 可自定义判断当前加载的 url 是否是视频
     *
     * @param url
     * @return
     */
    public boolean isVideoFormat(String url) {
        return false;
    }

    /**
     * 是否手动检测webview中加载的url
     *
     * @return
     */
    public boolean manualVideoCheck() {
        return false;
    }
}
