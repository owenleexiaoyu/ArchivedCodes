package com.wenbin.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.wenbin.mysports.R;
import com.wenbin.mysports.data.DataUtils;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BarChart  {

    public View getView(Context context) {
        DataUtils utils = DataUtils.getInstance(context.getApplicationContext());
        String[] titles = new String[] { "跑步", "深蹲", "俯卧撑", "计步","篮球" };
        //以下是X轴坐标第一次要显示的值
        List<Date[]> x = new ArrayList<Date[]>();
        x = utils.getListDate();
//        Date[] dateValues = new Date[] { new Date(95, 0, 1), new Date(95, 3, 1), new Date(95, 6, 1),
//                new Date(95, 9, 1), new Date(96, 0, 1), new Date(96, 3, 1), new Date(96, 6, 1),
//                new Date(96, 9, 1), new Date(97, 0, 1), new Date(97, 3, 1), new Date(97, 6, 1),
//                new Date(97, 9, 1) };
//        for(int i = 0;i < titles.length;i++) {
//            x.add(dateValues);
//        }
        //以下是Y坐标第一次要显示的值
        List<double[]> values = new ArrayList<double[]>();
        values = utils.getYValues();
//        values.add(new double[] { 12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
//                13.9 });
//        values.add(new double[] { 10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11 });
//        values.add(new double[] { 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6 });
//        values.add(new double[] { 9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10 });
//        values.add(new double[] { 4, 13, 15, 18, 13, 20, 21, 24, 26, 17, 14, 20 });
        //各个值要显示的颜色
        int[] colors = new int[] {context.getResources().getColor(R.color.runcolor),
                context.getResources().getColor(R.color.squatcolor), context.getResources().getColor(R.color.pushupcolor),
                context.getResources().getColor(R.color.walkcolor), context.getResources().getColor(R.color.basketballcolor) };
        //设置各个点的类型
        PointStyle[] styles = new PointStyle[] {PointStyle.POINT, PointStyle.CIRCLE, PointStyle.DIAMOND,
                PointStyle.TRIANGLE, PointStyle.SQUARE };

        //获取包含xy坐标的渲染器
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }

        setChartSettings(renderer, "", "", "", x.get(0)[0].getTime(),
                x.get(0)[x.get(0).length - 1].getTime(), 0, 2000,
                Color.LTGRAY, Color.LTGRAY);
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.LTGRAY);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setXRoundedLabels(false);
        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.WHITE);
        renderer.setXLabelsAlign(Paint.Align.RIGHT);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setZoomButtonsVisible(true);
//        renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
//        renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });

        XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);

        View view = ChartFactory.getTimeChartView(context, dataset, renderer,"MM/dd");
        return view;
    }
    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        setRenderer(renderer, colors, styles);
        return renderer;
    }
    protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
        //设置XY轴的标题字体大小
        renderer.setAxisTitleTextSize(20);
        renderer.setChartTitleTextSize(30);
        renderer.setLabelsTextSize(20);
        renderer.setLegendTextSize(20);
        renderer.setPointSize(5f);
        renderer.setXLabelsPadding(10f);
        renderer.setYLabelsPadding(10f);
        renderer.setMargins(new int[] { 20, 50, 10, 15 });
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            renderer.addSeriesRenderer(r);
        }
    }
    protected XYMultipleSeriesDataset buildDataset(String[] titles, List<Date[]> xValues,
                                                   List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        addXYSeries(dataset, titles, xValues, yValues);
        return dataset;
    }
    public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<Date[]> xValues,
                            List<double[]> yValues) {
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            TimeSeries series = new TimeSeries(titles[i]);
            Date[] xV = xValues.get(i);
            double[] yV = yValues.get(i);
            int seriesLength = xV.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
    }
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
                                    String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
                                    int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

}