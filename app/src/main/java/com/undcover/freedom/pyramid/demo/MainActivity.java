package com.undcover.freedom.pyramid.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.catvod.crawler.Spider;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import com.undcover.freedom.pyramid.PyLog;
import com.undcover.freedom.pyramid.PythonLoader;
import com.undcover.freedom.pyramid.PythonSpider;
import com.undcover.freedom.pyramid.demo.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Spider spider;
    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bindView();
    }

    private void bindView() {
        mBinding.btnConfig.setOnClickListener(this);
        mBinding.btnInit.setOnClickListener(this);
        mBinding.btnHome.setOnClickListener(this);
        mBinding.btnCategory.setOnClickListener(this);
        mBinding.btnDetail.setOnClickListener(this);
        mBinding.btnPlayer.setOnClickListener(this);
        mBinding.btnSearch.setOnClickListener(this);
        mBinding.btnProxy.setOnClickListener(this);
    }

    private void initSpider() {
        try {
            spider = PythonLoader.getInstance()
                    .setApplication(getApplication())
//                    .setAppPlatform(getApplication(),am)
//                    .getSpider("push_agent", "./py_ali.py?extend=81461f2e9d0b4ffb86612db403824b8a");
                    .getSpider("py_gitcafe", "./py_gitcafe.py");


//            PythonLoader.getInstance()
//                    .setFileStreamCallback((url, paramsMap, headerMap) -> {
//                        OKCallBack.OKCallBackDefault callBack = new OKCallBack.OKCallBackDefault() {
//                            @Override
//                            public void onFailure(Call call, Exception exc) {
//                            }
//
//                            public void onResponse(Response response) {
//                            }
//                        };
//                        OkHttpUtil.get(OkHttpUtil.defaultClient(), url, paramsMap, headerMap, callBack);
//                        return callBack.getResult().body().byteStream();
//                    })
//                    .setFileStringCallback(OkHttpUtil::string);

//                    .setFileStringCallback((url, headerMap) -> {
//                        return OkHttpUtil.string(url, headerMap);
//                    });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_config:
                new Thread(() -> {
                    String jo = "{\"sites\":[{\"key\":\"py_gitcafe\",\"name\":\"小纸条\",\"type\":3,\"api\":\"py_gitcafe\",\"searchable\":1,\"quickSearch\":1,\"filterable\":0,\"ext\":\"./py_gitcafe.py\"},{\"key\":\"push_agent\",\"name\":\"阿里\",\"type\":3,\"api\":\"py_ali\",\"searchable\":0,\"quickSearch\":0,\"filterable\":0,\"ext\":\"./py_ali.py?extend=81461f2e9d0b4ffb86612db403824b8a\"}],\"lives\":[],\"parses\":[],\"ijk\":[],\"ads\":[]}";
                    PythonLoader.getInstance()
                            .setApplication(getApplication())
                            .setConfig(jo);
                }).start();
                break;
            case R.id.btn_init:
                initSpider();
                break;
            case R.id.btn_home: {
                new Thread(() -> {
                    String tmp = spider.homeContent(false);
                }).start();
            }
            break;
            case R.id.btn_category: {
                new Thread(() -> {
                    String tmp = spider.categoryContent("tid", "1", false, new HashMap<>());
                }).start();
            }
            break;
            case R.id.btn_detail: {
                new Thread(() -> {
                    List<String> list = new ArrayList<>();
                    list.add("https://www.aliyundrive.com/s/JDD7fgtcYtH");
                    String tmp = spider.detailContent(list);
                }).start();
            }
            break;
            case R.id.btn_player: {
                new Thread(() -> {
                    String tmp = spider.playerContent("AliYun原画", "JDD7fgtcYtH+eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjdXN0b21Kc29uIjoie1wiZG9tYWluX2lkXCI6XCJiajI5XCIsXCJzaGFyZV9pZFwiOlwiSkREN2ZndGNZdEhcIixcImNyZWF0b3JcIjpcIjczYjViNzk4ZDlkODQ3NjQ4MmIxODBkNmI4MWI5N2Q3XCIsXCJ1c2VyX2lkXCI6XCJhbm9ueW1vdXNcIn0iLCJjdXN0b21UeXBlIjoic2hhcmVfbGluayIsImV4cCI6MTY2NTQ4MDYwNywiaWF0IjoxNjY1NDczMzQ3fQ.YgYQGq7BJ3UbAkVhSeJsX8ZLlXOUV9xqDs38YAP3MMhnC7eZA4BsnJbrp8YwGd8X7QbifCSNn5-0gCH6d23lGt8g6DqNdIK4CBHLlsjMQChnjImrZ_6cwQRGm5iu7aa2H-X_IvDmc1G6_9I0yGyklEQJtMjlBNvIx9iQJeqauXI+62f0abf79d64ba44ef834558a1fae7d300e5e9cf+video", new ArrayList<>());
                }).start();
            }
            break;
            case R.id.btn_search: {
                new Thread(() -> {
                    String tmp = spider.searchContent("", true);
                }).start();
            }
            break;

            case R.id.btn_proxy: {
                new Thread(() -> {
                    Object[] tmp = ((PythonSpider) spider).proxyLocal(new HashMap());
                }).start();
            }
            break;
        }
    }
}