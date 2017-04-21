package com.omnihealthgroup.reshining.diet.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import com.omnihealthgroup.reshining.custom.IO.FileReadWrite;
import com.omnihealthgroup.reshining.custom.adapter.LazyViewPager;
import com.omnihealthgroup.reshining.custom.adapter.NoScrollViewPager;
import com.omnihealthgroup.reshining.custom.adapter.ViewPagerAdapter;
import com.omnihealthgroup.reshining.diet.CameraActivity;
import com.omnihealthgroup.reshining.diet.R;
import com.omnihealthgroup.reshining.diet.adapter.GalleryAdapter;
import com.omnihealthgroup.reshining.diet.dao.FoodDefinition;
import com.omnihealthgroup.reshining.diet.dao.FoodDefinitionDAO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**  我的健康飲食首頁
 *
 *
 */

public class DietFragment extends DrawerFrameworkMainFragment {
    private static final String TAG ="DietFragment" ;
    private View dietView;
    private static SharedPreferences prf;
    private ArrayList<String> titleChannel;

    private static String[] FRAGMENT_NAME_ARRAY = new String[]{
            BreakFastFragment.class.getName()
            , DessertFragment.class.getName()
            , LunchFragment.class.getName()
            , AfternoonTeaFragment.class.getName()
            , DinnerFragment.class.getName()
            , SupperFragment.class.getName()};

    private ArrayList<Fragment> fragmentList;
    private RadioGroup tabButtonGroup;
    private HorizontalScrollView tabScrollBar;
    private NoScrollViewPager mainViewPager;
    private static ViewPagerAdapter pagerAdapter;
    private static String dating_tpye = "BreakFastFragment";
    private static TextView dateview=null;

    private final String DATABASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HealthCare";
    private final String NO_SDCARD_DATABASE_PATH = "/data/data/com.omnihealthgroup/databases/";
    private final String DATABASE_FILENAME = "FoodDefinition.txt";
    private FileReadWrite frw;
    private boolean isClickFab;
    private FloatingActionButton fab,fab_add,fab_camera;
    private static RecyclerView recyclerView;
    private static GalleryAdapter adapter;
    private static ArrayList<Bitmap> data;
    private static File sdRoot;
    private static String path;
    private static Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleChannel=new ArrayList<>();
        fragmentList=new ArrayList<>();
        frw=new FileReadWrite();

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        if (prf.getString("day_dating", "").getBytes().length < 1) {
            initFileData();
        }

        String timeStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        prf.edit().putString("day_dating", timeStr).commit();



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context=container.getContext();
        dietView=inflater.inflate(R.layout.fragment_diet,container,false);
        fab = (FloatingActionButton) dietView.findViewById(R.id.fab);
        fab_add = (FloatingActionButton) dietView.findViewById(R.id.fab_add);
        fab_camera = (FloatingActionButton) dietView.findViewById(R.id.fab_camera);
        recyclerView= (RecyclerView) dietView.findViewById(R.id.diet_gallery);

        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (!isClickFab)
              {
                  fab_add.setVisibility(View.VISIBLE);
                  fab_camera.setVisibility(View.VISIBLE);

              } else{
                  fab_add.setVisibility(View.INVISIBLE);
                  fab_camera.setVisibility(View.INVISIBLE);

              }
              isClickFab=!isClickFab;

            }
        });

        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(getContext(), CameraActivity.class);
                it.putExtra("dateview", dateview.getText().toString());
                it.putExtra("dating_tpye",dating_tpye);
                getContext().startActivity(it);
            }
        });

        Log.v(TAG,"diet");
        getTitleChannel();
        getFragmentsList();
        initTabButton();
        initView();

        return dietView ;
    }


    @Override
    public void onResume() {
        super.onResume();
        showGallery();

        Log.v(TAG,"onResume");

    }

    public  static void showGallery() {
        data=new ArrayList<>();
        sdRoot= Environment.getDataDirectory();

//        path=sdRoot.getPath()+"/pics/"+dateview.getText().toString()+"/"+dating_tpye+"/";
        path=context.getFilesDir()+"/pics/"+dateview.getText().toString()+"/"+dating_tpye+"/";
         Log.v(TAG,path);

        File dir=new File(path);
        try {

            //檔案存在才show，不在就不秀
            if (new File(path).exists() && dir.listFiles().length>0) {
//                 Log.v("FileCount","count "+dir.listFiles().length);
                recyclerView.setVisibility(View.VISIBLE);
                // 掃資料夾
                for (File f : dir.listFiles()) {
                    FileInputStream fin = new FileInputStream(f);
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(fin);

                        Toast.makeText(context,""+ bitmap.getByteCount(),Toast.LENGTH_LONG  ).show();

                                data.add(bitmap);
                    }catch (Exception e)
                    {
                        Log.v(TAG,e.toString());
                    }
                }

                adapter = new GalleryAdapter(context, data);
                recyclerView.setAdapter(adapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());


            } else
                recyclerView.setVisibility(View.GONE);
        }catch (Exception e){}
        finally {

        }
    }

    @Override
    protected void onSetToolbar(Toolbar toolbar) {
        super.onSetToolbar(toolbar);
        toolbar.setTitle(getString(R.string.fragment_myhealthdating));
    }

    private void initView() {
        dateview = (TextView) dietView.findViewById(R.id.dateview);
        dateview.setText(prf.getString("day_dating", ""));

        //todo 統計圖表
        dietView.findViewById(R.id.layout_chart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatingChartFragment datingChartFragment = new DatingChartFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_myhealthdating, datingChartFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });



        //日期button
        dateview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTruitonDatePickerDialog(v);
                Toast.makeText(getContext(),dateview.getText().toString(),Toast.LENGTH_LONG).show();
                showGallery();

            }
        });



        //新增button，自sharePreference 中取得設定的年月日，及點選的RadioButton後
        //包覆在bundle 中傳給DialogFragment 設定飲食

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DayEventYear", Integer.parseInt(prf.getString("day_dating", "").substring(0, 4)));
                bundle.putInt("DayEventMonth", Integer.parseInt(prf.getString("day_dating", "").substring(5, 7)));
                bundle.putInt("DayEventDate", Integer.parseInt(prf.getString("day_dating", "").substring(8, 10)));
                bundle.putString("dating_tpye", dating_tpye);

                DialogFragment dialogFragment = new DatingShowFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "DatingShowFragment");
            }
        });

        dietView.findViewById(R.id.add_new_dating_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DayEventYear", Integer.parseInt(prf.getString("day_dating", "").substring(0, 4)));
                bundle.putInt("DayEventMonth", Integer.parseInt(prf.getString("day_dating", "").substring(5, 7)));
                bundle.putInt("DayEventDate", Integer.parseInt(prf.getString("day_dating", "").substring(8, 10)));
                bundle.putString("dating_tpye", dating_tpye);

                DialogFragment dialogFragment = new DatingShowFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "DatingShowFragment");
            }
        });
    }







    /**
     * 取得各項目的標題清單
     *
     * @return
     */
    private ArrayList<String> getTitleChannel() {
        titleChannel.add(getString(R.string.tabpage_11));
        titleChannel.add(getString(R.string.tabpage_12));
        titleChannel.add(getString(R.string.tabpage_13));
        titleChannel.add(getString(R.string.tabpage_14));
        titleChannel.add(getString(R.string.tabpage_15));
        titleChannel.add(getString(R.string.tabpage_16));
        return titleChannel;
    }

    /**
     * 取得各項目的頁面清單
     *
     * @return fragmentList
     */
    private List<Fragment> getFragmentsList() {
        for (int i = 0; i < FRAGMENT_NAME_ARRAY.length; i++) {
            fragmentList.add(Fragment.instantiate(getActivity(), FRAGMENT_NAME_ARRAY[i]));
        }
        return fragmentList;
    }

    /**
     * 初始化TabButton 動態新增TabButton
     */
    private void initTabButton() {
        tabButtonGroup = (RadioGroup) dietView.findViewById(R.id.tab_btn_group);
        tabScrollBar = (HorizontalScrollView) dietView.findViewById(R.id.tab_scrollbar);

        for (int i = 0; i < titleChannel.size(); i++) {
            RadioButton addTabButton = (RadioButton) LayoutInflater
                    .from(getActivity())
                    .inflate(R.layout.tab_button_custom, null);
            addTabButton.setId(i);
            addTabButton.setText(titleChannel.get(i));
            RadioGroup.LayoutParams params = new RadioGroup
                    .LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT
                    , RadioGroup.LayoutParams.MATCH_PARENT);
            tabButtonGroup.addView(addTabButton, params);
        }
        ((RadioButton) tabButtonGroup.getChildAt(0)).setTextColor(0xFFFFFFFF);
        tabButtonGroup.check(0);
        for (int i = 1; i < tabButtonGroup.getChildCount(); i++) {
            // 判斷是否為選中的button，選中的文字為白色未選中的為灰色

            ((RadioButton) tabButtonGroup.getChildAt(i)).setTextColor(0xFFBDD79E );  //灰色
        }

        Log.v(TAG,"initTabButton");

        initViewPager(); //初始化ViewPager
    }



    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        //        mainViewPager = (ViewPager) rootView.findViewById(R.id.main_viewpager);
        mainViewPager = (NoScrollViewPager) dietView.findViewById(R.id.main_viewpager);
        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList);
        mainViewPager.setAdapter(pagerAdapter);
        mainViewPager.setCurrentItem(0);

        //初始化監聽
        initHandler();

    }

    /**
     * 初始化TabButton、ViewPager的監聽器
     */
    private void initHandler() {

        //TabButton
        tabButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mainViewPager.setCurrentItem(checkedId);
            }
        });

        //ViewPager
         mainViewPager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabScroll(position);

                /* 頁面切換 */
                switch (position) {
                    case 0:
                        dating_tpye = "BreakFastFragment";
                        Log.v(TAG + "position", "BreakFastFragment");
                        break;
                    case 1:
                        dating_tpye = "DessertFragment";
                        Log.v(TAG + "position", "DessertFragment");
                        break;
                    case 2:
                        dating_tpye = "LunchFragment";
                        Log.v(TAG + "position", "LunchFragment");
                        break;
                    case 3:
                        dating_tpye = "AfternoonTeaFragment";
                        Log.v(TAG + "position", "AfternoonTeaFragment");
                        break;
                    case 4:
                        dating_tpye = "DinnerFragment";
                        Log.v(TAG + "position", "DinnerFragment");
                        break;
                    case 5:
                        dating_tpye = "SupperFragment";
                        Log.v(TAG + "position", "SupperFragment");
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                showGallery();
            }
        });
    }

    /**
     * 設置TabButton滑動變化
     *
     * @param position 選中的Tab參數
     */
    private void setTabScroll(int position) {
        RadioButton setTabButton = (RadioButton) tabButtonGroup.getChildAt(position);
        for (int i = 0; i < tabButtonGroup.getChildCount(); i++) {
            // 判斷是否為選中的button，選中的文字為白色未選中的為灰色
            if (i == position) {
                setTabButton.setTextColor(0xFFFFFFFF);  //白色
            } else {
                ((RadioButton) tabButtonGroup.getChildAt(i)).setTextColor(0xFFBDD79E);  //灰色
            }
        }

        setTabButton.setChecked(true);
        int left = setTabButton.getLeft();
        int width = setTabButton.getMeasuredWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        super.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int len = left + width / 2 - screenWidth / 2;
        tabScrollBar.smoothScrollTo(len, 0);    //滑動ScrollView
    }



    //呼叫DatePickerFragment
    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");

    }



    // 將Res 資料夾中的 食物定義的欄位轉到資料庫
    //
    private void initFileData() {
        File txtFile = null;
        String txtStr = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.v(TAG, "Has SDCard");
            try {
                boolean b = false;
                //取得資料庫的完整路徑
                String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
                //Log.i(TAG, "databaseFilename : " + databaseFilename);
                //將資料庫文件從資源文件放到合適地方（資源文件也就是資料庫文件放在項目的res下的raw目錄中）
                //將資料庫文件複製到SD卡中
                File dir = new File(DATABASE_PATH);
                if (!dir.exists()) {
                    //Log.i(TAG,"MakeDir=" + dir.getAbsolutePath());
                    b = dir.mkdir();
                }
                //判斷是否存在該文件
                if (!(new File(databaseFilename)).exists()) {
                    //Log.i(TAG,"Database file=" + databaseFilename);
                    //若不存在則取得資料庫輸入串流對象
                    InputStream is = getContext().getResources().openRawResource(R.raw.fooddefinition);
                    //新建輸出串流
                    FileOutputStream fos = new FileOutputStream(databaseFilename);
                    //將資料輸出
                    byte[] buffer = new byte[8192];
                    int count = 0;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    // 關閉資源
                    fos.close();
                    is.close();
                }

                txtFile = new File(dir, "FoodDefinition.txt");
                txtStr = frw.readFromFile(txtFile);
                Log.v(TAG, txtStr);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            Log.v(TAG, "No SDCard");
            try {
                boolean b = false;
                //取得資料庫的完整路徑
                String databaseFilename = NO_SDCARD_DATABASE_PATH + DATABASE_FILENAME;
                //Log.i(TAG, "databaseFilename : " + databaseFilename);
                //將資料庫文件從資源文件放到合適地方（資源文件也就是資料庫文件放在項目的res下的raw目錄中）
                //將資料庫文件複製到手機裡
                File dir = new File(NO_SDCARD_DATABASE_PATH);
                if (!dir.exists()) {
                    //Log.i(TAG,"MakeDir=" + dir.getAbsolutePath());
                    b = dir.mkdir();
                }
                //判斷是否存在該文件
                if (!(new File(databaseFilename)).exists()) {
                    //Log.i(TAG,"Database file=" + databaseFilename);
                    //若不存在則取得資料庫輸入串流對象
                    InputStream is = getContext().getResources().openRawResource(R.raw.fooddefinition);
                    //新建輸出串流
                    FileOutputStream fos = new FileOutputStream(databaseFilename);
                    //將資料輸出
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    //關閉資源
                    fos.close();
                    is.close();

                    txtFile = new File(dir, "FoodDefinition.txt");
                    txtStr = frw.readFromFile(txtFile);
                    Log.v(TAG, txtStr);
                }
                //取得SQLDatabase對象
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (txtStr != null) {
            try {
                FoodDefinitionDAO foodDefinitionDAO = new FoodDefinitionDAO(getContext());

                JSONObject jsonObject = new JSONObject(txtStr);
                JSONArray jsonArray = jsonObject.getJSONArray("工作表1");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    FoodDefinition foodDefinition = new FoodDefinition();
                    foodDefinition.setType(jsonArray.getJSONObject(i).getString("食品分類"));
                    foodDefinition.setName(jsonArray.getJSONObject(i).getString("樣品名稱"));
                    foodDefinition.setContent(jsonArray.getJSONObject(i).getString("內容物描述"));
                    foodDefinition.setUnit(jsonArray.getJSONObject(i).getString("單位"));
                    //                    foodDefinition.setAmount(jsonArray.getJSONObject(i).getString("食品分類"));
                    foodDefinition.setAmountunit(jsonArray.getJSONObject(i).getString("每食物單位(g/ml)"));
                    foodDefinition.setRefimgsn(jsonArray.getJSONObject(i).getString("每食物單位參考圖"));
                    foodDefinition.setMoisture(jsonArray.getJSONObject(i).getString("每單位熱量(kcal)"));
                    foodDefinition.setProtein(jsonArray.getJSONObject(i).getString("每單位蛋白質(g)"));
                    foodDefinition.setFat(jsonArray.getJSONObject(i).getString("每單位脂肪(g)"));
                    foodDefinition.setSugar(jsonArray.getJSONObject(i).getString("每單位碳水化合物(g)"));
                    foodDefinition.setNote(jsonArray.getJSONObject(i).getString("食物種類"));
                    foodDefinition.setStatus("1");
                    foodDefinition.setCrTime(timeStr);
                    //                    foodDefinition.setMdTime(jsonArray.getJSONObject(i).getString("食品分類"));

                    foodDefinitionDAO.insert(foodDefinition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /*繼承Dialog 並實作 OnDateSetListener
      取得日期 塞入SharePreference 後供dateView 存取
     */
    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user(顯示在EditView中Month要加一)
            dateview.setText(year + "-" + (month + 1) + "-" + day);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(dateview.getText().toString());
                String timeStr = formatter.format(date);
                dateview.setText(timeStr);
                prf.edit().putString("day_dating", timeStr).commit();

                pagerAdapter.notifyDataSetChanged();
                showGallery();



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
