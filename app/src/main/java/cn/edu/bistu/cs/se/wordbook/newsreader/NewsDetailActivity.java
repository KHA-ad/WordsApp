package cn.edu.bistu.cs.se.wordbook.newsreader;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.bistu.cs.se.wordbook.R;

public class NewsDetailActivity extends AppCompatActivity {

    public static final String DetailUrl = "detailUrl";//键
    private static String newsDetailUrl;//值
    public static final String DetailTitle = "title";//键
    private static String newsDetailTitle;//值
    public static final String NewsSummery = "summery"; //键
    private static String newsSummery; //值


    //获取数据成功
    private final static int GET_DATA_SUCCEED = 1;

    public TextView newsDetail;
    public List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化视图
        initView();
        //初始化数据
        initData();

        Intent intent = getIntent();
        //新闻标题地址获取
        newsDetailTitle = intent.getStringExtra(DetailTitle);
        //新闻详细内容地址获取
        newsDetailUrl = intent.getStringExtra(DetailUrl);
        //新闻概要内容获取
        newsSummery = intent.getStringExtra(NewsSummery);
    }
    public void initView() {
        list = new ArrayList<String>();
        newsDetail = (TextView) findViewById(R.id.txt_news_detail);
    }

    public void initData() {
        //开启一个线程执行耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取网络数据
                String result = CommonTool.getRequest(newsDetailUrl, "utf-8");

                Log.d("结果------------->", result);
                Log.d("Runnable: ",result);
                //解析详细新闻数据
                List<String> list = Function.parseHtmlNewsDetailData(result);
                StringBuffer sb = new StringBuffer();
                if(list.get(0).equals("-1")){//无内容
                    sb.append(newsSummery);
                }else{
                for (int i = 0; i <list.size() ; i++) {//list.size()
                    sb.append(list.get(i));
                }
                }
                StringBuffer str =  new StringBuffer();
                str.append(newsDetailTitle +"\n" + sb);
                mHandler.sendMessage(mHandler.obtainMessage(GET_DATA_SUCCEED, str));
            }
        }).start();

    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA_SUCCEED:
                    StringBuffer detail = (StringBuffer) msg.obj;
                    //设置不同字体
                    final SpannableStringBuilder sp = new  SpannableStringBuilder(detail);
                    sp.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, newsDetailTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //粗体
                    sp.setSpan(new AbsoluteSizeSpan(70), 0, newsDetailTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//字体大小为70像素
                    sp.setSpan(new AbsoluteSizeSpan(50), newsDetailTitle.length(), detail.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体大小为50像素
                    newsDetail.setText(sp);
                    break;
            }
        }
    };
    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }
}
