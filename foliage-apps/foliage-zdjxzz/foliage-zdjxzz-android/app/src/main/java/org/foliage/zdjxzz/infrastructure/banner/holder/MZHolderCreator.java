package org.foliage.zdjxzz.infrastructure.banner.holder;

/**
 * Description：创建ViewHolder接口
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public interface MZHolderCreator<VH extends MZViewHolder> {
    /**
     * 创建ViewHolder
     *
     * @return
     */
    public VH createViewHolder();
}
