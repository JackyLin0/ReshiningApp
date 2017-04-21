package com.omnihealthgroup.reshining.schedule;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.omnihealthgroup.reshining.custom.Util.WebServiceConnection;
import com.omnihealthgroup.reshining.custom.object.Clinic;
import com.omnihealthgroup.reshining.custom.object.Consultation;
import com.omnihealthgroup.reshining.custom.object.Examination;
import com.omnihealthgroup.reshining.custom.object.Meeting;
import com.omnihealthgroup.reshining.custom.object.Operation;
import com.omnihealthgroup.reshining.custom.object.Personal;
import com.omnihealthgroup.reshining.schedule.adapter.DialogEventListAdapter;
import com.omnihealthgroup.reshining.schedule.dao.EventClassifyData;
import com.omnihealthgroup.reshining.schedule.dao.ScheduleDAO;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * DialogFragment For MonthView
 */
public class ShowWeekEventFragment extends Fragment {
    private static final String TAG = "ShowWeekEventFragment";
    private View showWeekView;
    private DialogEventListAdapter dialogEventListAdapter;
    private LinkedHashMap<String, List<Object>> dayEventCheckList;

    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    public ShowWeekEventFragment() {
        // Required empty public constructor
    }

    /**
     * 載入各種初始化此DialogFragment時需要的參數
     *
     * @param fragmentWeekDay 星期
     * @param fragmentId      Fragment編號
     * @param fragmentMonth   月份
     * @param fragmentDate    日期
     * @return 實作 ShowWeekEventFragment
     */
    public static ShowWeekEventFragment newInstance(String fragmentWeekDay, int fragmentId, int fragmentYear, int fragmentMonth, int fragmentDate) {
        ShowWeekEventFragment showWeekEventFragment = new ShowWeekEventFragment();
        Bundle args = new Bundle();
        args.putString("FragmentWeekDay", fragmentWeekDay);
        args.putInt("FragmentId", fragmentId);
        args.putInt("FragmentYear", fragmentYear);
        args.putInt("FragmentMonth", fragmentMonth);
        args.putInt("FragmentDate", fragmentDate);
        showWeekEventFragment.setArguments(args);
        return showWeekEventFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showWeekView = inflater.inflate(R.layout.dialogfragment_show_event, container, false);
        Log.v(TAG, "onCreateView");

        Button dialogAddNewEventBtn = (Button) showWeekView.findViewById(R.id.dialog_addnewevent_btn);
        dialogAddNewEventBtn.setTextColor(getResources().getColor(R.color.gray_color));

        //  取得日期
        TextView dialogDateText = (TextView) showWeekView.findViewById(R.id.dialog_main).findViewById(R.id.dialog_show_date);
        dialogDateText.setText(String.format("%s", getArguments().getInt("FragmentMonth") + "月" + getArguments().getInt("FragmentDate") + "日"));

        //  取得星期
        TextView dialogWeekdayText = (TextView) showWeekView.findViewById(R.id.dialog_main).findViewById(R.id.dialog_show_weekday);
        dialogWeekdayText.setText(getArguments().getString("FragmentWeekDay"));

        // 初始化事件列表
        initListView();

        //載入當日資料
        final ScheduleDAO scheduleDAO = new ScheduleDAO(getContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder todayString = new StringBuilder();
                todayString.append(getArguments().getInt("FragmentYear"));
                if (getArguments().getInt("FragmentMonth") < 10) {
                    todayString.append("0");
                }
                todayString.append(getArguments().getInt("FragmentMonth"));
                if (getArguments().getInt("FragmentDate") < 10) {
                    todayString.append("0");
                }
                todayString.append(getArguments().getInt("FragmentDate"));

                dayEventCheckList = new LinkedHashMap<>();
                int eventTable = EventClassifyData.EVENT_CLASSIFY_DATA.getEventClassify();
                try {
                    int listCount = 0;
                    if (eventTable == 0) {
                        for (int i = 0; i < ScheduleDAO.TABLE_ARRAY.length; i++) {
                            List<Object> searchDayEventList = scheduleDAO.getDayEvent(i, todayString.toString());
                            switch (ScheduleDAO.TABLE_ARRAY[i]) {
                                case ScheduleDAO.TABLE_CLINIC:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_CLINIC, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();

                                    break;

                                case ScheduleDAO.TABLE_CONSULTATION:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_CONSULTATION, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();
                                    break;

                                case ScheduleDAO.TABLE_OPERATE:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_OPERATE, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();
                                    break;

                                case ScheduleDAO.TABLE_MEETING:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_MEETING, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();
                                    break;

                                case ScheduleDAO.TABLE_PERSONAL:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_PERSONAL, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();
                                    break;

                                case ScheduleDAO.TABLE_EXAMINATION:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_EXAMINATION, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();
                                    break;
                            }
                        }
                    } else {
                        List<Object> searchDayEventList = scheduleDAO.getDayEvent(eventTable, todayString.toString());
                        switch (eventTable) {
                            case 1:
                                dayEventCheckList.put(ScheduleDAO.TABLE_CLINIC, searchDayEventList);
                                listCount = listCount + searchDayEventList.size();
                                break;

                            case 2:
                                dayEventCheckList.put(ScheduleDAO.TABLE_CONSULTATION, searchDayEventList);
                                listCount = listCount + searchDayEventList.size();
                                break;

                            case 3:
                                dayEventCheckList.put(ScheduleDAO.TABLE_OPERATE, searchDayEventList);
                                listCount = listCount + searchDayEventList.size();
                                break;

                            case 4:
                                dayEventCheckList.put(ScheduleDAO.TABLE_MEETING, searchDayEventList);
                                listCount = listCount + searchDayEventList.size();
                                break;

                            case 5:
                                dayEventCheckList.put(ScheduleDAO.TABLE_PERSONAL, searchDayEventList);
                                listCount = listCount + searchDayEventList.size();
                                break;

                            case 6:
                                dayEventCheckList.put(ScheduleDAO.TABLE_EXAMINATION, searchDayEventList);
                                listCount = listCount + searchDayEventList.size();
                                break;
                        }
                    }
                    dialogEventListAdapter.setCount(listCount);
                    dialogEventListAdapter.setDayEventList(dayEventCheckList, eventTable);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogEventListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();

        // 新增事件按鈕的監聽
        dialogAddNewEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                Intent intent = new Intent();
                intent.setClass(getActivity(), AddNewEventActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return showWeekView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");

    }

    private void initListView() {
        ListView dialogEventListView = (ListView) showWeekView.findViewById(R.id.dialog_event_list);
        dialogEventListAdapter = new DialogEventListAdapter(getActivity());
        dialogEventListView.setAdapter(dialogEventListAdapter);

        dialogEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //                final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                //                alertDialog.show();
                //                Window window = alertDialog.getWindow();
                //                window.setContentView(R.layout.alertdialog_imagebutton);

                final ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_CLINIC).size(); i++) {
                    arrayList.add("clinic" + i);
                }
                for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_CONSULTATION).size(); i++) {
                    arrayList.add("consultation" + i);
                }
                for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_OPERATE).size(); i++) {
                    arrayList.add("operation" + i);
                }
                for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_MEETING).size(); i++) {
                    arrayList.add("meeting" + i);
                }
                for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_PERSONAL).size(); i++) {
                    arrayList.add("personal" + i);
                }
                for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_EXAMINATION).size(); i++) {
                    arrayList.add("examination" + i);
                }

                //                window.findViewById(R.id.del_btn).setOnClickListener(new View.OnClickListener() {
                //                    @Override
                //                    public void onClick(View v) {
                //                        delEvent(alertDialog, position, arrayList);
                //                    }
                //                });
                //
                //                window.findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
                //                    @Override
                //                    public void onClick(View v) {
                //                        editEvent(alertDialog, position, arrayList);
                //                    }
                //                });

                editEvent(position, arrayList);
            }
        });
    }

    private void delEvent(AlertDialog alertDialog, int position, ArrayList<String> arrayList) {
        String tableName = null, event_MsgPk = null;
        switch (arrayList.get(position)) {
            case "clinic":
                Clinic clinicObj = (Clinic) dayEventCheckList.get(ScheduleDAO.TABLE_CLINIC).get(0);
                tableName = ScheduleDAO.TABLE_CLINIC;
                event_MsgPk = clinicObj.getClinicMsgPk();

                delAlertDialog(tableName, event_MsgPk, clinicObj, alertDialog);

                break;

            case "consultation":
                Consultation consultationObj = (Consultation) dayEventCheckList.get(ScheduleDAO.TABLE_CONSULTATION).get(0);
                tableName = ScheduleDAO.TABLE_CONSULTATION;
                event_MsgPk = consultationObj.getConsultationMsgPk();

                delAlertDialog(tableName, event_MsgPk, consultationObj, alertDialog);

                break;

            case "operation":
                Operation operationObj = (Operation) dayEventCheckList.get(ScheduleDAO.TABLE_OPERATE).get(0);
                tableName = ScheduleDAO.TABLE_OPERATE;
                event_MsgPk = operationObj.getOperateMsgPk();

                delAlertDialog(tableName, event_MsgPk, operationObj, alertDialog);

                break;

            case "meeting":
                Meeting meetingObj = (Meeting) dayEventCheckList.get(ScheduleDAO.TABLE_MEETING).get(0);
                tableName = ScheduleDAO.TABLE_MEETING;
                event_MsgPk = meetingObj.getMeetingMsgPk();

                delAlertDialog(tableName, event_MsgPk, meetingObj, alertDialog);

                break;

            case "personal":
                Personal personalObj = (Personal) dayEventCheckList.get(ScheduleDAO.TABLE_PERSONAL).get(0);
                tableName = ScheduleDAO.TABLE_PERSONAL;
                event_MsgPk = personalObj.getPersonalMsgPk();

                delAlertDialog(tableName, event_MsgPk, personalObj, alertDialog);

                break;

            case "examination":
                Examination examinationObj = (Examination) dayEventCheckList.get(ScheduleDAO.TABLE_EXAMINATION).get(0);
                tableName = ScheduleDAO.TABLE_EXAMINATION;
                event_MsgPk = examinationObj.getExaminationMsgPk();

                delAlertDialog(tableName, event_MsgPk, examinationObj, alertDialog);

                break;
        }
    }

    //    private void editEvent(AlertDialog alertDialog, int position, ArrayList<String> arrayList) {
    private void editEvent(int position, ArrayList<String> arrayList) {
        //        alertDialog.dismiss();
        Bundle bundle = new Bundle();

        switch (arrayList.get(position).substring(0, 5)) {
            //            case "clinic":
            case "clini":
                Clinic clinicObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("clinic" + i)) {
                        clinicObj = (Clinic) dayEventCheckList.get(ScheduleDAO.TABLE_CLINIC).get(i);
                    }
                }
                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("clinicObj", clinicObj);

                break;

            //            case "consultation":
            case "consu":
                Consultation consultationObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("consultation" + i)) {
                        consultationObj = (Consultation) dayEventCheckList.get(ScheduleDAO.TABLE_CONSULTATION).get(i);
                    }
                }
                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("consultationObj", consultationObj);

                break;

            //            case "operation":
            case "opera":
                Operation operationObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("operation" + i)) {
                        operationObj = (Operation) dayEventCheckList.get(ScheduleDAO.TABLE_OPERATE).get(i);
                    }
                }
                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("operationObj", operationObj);

                break;

            //            case "meeting":
            case "meeti":
                Meeting meetingObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("meeting" + i)) {
                        meetingObj = (Meeting) dayEventCheckList.get(ScheduleDAO.TABLE_MEETING).get(i);
                    }
                }
                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("meetingObj", meetingObj);

                break;

            //            case "personal":
            case "perso":
                Personal personalObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("personal" + i)) {
                        personalObj = (Personal) dayEventCheckList.get(ScheduleDAO.TABLE_PERSONAL).get(i);
                    }
                }
                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("personalObj", personalObj);

                break;

            //            case "examination":
            case "exami":
                Examination examinationObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("examination" + i)) {
                        examinationObj = (Examination) dayEventCheckList.get(ScheduleDAO.TABLE_EXAMINATION).get(i);
                    }
                }
                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("examinationObj", examinationObj);

                break;
        }
        //
        //
        //
        //
        //        if (dayEventCheckList.get(ScheduleDAO.TABLE_CLINIC).size() > 0) {
        //            Clinic clinicObj = (Clinic) dayEventCheckList.get(ScheduleDAO.TABLE_CLINIC).get(0);
        //
        //            bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
        //            bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
        //            bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
        //            bundle.putSerializable("clinicObj", clinicObj);
        //        } else if (dayEventCheckList.get(ScheduleDAO.TABLE_CONSULTATION).size() > 0) {
        //            Consultation consultationObj = (Consultation) dayEventCheckList.get(ScheduleDAO.TABLE_CONSULTATION).get(0);
        //
        //            bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
        //            bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
        //            bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
        //            bundle.putSerializable("consultationObj", consultationObj);
        //        } else if (dayEventCheckList.get(ScheduleDAO.TABLE_OPERATE).size() > 0) {
        //            Operation operationObj = (Operation) dayEventCheckList.get(ScheduleDAO.TABLE_OPERATE).get(0);
        //
        //            bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
        //            bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
        //            bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
        //            bundle.putSerializable("operationObj", operationObj);
        //        } else if (dayEventCheckList.get(ScheduleDAO.TABLE_MEETING).size() > 0) {
        //            Meeting meetingObj = (Meeting) dayEventCheckList.get(ScheduleDAO.TABLE_MEETING).get(0);
        //
        //            bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
        //            bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
        //            bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
        //            bundle.putSerializable("meetingObj", meetingObj);
        //        } else if (dayEventCheckList.get(ScheduleDAO.TABLE_PERSONAL).size() > 0) {
        //            Personal personalObj = (Personal) dayEventCheckList.get(ScheduleDAO.TABLE_PERSONAL).get(0);
        //
        //            bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
        //            bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
        //            bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
        //            bundle.putSerializable("personalObj", personalObj);
        //        } else if (dayEventCheckList.get(ScheduleDAO.TABLE_EXAMINATION).size() > 0) {
        //            Examination examinationObj = (Examination) dayEventCheckList.get(ScheduleDAO.TABLE_EXAMINATION).get(0);
        //
        //            bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
        //            bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
        //            bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
        //            bundle.putSerializable("examinationObj", examinationObj);
        //        }

        Intent intent = new Intent();
        intent.setClass(getActivity(), AddNewEventActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void delAlertDialog(final String tableName, final String event_MsgPk, final Object object, AlertDialog alertDialog) {
        alertDialog.dismiss();

        new android.support.v7.app.AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_del_event))
                .setPositiveButton(getString(R.string.msg_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        Log.i(TAG, "manual upload data");
                                        JSONObject reUpdateResponse = webServiceConnection.delCaleinfo(prf.getString("access_token", ""), event_MsgPk);
                                        //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                        if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                            // 更新sql lite資料庫
                                            ScheduleDAO scheduleDAO = new ScheduleDAO(getContext());
                                            scheduleDAO.delete_MsgPk(tableName, object);
                                            Looper.prepare();
                                            Toast.makeText(getContext(), getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();

                                            Fragment fragment = new MyHealthCalendarFragment();

                                            getChildFragmentManager().beginTransaction()
                                                    .replace(R.id.dialog_main, fragment)
                                                            //                                .addToBackStack(null)
                                                    .commit();

//                                            MainActivity.weekEventDialogFragment.onDestroyView();
                                            Looper.loop();
                                            break;
                                        } else {
                                            Looper.prepare();
                                            Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                            Looper.loop();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                })
                .setNegativeButton(getString(R.string.msg_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
