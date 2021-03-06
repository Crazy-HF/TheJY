package com.example.thejiyu.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptIntrinsicColorMatrix;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thejiyu.R;
import com.example.thejiyu.adapter.DividerItemDecoration;
import com.example.thejiyu.adapter.SimpleAdapter;
import com.example.thejiyu.view.ImageCycleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

/**
 * Created by 峰 on 2016/6/5.
 */
public class FirstFragment extends Fragment {
    private static final int REFRESE_COMPLETE = 0x110;
    private SwipeRefreshLayout mSwipeLayout;
    private ImageCycleView mImageCycleView;
    private RecyclerView recyclerView;
    private SimpleAdapter simpleAdapter;
    boolean isLoading;
    private List<String> data = new ArrayList<>();
    private Handler handler = new Handler();
    private LinearLayoutManager linearLayoutManager;
    public FirstFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);
        //初始化view
        initView(view);
        //设置轮播图
//        initCycle();
        //设置Recyclerview
        initRecycler();
        //设置下拉刷新
        initSwipe();
        //获取数据
        initData();


        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        mImageCycleView = (ImageCycleView)view.findViewById(R.id.icv_topView);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
    }
    //下拉刷新
    private void initSwipe() {
        mSwipeLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
//        setOnRefreshListener(OnRefreshListener):添加下拉刷新监听器
//        setRefreshing(boolean):显示或者隐藏刷新进度条
//        isRefreshing():检查是否处于刷新状态

        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
            }
        });

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        data.clear();
                        for (int i = 0; i < 2; i++) {
                            int index = i + 1;
                            data.add(0,"new item" + index);
                        }
                        simpleAdapter.notifyDataSetChanged();
                        mSwipeLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }
    //Recycler列表
    private void initRecycler() {
        simpleAdapter = new SimpleAdapter(getActivity(),data);
        recyclerView.setAdapter(simpleAdapter);
        //设置RecyclerView的布局管理
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        //设置布局
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置RecyclerView分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//              scrollState = SCROLL_STATE_TOUCH_SCROLL(1)：表示正在滚动。当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1
//              scrollState =SCROLL_STATE_FLING(2) ：表示手指做了抛的动作（手指离开屏幕前，用力滑了一下，屏幕产生惯性滑动）。
//              scrollState =SCROLL_STATE_IDLE(0) ：表示屏幕已停止。屏幕停止滚动时为0
                // 记录当前滑动状态
                Log.d("test", "StateChanged = " + newState);
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == simpleAdapter.getItemCount()) {
                    simpleAdapter.changeMoreStatus(SimpleAdapter.LOADING_MORE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 2; i++) {
                                int index = i + 1;
                                data.add("more item" + index);
                            }
                            simpleAdapter.notifyDataSetChanged();
                            simpleAdapter.changeMoreStatus(SimpleAdapter.PULLUP_LOAD_MORE);
                        }
                    }, 2500);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("test", "onScrolled");


                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                Log.d("test", "firstVisibleItemPosition：" + firstVisibleItemPosition);

//
            }
        });
        //添加点击事件
        simpleAdapter.setOnItemClickListener(new SimpleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("test", "item position = " + position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    public void initData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);

    }

    private void getData() {
        for (int i = 'A'; i < 'Z'; i++) {
            data.add(""+(char)i);
        }
        Log.d("test", "获取数据");
        simpleAdapter.notifyDataSetChanged();
        mSwipeLayout.setRefreshing(false);
//        simpleAdapter.notifyItemRemoved(simpleAdapter.getItemCount());
    }

    private void initCycle() {

//		mImageCycleView.setAutoCycle(false); //关闭自动播放
		mImageCycleView.setCycleDelayed(3000);//设置自动轮播循环时间
//
//		mImageCycleView.setIndicationStyle(ImageCycleView.IndicationStyle.COLOR,
//				Color.BLUE, Color.RED, 1f);

//		mImageCycleView.setIndicationStyle(ImageCycleView.IndicationStyle.IMAGE,
//				R.drawable.dian_unfocus, R.drawable.dian_focus, 1f);

//		Log.e("eee", Environment.getExternalStorageDirectory().getPath()+ File.separator+"a1.jpg");

        List<ImageCycleView.ImageInfo> list=new ArrayList<ImageCycleView.ImageInfo>();

        //res图片资源
        list.add(new ImageCycleView.ImageInfo(R.drawable.a1,"111111111111",""));
        list.add(new ImageCycleView.ImageInfo(R.drawable.a2,"222222222222222",""));
        list.add(new ImageCycleView.ImageInfo(R.drawable.a3,"3333333333333",""));

        //SD卡图片资源
//		list.add(new ImageCycleView.ImageInfo(new File(Environment.getExternalStorageDirectory(),"a1.jpg"),"11111",""));
//		list.add(new ImageCycleView.ImageInfo(new File(Environment.getExternalStorageDirectory(),"a2.jpg"),"22222",""));
//		list.add(new ImageCycleView.ImageInfo(new File(Environment.getExternalStorageDirectory(),"a3.jpg"),"33333",""));


        //使用网络加载图片
//		list.add(new ImageCycleView.ImageInfo("http://img.lakalaec.com/ad/57ab6dc2-43f2-4087-81e2-b5ab5681642d.jpg","11","eeee"));
//		list.add(new ImageCycleView.ImageInfo("http://img.lakalaec.com/ad/cb56a1a6-6c33-41e4-9c3c-363f4ec6b728.jpg","222","rrrr"));
//		list.add(new ImageCycleView.ImageInfo("http://img.lakalaec.com/ad/e4229e25-3906-4049-9fe8-e2b52a98f6d1.jpg", "333", "tttt"));
//        mImageCycleView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_MOVE:
//                        mSwipeLayout.setEnabled(false);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        mSwipeLayout.setEnabled(true);
//                        break;
//                }
//                return false;
//            }
//        });
		mImageCycleView.setOnPageClickListener(new ImageCycleView.OnPageClickListener() {
			@Override
			public void onClick(View imageView, ImageCycleView.ImageInfo imageInfo) {
				Toast.makeText(getActivity(), "你点击了" + imageInfo.value.toString(), Toast.LENGTH_SHORT).show();
			}
		});

        mImageCycleView.loadData(list, new ImageCycleView.LoadImageCallBack() {
            @Override
            public ImageView loadAndDisplay(ImageCycleView.ImageInfo imageInfo) {

                //本地图片
                ImageView imageView=new ImageView(getActivity());
                imageView.setImageResource(Integer.parseInt(imageInfo.image.toString()));
                return imageView;


//				//使用SD卡图片
//				SmartImageView smartImageView=new SmartImageView(MainActivity.this);
//				smartImageView.setImageURI(Uri.fromFile((File)imageInfo.image));
//				return smartImageView;

//				//使用SmartImageView，既可以使用网络图片也可以使用本地资源
//				SmartImageView smartImageView=new SmartImageView(MainActivity.this);
//				smartImageView.setImageResource(Integer.parseInt(imageInfo.image.toString()));
//				return smartImageView;

                //使用BitmapUtils,只能使用网络图片
//				BitmapUtils bitmapUtils = new BitmapUtils(MainActivity.this);
//				ImageView imageView = new ImageView(MainActivity.this);
//				bitmapUtils.display(imageView, imageInfo.image.toString());
//				return imageView;


            }
        });
    }
}
