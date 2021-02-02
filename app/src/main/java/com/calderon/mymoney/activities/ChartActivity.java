package com.calderon.mymoney.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.calderon.mymoney.R;
import com.calderon.mymoney.models.Registro;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.calderon.mymoney.utils.Util.loadData;

public class ChartActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private AnyChartView chartView;
    private List<Registro> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences("DATA",MODE_PRIVATE);
        chartView = findViewById(R.id.any_chart_view);

        chartView.setProgressBar(findViewById(R.id.progress_bar));
        Cartesian cartesian = AnyChart.column();

        list = loadData(preferences,list);
        for (int i = 0;i<list.size();i++)
            System.out.println(list);


        List<DataEntry> data = new ArrayList<>();
        for (int i = 0;i<list.size();i++)
            data.add(new ValueDataEntry(list.get(i).getFecha(),list.get(i).getTotal()));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Histograma");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Fecha");
        cartesian.yAxis(0).title("Cantidad");

        chartView.setChart(cartesian);

    }
}
