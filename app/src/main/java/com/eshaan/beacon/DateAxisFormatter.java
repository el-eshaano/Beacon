package com.eshaan.beacon;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class DateAxisFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float timestamp) {
        return new SimpleDateFormat("dd/MM").format(new Date((long) timestamp));
    }
}
