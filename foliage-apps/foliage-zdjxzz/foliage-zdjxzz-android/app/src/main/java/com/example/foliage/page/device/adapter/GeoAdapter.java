package com.example.foliage.page.device.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.foliage.R;
import com.example.foliage.page.device.dto.GeographicDTO;
import com.example.foliage.page.device.view.DeviceDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Description：分享Adapter
 * Created by liang.qfzc@gmail.com on 2018/6/12.
 */

public class GeoAdapter extends BaseAdapter {

    private final LayoutInflater layoutInflater;
    private List<GeographicDTO.GeographicsByProvinceBean> mGeoList = new ArrayList<>();

    public GeoAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mGeoList.size();
    }

    @Override
    public GeographicDTO.GeographicsByProvinceBean getItem(int position) {
        return mGeoList.get(position);
    }

    public void setData(List<GeographicDTO.GeographicsByProvinceBean> mGeoList) {
        this.mGeoList = mGeoList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<GeographicDTO.GeographicsByProvinceBean> getData() {
        return mGeoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_geo, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = view.findViewById(R.id.tv_device_name);
            viewHolder.deviceCode = view.findViewById(R.id.tv_device_code);
            viewHolder.location = view.findViewById(R.id.tv_location);
            viewHolder.deviceDetail = view.findViewById(R.id.device_detail);
            view.setTag(viewHolder);

            viewHolder.deviceDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeviceDetailActivity.startActivity(layoutInflater.getContext(), getItem(position).getDevice().getId());
                }
            });
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.deviceCode.setText(getItem(position).getDevice().getDevcode());
        viewHolder.deviceName.setText(" - " + getItem(position).getDevice().getName());
        viewHolder.location.setText(getItem(position).getAddress());

        return view;
    }

    static class ViewHolder {
        TextView deviceCode;
        TextView deviceName;
        TextView location;
        AppCompatImageView deviceDetail;
    }

}
