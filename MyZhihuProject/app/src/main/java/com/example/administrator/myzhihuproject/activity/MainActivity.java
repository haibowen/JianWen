package com.example.administrator.myzhihuproject.activity;

import android.app.Application;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Toast;
import com.example.administrator.myzhihuproject.internet.MyHttpUtil;
import com.example.administrator.myzhihuproject.R;
import com.example.administrator.myzhihuproject.adapter.MyAdapter;
import com.example.administrator.myzhihuproject.bean.News;
import com.example.administrator.myzhihuproject.permession.RequestPermession;
import com.example.administrator.myzhihuproject.url.DataUrl;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CircleImageView circleImageView;
    private  MyAdapter myAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;



    private List<String> mtitle=new ArrayList<>();
    private List<String> mimage=new ArrayList<>();
    private  List<String> mcontent=new ArrayList<>();

    private List<News> mydata=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.nightTheme);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //侧滑设置
        drawerLayout=findViewById(R.id.drawerlayout);
        navigationView=findViewById(R.id.nav_view);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayShowTitleEnabled(false);

        }
        //数据获取

        GetInternetData(DataUrl.DEFAULTURL);
        //下拉刷新
        swipeRefreshLayout=findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        ReFresh();

        //RecyclerView
        recyclerView=findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);


        //侧滑的导航菜单的点击事件
        navigationView.setCheckedItem(R.id.nav_first);
        //navigationView.setItemIconTintList(null);
        Event();

        //侧滑的头像
        View nav_header=navigationView.inflateHeaderView(R.layout.nav_header);
        circleImageView=nav_header.findViewById(R.id.icon_image);
        ChangeImage();




    }
    //侧滑头像的点击事件
    public  void ChangeImage(){
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestPermession.CheckPermession(MainActivity.this);
                openAlbum();

            }
        });


    }


    //打开相册
    public void openAlbum(){

        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,2);

    }
    //

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode){
            case 2:
                //不同android 版本处理图片的方法不同
                if (requestCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        HandleImageUPKitKat(data);
                    }else {
                        HandleImageBelowKitKat(data);
                    }
                }

                break;
                default:
                    break;
        }
    }

    //低版本图片路径解析

    private void HandleImageBelowKitKat(Intent data) {
        Uri uri=data.getData();
        String imagepath=getImagePath(uri,null);
        displayImage(imagepath);



    }
    //图片路径的解析

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void HandleImageUPKitKat(Intent data) {

        String imagepath=null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){

            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){

                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagepath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contenturi= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagepath=getImagePath(contenturi,null);

            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagepath=getImagePath(uri,null);

        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagepath=uri.getPath();

        }
        displayImage(imagepath);


    }


//取到图片的路径
    private String getImagePath(Uri uri, String selection) {

        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();

        }
        return path;
    }

    //头像的显示
    private void displayImage(String imagepath) {
        if (imagepath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagepath);
            circleImageView.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"获取图片失败",Toast.LENGTH_SHORT).show();

        }
    }


    //申请权限

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case 1:
                if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"拒绝权限，将导致该功能不可用",Toast.LENGTH_SHORT).show();

                }
                break;

        }

    }

    //下拉刷新的事件
    public  void ReFresh(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //myAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);

                            }
                        });

                    }
                }).start();

            }
        });


    }
    //侧滑导航栏的点击事件
    public  void Event(){

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.nav_first:

                        GetInternetData(DataUrl.TOPURL);

                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_second:
                        GetInternetData(DataUrl.SHEHUIURL);

                        drawerLayout.closeDrawers();

                        break;
                    case R.id.nav_third:
                        GetInternetData(DataUrl.GUONEIURL);

                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_fourth:
                        GetInternetData(DataUrl.YULEURL);

                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_fivth:
                        GetInternetData(DataUrl.TIYUURL);

                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_six:
                        GetInternetData(DataUrl.JUNSHIURL);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_seven:
                        GetInternetData(DataUrl.KEJIURL);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_eight:
                        GetInternetData(DataUrl.CAIJINGURL);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_nine:
                        GetInternetData(DataUrl.SHISHANGURL);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.zhuti:


                        break;
                    case R.id.tuichu:
                        finish();

                        break;
                    default:
                        break;
                }


                return true;
            }
        });



    }



//顶部菜单
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.toolbar,menu);
        return  true;

    }
//菜单的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.first:
                Toast.makeText(this,"onclick",Toast.LENGTH_SHORT).show();

                break;
            case R.id.second:

                break;
            case R.id.third:

                break;


                default:
                    break;

        }
        return true;
    }

    //请求网络数据
    public  void  GetInternetData(String url){


        MyHttpUtil.SendRequestWithOkHttp(url,
                new okhttp3.Callback()  {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                String ResponseData=response.body().string();
                Log.d("jiang", "onResponse: "+ResponseData);

                DealWithResponseData(ResponseData);

            }
        });
    }
    //解析数据
    public  void DealWithResponseData(String ResponseData){
        JSONObject jsonObject= null;
        if (mcontent!=null){
            mcontent.clear();
            mtitle.clear();
            mimage.clear();
            mydata.clear();
        }
        try {
            jsonObject = new JSONObject(ResponseData);
            Log.e("2222", "DealWithResponseData: "+jsonObject );


            JSONObject jsonObject1=jsonObject.getJSONObject("result");
            JSONArray jsonArray=jsonObject1.getJSONArray("data");


            Log.e("1234", "DealWithResponseData: "+jsonArray );

            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject2= (JSONObject) jsonArray.get(i);
                String title=jsonObject2.getString("title");
                Log.d("0000000", "DealWithResponseData: "+title);

                String image=jsonObject2.getString("thumbnail_pic_s");

                Log.e("2222", "DealWithResponseData: "+image );
                String url=jsonObject2.getString("url");
                Log.e("1111111111111", "DealWithResponseData: "+url );


                    mcontent.add(url);
                    mtitle.add(title);
                    mimage.add(image);


                GetDataSource(mtitle);



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    //适配器的数据源
    public  void GetDataSource(final List<String> mtitle){


            News [] news=new News[mtitle.size()];
            for (int i=0;i<mtitle.size();i++){


                news[i]=new News(mtitle.get(i),mimage.get(i),mcontent.get(i));

                Log.e("888888", "GetDataSource: "+news[i].getTitle() );
                Log.e("99999", "GetDataSource: "+news[i].getImageid() );
                Log.d("dong", "GetDataSource: "+news[i].getTitle());


                mydata.add(news[i]);


        }



      ShowResult();

    }
    public void ShowResult(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyAdapter myAdapter=new MyAdapter(mydata);
                myAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(myAdapter);
            }
        });
    }
}
