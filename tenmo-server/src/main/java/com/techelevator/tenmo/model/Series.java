package com.techelevator.tenmo.model;

import com.techelevator.tenmo.model.dto.request.SeriesDto;

import java.time.LocalDate;

public class Series extends SeriesDto {
    int seriesId;

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }
}
