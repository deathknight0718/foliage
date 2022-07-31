package com.example.foliage.page.main.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foliage.R;
import com.example.foliage.base.BaseFragmentActivity;
import com.example.foliage.infrastructure.banner.MZBannerView;
import com.example.foliage.infrastructure.banner.holder.MZHolderCreator;
import com.example.foliage.infrastructure.banner.holder.MZViewHolder;
import com.example.foliage.infrastructure.imageloader.DzImageLoader;
import com.example.foliage.infrastructure.utils.IntentTool;
import com.example.foliage.page.device.view.DeviceListActivity;
import com.example.foliage.page.main.contract.MainContract;
import com.example.foliage.page.main.entity.BannerEntity;
import com.example.foliage.page.main.entity.GridEntity;
import com.example.foliage.page.main.presenter.MainPresenter;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseFragmentActivity implements MainContract.View {

    private MainContract.Presenter mPresenter;

    @BindView(R.id.banner)
    MZBannerView vMZBannerView;

    @BindView(R.id.grid_view)
    GridView vGridView;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected String getActionBarTitle() {
        return null;
    }

    @Override
    protected boolean hasBindEventBus() {
        return false;
    }

    @Override
    protected void initView() {
        super.initView();

        mPresenter = new MainPresenter(this);
        mPresenter.init();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void initBanner(List<BannerEntity> retDatBean) {
        vMZBannerView.setBannerPageClickListener((view, position) -> {

        });
        vMZBannerView.setPages(retDatBean, new MZHolderCreator() {
            @Override
            public MZViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        if (retDatBean.size() > 1) vMZBannerView.start();

    }

    @Override
    public void initGrid() {
        GridEntity entity = new GridEntity();
        entity.setIcon(new IconDrawable(this, Iconify.IconValue.zmdi_device_hub).color(R.color.colorPrimary));
        entity.setName("设备监控");
        GridAdapter adapter = new GridAdapter(this, List.of(entity));
        vGridView.setAdapter(adapter);
        vGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IntentTool.startActivity(mContext, DeviceListActivity.class);
            }
        });
    }

    public static class BannerViewHolder implements MZViewHolder<BannerEntity> {

        private ImageView mImageView;
        private TextView mTextView;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product_pic, null);
            mImageView = view.findViewById(R.id.item_event_image);
            mTextView = view.findViewById(R.id.item_event_tv);
            return view;
        }

        @Override
        public void onBind(Context context, int i, BannerEntity banner) {
            DzImageLoader.getInstance().displayImage(mImageView.getContext(), banner.getUrl(), mImageView);
            mTextView.setText(banner.getName());
        }

    }

    public static class GridAdapter extends BaseAdapter {

        private Context mContext;
        private List<GridEntity> all = new ArrayList<>();

        public GridAdapter(Context mContext, List<GridEntity> data) {
            this.mContext = mContext;
            this.all = data;
        }

        @Override
        public int getCount() {
            return all.size();
        }

        @Override
        public GridEntity getItem(int i) {
            return all.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_option, null);
            }
            TextView titleTv = ButterKnife.findById(convertView, R.id.title);
            ImageView iconView = ButterKnife.findById(convertView, R.id.icon);

            GridEntity entity = getItem(position);
            if (entity != null) {
                titleTv.setText(entity.getName());
                DzImageLoader.getInstance().displayImage(mContext, entity.getIcon(), iconView);
            }
            return convertView;
        }

    }

}