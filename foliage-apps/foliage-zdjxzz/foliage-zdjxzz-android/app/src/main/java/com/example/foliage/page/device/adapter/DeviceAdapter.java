package com.example.foliage.page.device.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.foliage.R;
import com.example.foliage.page.device.dto.DeviceInfoDTO;

import java.util.ArrayList;

/**
 * Description：信用卡活动
 * Created by liang.qfzc@gmail.com on 2019/4/1.
 */
public class DeviceAdapter extends BaseQuickAdapter<DeviceInfoDTO, BaseViewHolder> {

    public DeviceAdapter(Context context) {
        super(R.layout.item_device_info, new ArrayList<DeviceInfoDTO>());
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceInfoDTO item) {
        helper.setText(R.id.tv_device_name, item.getName());
        helper.setText(R.id.tv_device_code, item.getDevcode());
    }
}
