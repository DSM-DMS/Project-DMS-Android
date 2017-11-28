package com.dms.beinone.application.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dms.beinone.application.DMSService;
import com.dms.beinone.application.R;
import com.dms.beinone.application.activities.ChangePasswordActivity;
import com.dms.beinone.application.activities.LoginActivity;
import com.dms.beinone.application.dialogs.LogoutDialogFragment;
import com.dms.beinone.application.managers.AccountManager;
import com.dms.beinone.application.managers.HttpManager;
import com.dms.beinone.application.models.Account;
import com.dms.beinone.application.models.ApplyStatus;
import com.dms.beinone.application.models.Class;
import com.dms.beinone.application.models.Goingout;
import com.dms.beinone.application.models.Meal;
import com.dms.beinone.application.models.Token;
import com.dms.beinone.application.utils.ExtensionUtils;
import com.dms.beinone.application.utils.StayUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dms.beinone.application.DMSService.HTTP_BAD_REQUEST;
import static com.dms.beinone.application.DMSService.HTTP_INTERNAL_SERVER_ERROR;
import static com.dms.beinone.application.DMSService.HTTP_NO_CONTENT;
import static com.dms.beinone.application.DMSService.HTTP_OK;

/**
 * Created by BeINone on 2017-05-31.
 */

public class MyPageFragment extends Fragment {

    public static final int REQUEST_CODE_LOGOUT_DIALOG = 1;

    private TextView mStayStatusTV;
    private TextView mExtensionStatusTV;
    private TextView mMeritTV;
    private TextView mDemeritTV;
    private View mLogoutMenu;
    private TextView mLogoutTV;
    private View mChangePassword;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        mStayStatusTV = (TextView) view.findViewById(R.id.tv_my_page_stay_status);
        mExtensionStatusTV = (TextView) view.findViewById(R.id.tv_my_page_extension_status);
        mMeritTV = (TextView) view.findViewById(R.id.tv_my_page_merit);
        mDemeritTV = (TextView) view.findViewById(R.id.tv_my_page_demerit);
        mLogoutMenu = view.findViewById(R.id.layout_my_page_logout);
        mLogoutTV = (TextView) view.findViewById(R.id.tv_my_page_logout);
        mChangePassword = view.findViewById(R.id.layout_my_page_change_password);

        mMeritTV.setText("0");
        mDemeritTV.setText("0");

        loadMyPage_json();


     /*   try {
            loadApplyStatus();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        initMenuArrowColor(view);

        View qnaListMenu = view.findViewById(R.id.layout_my_page_qna_list);

        qnaListMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View reportFacilityMenu = view.findViewById(R.id.layout_my_page_report_facility);
        reportFacilityMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View changePasswordMenu = view.findViewById(R.id.layout_my_page_change_password);
        changePasswordMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
            }
        });

      /*  Button loginBtn = (Button) view.findViewById(R.id.btn_my_page_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });*/

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setLogoutMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_LOGOUT_DIALOG:
                if (resultCode == Activity.RESULT_OK) {
                    setLogoutMenu();
                }
                break;
        }
    }

    private void initMenuArrowColor(View rootView) {
        ImageView arrowIV1 = (ImageView) rootView.findViewById(R.id.iv_my_page_arrow1);
        ImageView arrowIV2 = (ImageView) rootView.findViewById(R.id.iv_my_page_arrow2);
        ImageView arrowIV3 = (ImageView) rootView.findViewById(R.id.iv_my_page_arrow3);
        ImageView arrowIV4 = (ImageView) rootView.findViewById(R.id.iv_my_page_arrow4);

        arrowIV1.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        arrowIV2.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        arrowIV3.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        arrowIV4.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

    }

    private void bind(Account account) {

        mStayStatusTV.setText(StayUtils.getStringFromStayStatus(account.getStayValue()));
        String extensionStatus = ExtensionUtils.getStringFromClass(account.getExtension_11_class());
        mExtensionStatusTV.setText(extensionStatus);
/*        mMeritTV.setText(account.getMerit());
        mDemeritTV.setText(account.getDemerit());*/
    }




 /*   private void loadApplyStatus() throws IOException {
        DMSService dmsService = HttpManager.createDMSService(getContext());
        Call<ApplyStatus> call = dmsService.loadApplyStatus();
        call.enqueue(new Callback<ApplyStatus>() {
            @Override
            public void onResponse(Call<ApplyStatus> call, Response<ApplyStatus> response) {

                Log.d("MYPAGE",response.body().toString());
                switch (response.code()) {
                    case HTTP_OK:
                        ApplyStatus applyStatus = response.body();
                        if (applyStatus.isExtensionApplied()) {
                            int no = applyStatus.getExtensionClass();
                            String name = applyStatus.getExtensionName();
                            setExtensionApplyStatus(new Class(no, name));
                        }

                        if (applyStatus.isStayApplied()) {
                            setStayApplyStatus(applyStatus.getStayValue());
                        }
                        break;
                    case HTTP_BAD_REQUEST:
                        Toast.makeText(getContext(), R.string.http_bad_request, Toast.LENGTH_SHORT).show();
                        break;
                    case HTTP_INTERNAL_SERVER_ERROR:
                        Toast.makeText(getContext(), R.string.apply_list_load_internal_server_error, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ApplyStatus> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }*/


    private void setExtensionApplyStatus(Class clazz) {
        mExtensionStatusTV.setText("미신청");

        if (clazz == null) {
            mExtensionStatusTV.setText(R.string.unapplied);
        } else {
            mExtensionStatusTV.setText(ExtensionUtils.getStringFromClass(clazz.getNo()));
        }
    }

    private void setStayApplyStatus(int value) {
        if (value == -1) {
            mStayStatusTV.setText(R.string.unapplied);
        } else {
            mStayStatusTV.setText(StayUtils.getStringFromStayStatus(value));
        }
    }

    private void setLogoutMenu() {
        if (AccountManager.isLogined(getContext()) || HttpManager.token!=null) {
            mLogoutTV.setText(R.string.logout);
            mLogoutMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogoutDialogFragment logoutDialogFragment = new LogoutDialogFragment(getContext());
                    logoutDialogFragment.setTargetFragment(MyPageFragment.this, REQUEST_CODE_LOGOUT_DIALOG);
                    logoutDialogFragment.show(getChildFragmentManager(), null);
                }
            });
        } else {
            mLogoutTV.setText(R.string.login);
            mLogoutMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), LoginActivity.class));

                }
            });
        }
    }

    private void loadMyPage() {
        Log.d("loadMypage 함수 호출","함수 호출");
        if(AccountManager.isLogined(getActivity())){
            DMSService dmsService = HttpManager.createDMSService(getContext());
            Call<Account> call = dmsService.loadMyPage(AccountManager.isToken(getActivity()));
            call.enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {
                    Log.d("MYPAGE_CODE",String.valueOf(response.code()));
                    switch (response.code()) {
                        case HTTP_OK:
                            Log.d("MYPAGE_DATA",response.body().toString());

                            Account account=response.body();
                            Log.d("MYPAGE_DATA",account.getName());

                            //bind(account);
                            break;
                        case HTTP_NO_CONTENT:
                            Toast.makeText(getContext(), R.string.my_page_load_no_content, Toast.LENGTH_SHORT).show();
                            break;
                        case HTTP_BAD_REQUEST:
                            break;
                        case HTTP_INTERNAL_SERVER_ERROR:
                            Toast.makeText(getContext(), R.string.my_page_load_internal_server_error, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                @Override
                public void onFailure(Call<Account> call, Throwable t) {
                    t.printStackTrace();
                }

            });
        }else {
            Toast.makeText(getActivity(),"로그인을 해주세요",Toast.LENGTH_SHORT).show();
        }

    }

    private void loadMyPage_json() {
        Log.d("loadMypage 함수 호출","함수 호출");
        if(AccountManager.isLogined(getActivity())){
            DMSService dmsService = HttpManager.createDMSService(getContext());
            Call<JsonObject> call = dmsService.loadMyPage_json(AccountManager.isToken(getActivity()));

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("MYPAGE_CODE",String.valueOf(response.code()));
                    switch (response.code()) {
                        case HTTP_OK:
                            Log.d("MYPAGE_DATA",response.body().toString());



                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(response.body().toString());
                            int extension_11_class=element.getAsJsonObject().get("extension_11_class").getAsInt();
                            int extension_11_seat=element.getAsJsonObject().get("extension_11_seat").getAsInt();
                            int extension_12_class=element.getAsJsonObject().get("extension_12_class").getAsInt();
                            int extension_12_seat=element.getAsJsonObject().get("extension_12_seat").getAsInt();
                            boolean goingout_sat=element.getAsJsonObject().get("goingout_sat").getAsBoolean();
                            boolean goingout_sun=element.getAsJsonObject().get("goingout_sun").getAsBoolean();
                            String name=element.getAsJsonObject().get("name").getAsString();
                            int number=element.getAsJsonObject().get("number").getAsInt();
                            String signup_date=element.getAsJsonObject().get("signup_date").getAsString();
                            int stay_value=element.getAsJsonObject().get("stay_value").getAsInt();


                            bind(new Account(stay_value,number,signup_date,name,goingout_sun,goingout_sat,extension_11_class,extension_12_class
                            ,extension_11_seat,extension_12_seat));
                            break;
                        case HTTP_NO_CONTENT:
                            Toast.makeText(getContext(), R.string.my_page_load_no_content, Toast.LENGTH_SHORT).show();
                            break;
                        case HTTP_BAD_REQUEST:
                            break;
                        case HTTP_INTERNAL_SERVER_ERROR:
                            Toast.makeText(getContext(), R.string.my_page_load_internal_server_error, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                }

            });
        }else {
            Toast.makeText(getActivity(),"로그인을 해주세요",Toast.LENGTH_SHORT).show();
        }

    }
}
