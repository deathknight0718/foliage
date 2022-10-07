package org.foliage.zdjxzz.infrastructure.banner.holder;

import android.content.Context;
import android.view.View;

/**
 * Description：MZView自定义接口
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public interface MZViewHolder<T> {
    /**
     * 创建View
     *
     * @param context
     * @return
     */
    View createView(Context context);

    /**
     * 绑定数据
     *
     * @param context
     * @param position
     * @param data
     */
    void onBind(Context context, int position, T data);
}
