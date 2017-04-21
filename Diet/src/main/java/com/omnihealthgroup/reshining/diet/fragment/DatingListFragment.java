package com.omnihealthgroup.reshining.diet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.omnihealthgroup.reshining.diet.AddNewDatingActivity;
import com.omnihealthgroup.reshining.diet.R;
import com.omnihealthgroup.reshining.diet.adapter.DatingListAdapter;
import com.omnihealthgroup.reshining.diet.dao.FoodDefinition;
import com.omnihealthgroup.reshining.diet.dao.FoodDefinitionDAO;

import java.util.List;




/* 飲食分類按下後彈跳出食品列表
*
*
*
* */



public class DatingListFragment extends DialogFragment {
    private static final String TAG = "DatingListFragment";
    private View rootView;
//    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;
    private EditText serachText;
    private ListView listView;
    private ImageButton dialogSearchEventBtn;
    private TextView dialogDateText, dialog_show_type;
    private List<FoodDefinition> searchDayEventList;
    private String dating_tpye = null , timeStr = null;
    private boolean isClickFab=false;

    //    private DialogEventListAdapter dialogEventListAdapter;
    private DatingListAdapter datingListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prf = getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
//        // TODO: 2016/08/11  是否有用處需再確認
//        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialogfragment_list_dating, container, false);
        Log.v(TAG, "onCreateView");

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //editview 不會自動跳出鍵盤 in DialogFragment
        initView();
        initListView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        // 監聽返回鍵
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button
                    Bundle bundle = new Bundle();

                    bundle.putInt("DayEventYear", Integer.parseInt(prf.getString("day_dating", "").substring(0, 4)));
                    bundle.putInt("DayEventMonth", Integer.parseInt(prf.getString("day_dating", "").substring(5, 7)));
                    bundle.putInt("DayEventDate", Integer.parseInt(prf.getString("day_dating", "").substring(8, 10)));
                    bundle.putString("dating_tpye", dating_tpye);

                    Toast.makeText(getContext(),"handle back",Toast.LENGTH_SHORT).show();

                    //返回上一頁
                    DialogFragment dialogFragment = new DatingShowFragment();
                    dialogFragment.setArguments(bundle);
                    dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
                    dialogFragment.show(getActivity().getSupportFragmentManager(), "DatingShowFragment");
                    dismiss();

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
    }

    private void initView() {
        dialogSearchEventBtn = (ImageButton) rootView.findViewById(R.id.dialog_search_btn);
        //  取得由DatingShowFragment 傳出的年月日及餐點種類，早、午、晚餐....
        dialogDateText = (TextView) rootView.findViewById(R.id.dialog_show_date);
        dialogDateText.setText(String.format("%s", getArguments().getInt("DayEventMonth") + "月" + getArguments().getInt("DayEventDate") + "日") + " " + dialogDateText.getText());


        //由DatingShowFragment 所點選的食品種類
        dialog_show_type = (TextView) rootView.findViewById(R.id.dialog_show_type);
//        Log.v(TAG,getArguments().getString("FoodDefinitionDAO_type"));
        if (getArguments().getString("FoodDefinitionDAO_type") != null) {
            dialog_show_type.setVisibility(View.VISIBLE);
            dialog_show_type.setText("<< " + getArguments().getString("FoodDefinitionDAO_type") + " >>");
        } else {
            dialog_show_type.setVisibility(View.GONE);
        }

        serachText = (EditText) rootView.findViewById(R.id.dialog_show_data);
        if (getArguments().getString("SerachText") != null) {
            serachText.setText(getArguments().getString("SerachText"));
        }

        listView = (ListView) rootView.findViewById(R.id.dialog_event_list);

         timeStr = prf.getString("day_dating", "");
    }

    private void initListView() {
        final FoodDefinitionDAO foodDefinitionDAO = new FoodDefinitionDAO(getContext());
        if (getArguments().getString("FoodDefinitionDAO_type") != null) {
            Log.v(TAG, getArguments().getString("FoodDefinitionDAO_type"));
            searchDayEventList = foodDefinitionDAO.getfood_type_data(getArguments().getString("FoodDefinitionDAO_type"));
        } else {
            searchDayEventList = foodDefinitionDAO.getfood_type_data(null);
        }

        if (getArguments().getString("dating_tpye") != null) {
            dating_tpye = getArguments().getString("dating_tpye");
            Log.v(TAG, dating_tpye);
        }

        datingListAdapter = new DatingListAdapter(getContext(), searchDayEventList, dating_tpye , timeStr);
        listView.setAdapter(datingListAdapter);


        if (serachText.getText().toString().trim() != "") {
            datingListAdapter.getFilter().filter(serachText.getText());
        }

        // Search Button.....
        dialogSearchEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchDayEventList = foodDefinitionDAO.getfood_type_data(null);
                //隱藏食品種類
                dialog_show_type.setVisibility(View.GONE);

                //重新 List
                datingListAdapter = new DatingListAdapter(getContext(), searchDayEventList, dating_tpye , timeStr);
                listView.setAdapter(datingListAdapter);

                //如果search text 不為空白，以search text 的值進行Listview filter
                if (serachText.getText().toString().trim() != "") {
                    datingListAdapter.getFilter().filter(serachText.getText());
                }
            }
        });

        // Add Text Change Listener to EditText
        //        listView.setTextFilterEnabled(true);
        // search text 變動時，針對ListView 進行filter
        serachText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                //                datingListAdapter.getFilter().filter(s.toString());

                datingListAdapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // TODO: 2016/08/12
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Bundle bundle = new Bundle();

                bundle.putInt("DayEventYear", Integer.parseInt(prf.getString("day_dating", "").substring(0, 4)));
                bundle.putInt("DayEventMonth", Integer.parseInt(prf.getString("day_dating", "").substring(5, 7)));
                bundle.putInt("DayEventDate", Integer.parseInt(prf.getString("day_dating", "").substring(8, 10)));
                bundle.putString("dating_tpye", dating_tpye);

                bundle.putSerializable("FoodDefinitionDAO", searchDayEventList.get(position));

                Intent intent = new Intent();
                intent.setClass(getActivity(), AddNewDatingActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                dismiss();
            }
        });
    }


}
