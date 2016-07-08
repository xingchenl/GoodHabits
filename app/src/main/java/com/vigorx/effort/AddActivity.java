package com.vigorx.effort;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import com.vigorx.effort.database.EffortOperations;

import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity {
    public static final String TYPE_KEY = "type";
    public static final String EFFORT_KEY = "effort";
    public static final int TYPE_EDIT = 2;
    public static final int TYPE_ADD = 1;

    private int mType;
    private EffortInfo mEffort;
    private EditText mTitle;
    private EditText mStartDate;
    private Switch mHaveAlarm;
    private TimePicker mAlarm;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_bar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitle = (EditText) findViewById(R.id.editTextTitle);

        mStartDate = (EditText) findViewById(R.id.editTextStartDate);
        mStartDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DatePickerDialog dialog;
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                dialog = new DatePickerDialog(AddActivity.this, dateSetListener, year, month, day);
                dialog.show();
            }
        });

        mHaveAlarm = (Switch) findViewById(R.id.switchRemind);

        mAlarm = (TimePicker) findViewById(R.id.timePickerRemind);

        Button okButton = (Button) findViewById(R.id.buttonOk);
        assert okButton != null;
        okButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // 取值
                mEffort.setTitle(mTitle.getText().toString());
                mEffort.setStartDate(mStartDate.getText().toString());
                if (mHaveAlarm.isChecked()) {
                    mEffort.setHaveAlarm(1);
                } else {
                    mEffort.setHaveAlarm(0);
                }
                mEffort.setAlarm(mAlarm.getCurrentHour() + ":" + mAlarm.getCurrentMinute());

                // 写入数据库
                EffortOperations operator = EffortOperations.getInstance(getApplicationContext());
                operator.open();
                if (mType == 1) {
                    operator.addEffort(mEffort);
                } else {
                    operator.updateEffort(mEffort);
                }
                operator.close();
                finish();
                v.setEnabled(false);
            }
        });

        mEffort = getIntent().getParcelableExtra(EFFORT_KEY);
        mType = getIntent().getIntExtra(TYPE_KEY, TYPE_ADD);
        if (mType == TYPE_EDIT) {
            mTitle.setText(mEffort.getTitle());
            mStartDate.setText(mEffort.getStartDate());
            mHaveAlarm.setChecked((mEffort.getHaveAlarm() == 1));
            mAlarm.setCurrentHour(Integer.parseInt(mEffort.getAlarm().split(":")[0]));
            mAlarm.setCurrentMinute(Integer.parseInt(mEffort.getAlarm().split(":")[1]));
            setTitle(R.string.title_activity_edit);
        } else {
            setTitle(R.string.title_activity_add);
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date = year + "-" + monthOfYear + "-" + dayOfMonth;
            mEffort.setStartDate(date);
        }
    };
}