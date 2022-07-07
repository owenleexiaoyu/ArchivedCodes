package com.redant.childcare.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redant.childcare.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2017-06-01.
 */

public class FragmentChart extends Fragment {
    /**
     * 从外界传入的数据
     */
    private String mChartName;//图表名称
    private String mYLabel;//Y轴的文字
    private String [] mXLabels;//X轴的文字
    private double [] mDataValues;//数据

    private int columnNumber = 7;//柱状图的列数
    private int subColumnNumber = 1;//每列只有一根柱形

    private ColumnChartView ccView;
    private ColumnChartData columnChartData;
    private List<AxisValue> mAxisValues;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart,container,false);
        Bundle bundle = getArguments();
        this.mChartName = bundle.getString("chartName");
        this.mYLabel = bundle.getString("yLabel");
        this.mXLabels = bundle.getStringArray("xLabels");
        this.mDataValues = bundle.getDoubleArray("dataValues");
        ccView = (ColumnChartView) view.findViewById(R.id.columnchartView);
        initChart();
        ccView.setColumnChartData(columnChartData);
        return view;
    }




    /**
     * 初始化图表的各项属性
     */
    private void initChart() {
        List<SubcolumnValue> values;
        List<Column> columns = new ArrayList<>();
        mAxisValues = new ArrayList<>();
        for (int i = 0; i < columnNumber; i++) {
            values = new ArrayList<>();
            for (int j = 0;j < subColumnNumber; j++) {
                values.add(new SubcolumnValue((float) mDataValues[i], ChartUtils.pickColor()));
            }
            Column column = new Column(values);
            //设置数据标注的格式
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter(2);
            column.setFormatter(chartValueFormatter);
            //是否有数据标注
            column.setHasLabels(true);
            //是否在点击圆柱时才显示标注
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
            //给x坐标设置描述
            mAxisValues.add(new AxisValue(i).setLabel(mXLabels[i]));
        }
        columnChartData = new ColumnChartData(columns);
        Axis axisX = new Axis();
        Axis axisY = new Axis();

        axisY.setName(mYLabel);
        axisY.setHasLines(true);
        axisY.setTextColor(android.R.color.black);

        axisX.setTextColor(android.R.color.black);
        axisX.setValues(mAxisValues);
        axisX.setName(mChartName);

        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(axisY);

    }
}
