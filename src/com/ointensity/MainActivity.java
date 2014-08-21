package com.ointensity;



import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;


public class MainActivity extends Activity implements OnClickListener {
	
	private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button showBtn = (Button)findViewById(R.id.showBtn);
        showBtn.setOnClickListener(this);

        graphView = new LineGraphView(this, "");
        
        _initGraphView();
    }

    // 그래프 모양 셋팅
	private void _initGraphView() {
		// 포인트 모양
        // ((LineGraphView) graphView).setDrawDataPoints(true);
        // ((LineGraphView) graphView).setDataPointsRadius(5f);
        
        // Y축 범위
        graphView.setManualYAxisBounds(_log2(64), _log2(0.5f));
        
        // X,Y축 라벨
        graphView.setHorizontalLabels(new String[] {"1/4", "1/2", "1", "2", "4", "8"});
        graphView.setVerticalLabels(new String[] {"64", "32", "16", "8", "4", "2", "1", "1/2"});
        
        // 라벨 스타일
        graphView.getGraphViewStyle().setTextSize(16f);
        graphView.getGraphViewStyle().setVerticalLabelsAlign(Paint.Align.CENTER);
        graphView.getGraphViewStyle().setVerticalLabelsWidth(25);
        
        LinearLayout graphLayout = (LinearLayout) findViewById(R.id.graphLayout);
        graphLayout.addView(graphView);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private double _log2(double num)
    {
    	return (Math.log(num)/Math.log(2));
    }
    
    private int _getNumberOfCpu() {
    	EditText edit_nCPU = (EditText)findViewById(R.id.edit_nCPU);
		String str_nCPU = edit_nCPU.getText().toString();
		if(str_nCPU.isEmpty()) str_nCPU = "0";
		
		return Integer.parseInt(str_nCPU);
    }
    
    private int _getNumberOfFpu() {
    	EditText edit_nFPU = (EditText)findViewById(R.id.edit_nFPU);
		String str_nFPU = edit_nFPU.getText().toString();
		if(str_nFPU.isEmpty()) str_nFPU = "0";
		
		return Integer.parseInt(str_nFPU);
    }
    
    private float _getClockSpeed() {
    	EditText edit_clockSpeed = (EditText)findViewById(R.id.edit_clockSpeed);
		String str_clockSpeed = edit_clockSpeed.getText().toString();
		if(str_clockSpeed.isEmpty()) str_clockSpeed = "0";
		
		return Float.parseFloat(str_clockSpeed);
    }
    
    private float _getBandWidth() {
    	EditText edit_bandWidth = (EditText)findViewById(R.id.edit_bandWidth);
		String str_bandWidth = edit_bandWidth.getText().toString();
		if(str_bandWidth.isEmpty()) str_bandWidth = "0";
		
		return Float.parseFloat(str_bandWidth);
    }    
    
	@Override
	public void onClick(View v) {
		int nCPU = _getNumberOfCpu();
		float clockSpeed = _getClockSpeed();
		int nFPU = _getNumberOfFpu();
		float bandWidth = _getBandWidth();
		
		float beta = nCPU * clockSpeed * nFPU;
		
		List<GraphView.GraphViewData> dataList = new ArrayList<GraphView.GraphViewData>();

		boolean isCheckedPeak = false;
		float peakX = 0.0f;
		float peakY = 0.0f;
		for(float x = 0.25f; x <= 8.0f; x += 0.01) {
			float y = Math.min(beta, bandWidth * x);
			if(!isCheckedPeak && y == beta) {
				peakX = x;
				peakY = y;
				isCheckedPeak = true;
			}

			dataList.add(new GraphView.GraphViewData(_log2(x), _log2(y)));
		}
		
		GraphViewDataInterface[] datas = new GraphViewDataInterface[dataList.size()];
		for(int i = 0; i < dataList.size(); i++) {
			datas[i] = dataList.get(i);
		}
		
		GraphViewSeries dataSeries = new GraphViewSeries(datas);
		
        graphView.removeAllSeries();
        graphView.addSeries(dataSeries); // data
        ((TextView)findViewById(R.id.oIntensityTv)).setText("Operation intensity : " + String.format("%.2f" , peakX));
        ((TextView)findViewById(R.id.peakFPointTv)).setText("Peak floating-point : " + String.format("%.2f" , peakY));
	}
}
