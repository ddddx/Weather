package com.ddddx.weather;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ddddx.weather.db.City;
import com.ddddx.weather.db.Country;
import com.ddddx.weather.db.Province;
import com.ddddx.weather.util.HttpUtil;
import com.ddddx.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/28.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVE_PROVINCE = 0;
    public static final int LEVE_CITY = 1;
    public static final int LEVE_COUNTRY = 2;

    private ProgressDialog mProgressDialog;
    private Button mBackButton;
    private TextView mTitleText;
    private ListView mListView;
    private List<String> mDataList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<Country> mCountryList;

    private Province mSelectedProvince;
    private City mSelectedCity;
    private int currentLevel;

    private String mLatitude;
    private String mLongitude;
    private String mCountry;

    private TagGroup mTagGroup;
    private LocationClient mClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        mClient = new LocationClient(getActivity());
        mClient.registerLocationListener(new MyLocationListener());
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mTitleText = (TextView) view.findViewById(R.id.title_text);
        mTagGroup = (TagGroup) view.findViewById(R.id.tag);
        mTagGroup.setTags("正在定位...");
        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                if ((Activity)getActivity()instanceof MainActivity){
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", mCountry);
                    startActivity(intent);
                    getActivity().finish();
                }else {
                    WeatherActivity activity = (WeatherActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefresh.setRefreshing(true);
                    activity.requestWeather(mCountry);
                    //mLongitude+","+mLatitude
                }
            }
        });

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, mDataList);
        mListView.setAdapter(mAdapter);
        requestLocation();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVE_PROVINCE) {
                    mSelectedProvince = mProvinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVE_CITY) {
                    mSelectedCity = mCityList.get(position);
                    queryCountry();
                } else if (currentLevel == LEVE_COUNTRY) {
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", mCountryList.get(position).getWeatherId());
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(mCountryList.get(position).getWeatherId());
                    }
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVE_COUNTRY) {
                    queryCities();
                } else if (currentLevel == LEVE_CITY) {
                    queryProvince();
                }
            }
        });
        queryProvince();
    }

    private void queryProvince() {
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.INVISIBLE);
        mProvinceList = DataSupport.findAll(Province.class);
        if (mProvinceList.size() > 0) {
            mDataList.clear();
            for (Province province : mProvinceList) {
                mDataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVE_PROVINCE;
        } else {
            String url = "http://guolin.tech/api/china";
            queryFromService(url, "province");
        }
    }


    private void queryCountry() {
        mTitleText.setText(mSelectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCountryList = DataSupport.where("cityid=?", String.valueOf(mSelectedCity.getId())).find(Country.class);
        if (mCountryList.size() > 0) {
            mDataList.clear();
            for (Country country : mCountryList) {
                mDataList.add(country.getCountyrName());

            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVE_COUNTRY;
        } else {
            String url = "http://guolin.tech/api/china/" + mSelectedProvince.getProvinceCode() + "/" + mSelectedCity.getCityCode();

            queryFromService(url, "country");
        }
    }

    private void queryCities() {
        mTitleText.setText(mSelectedProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        mCityList = DataSupport.where("provinceid=?", String.valueOf(mSelectedProvince.getId())).find(City.class);
        if (mCityList.size() > 0) {
            mDataList.clear();
            for (City city : mCityList) {
                mDataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVE_CITY;
        } else {
            String url = "http://guolin.tech/api/china/" + mSelectedProvince.getProvinceCode();
            queryFromService(url, "city");
        }
    }

    private void queryFromService(String url, final String type) {
        showProgressDialog();
        HttpUtil.sendOKHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean result = false;
                String responseText = response.body().string();
                switch (type) {
                    case "province":
                        result = Utility.handleProvinceResponse(responseText);
                        break;
                    case "city":
                        result = Utility.handleCityResponse(responseText, mSelectedProvince.getId());
                        break;
                    case "country":
                        result = Utility.handleCountryResponse(responseText, mSelectedCity.getId());
                        break;
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            switch (type) {
                                case "province":
                                    queryProvince();
                                    break;
                                case "city":
                                    queryCities();
                                    break;
                                case "country":
                                    queryCountry();
                                    break;
                            }
                        }
                    });
                }
            }
        });

    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("加载中");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void requestLocation() {
        initLocation();
        mClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
//        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mClient.setLocOption(option);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mClient.stop();
    }




    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            mLatitude = String.valueOf(location.getLatitude());
            mLongitude = String.valueOf(location.getLongitude());
            mCountry = location.getDistrict();
            currentPosition.append("纬度：").append(mLatitude).append("\n");
            currentPosition.append("经线：").append(mLongitude).append("\n");
            currentPosition.append("国家：").append(location.getCountry()).append("\n");
            currentPosition.append("省：").append(location.getProvince()).append("\n");
            currentPosition.append("市：").append(location.getCity()).append("\n");
            currentPosition.append("区：").append(mCountry).append("\n");
            currentPosition.append("街道：").append(location.getStreet()).append("\n");
            currentPosition.append("定位方式：");
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            }
            mTagGroup.setTags(location.getDistrict());

            System.out.println(currentPosition);
        }
    }
}
