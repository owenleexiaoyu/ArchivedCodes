package com.wenbin.view;

import android.content.Context;
import android.view.View;

import com.wenbin.mysports.R;
import com.wenbin.mysports.data.DataUtils;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

/**
 * Created by wenbin on 2016/2/11.
 */
public class PieChart {
    private Context context = null;
    public PieChart(Context context) {
        this.context = context;
    }
    public View getView() {
        View view = ChartFactory.getPieChartView(context,getDataset(),getRenderer());
        return view;
    }

    private DefaultRenderer getRenderer() {
        int[] colors = new int[] {context.getResources().getColor(R.color.runcolor),
                context.getResources().getColor(R.color.squatcolor), context.getResources().getColor(R.color.pushupcolor),
                context.getResources().getColor(R.color.walkcolor), context.getResources().getColor(R.color.basketballcolor) };
        DefaultRenderer renderer = new DefaultRenderer();
        //设置背景颜色
        renderer.setChartTitle("每部分卡路里消耗的分量");
        //这个是统计图里面的数字
        renderer.setLabelsTextSize(30);
        //这个是说明颜色是哪个的那个框
        renderer.setLegendTextSize(20);
        renderer.setMargins(new int[] { 200, 30, 15, 0 });
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        renderer.setZoomButtonsVisible(true);
        renderer.setZoomEnabled(true);
        renderer.setChartTitleTextSize(30);
        renderer.setDisplayValues(true);
        //设置显示标题，和指示的文字
        renderer.setShowLabels(true);
        return renderer;
    }

    private CategorySeries getDataset() {
        CategorySeries series = new CategorySeries("每部分卡路里消耗的分量");
        series.add("跑步",DataUtils.getInstance(context).get(DataUtils.UTILS_SPORT_RUN));
        series.add("深蹲",DataUtils.getInstance(context).get(DataUtils.UTILS_SPORT_SQUAT));
        series.add("俯卧撑",DataUtils.getInstance(context).get(DataUtils.UTILS_SPORT_PUSHUP));
        series.add("记步",DataUtils.getInstance(context).get(DataUtils.UTILS_SPORT_WALK));
        series.add("篮球场",DataUtils.getInstance(context).get(DataUtils.UTILS_SPORT_BASKETBALL));
        return series;
    }

}
