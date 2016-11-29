package cn.edu.bistu.cs.se.wordbook.newsreader;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.bistu.cs.se.wordbook.R;


public class NewsActivity extends AppCompatActivity {
    public static final String DetailUrl = "detailUrl";//键
    public static final String DetailTitle = "title";//键
    public static final String NewsSummery = "summery"; //键

    private ListView mListView;
    private List<NewsItemModel> list;
    private NewsAdapter adapter;
    //获取数据成功
    private final static int GET_DATA_SUCCEED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //初始化视图
        initView();
        //初始化数据
        initData();
    }

    public void initView() {
        list = new ArrayList<NewsItemModel>();
        mListView = (ListView) findViewById(R.id.list_view_news);
    }


    public void initData() {
        //开启一个线程执行耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取网络数据
                String result = CommonTool.getRequest("http://english.cctv.com/news/morenews/index.shtml", "utf-8");

//                Log.d("结果------------->", result);
  //              Log.d("Runnable: ",result);
                //解析新闻数据
                List<NewsItemModel> list = Function.parseHtmlData(result);

                for (int i = 0; i <25 ; i++) {//list.size()
                    NewsItemModel model = list.get(i);
                    //获取新闻图片
                    Bitmap bitmap = BitmapFactory.decodeStream(CommonTool.getImgInputStream(list.get(i).getUrlImgAddress()));

                    model.setNewsBitmap(bitmap);
                }
                mHandler.sendMessage(mHandler.obtainMessage(GET_DATA_SUCCEED, list));
            }
        }).start();

    }


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA_SUCCEED:
                    List<NewsItemModel> list = (List<NewsItemModel>) msg.obj;
                    //新闻列表适配器
                    adapter = new NewsAdapter(NewsActivity.this, list, R.layout.adapter_news_item);
                    mListView.setAdapter(adapter);
                    //设置点击事件
                    mListView.setOnItemClickListener(new ItemClickListener());
                    Toast.makeText(getApplicationContext(), String.valueOf(list.size()), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    /**
     * 新闻列表点击事件
     */
    public class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            NewsItemModel temp =(NewsItemModel) adapter.getItem(i);
            Toast.makeText(getApplicationContext(), temp.getNewsTitle(), Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(NewsActivity.this,NewsDetailActivity.class);
    //        intent.setAction(Intent.ACTION_VIEW);
      //      intent.setData(Uri.parse(temp.getNewsDetailUrl()));
            intent.putExtra(DetailUrl,temp.getNewsDetailUrl());
            intent.putExtra(DetailTitle,temp.getNewsTitle());
            intent.putExtra(NewsSummery,temp.getNewsSummary());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }
}
