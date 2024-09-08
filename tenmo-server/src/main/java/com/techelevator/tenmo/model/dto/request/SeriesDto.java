package com.techelevator.tenmo.model.dto.request;

import java.time.LocalDate;
import java.util.Objects;

public class SeriesDto {
    private int frequency = 0;
    private LocalDate end_date = null;

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public LocalDate getEndDate() {
        return end_date;
    }

    public void setEndDate(LocalDate endDate) {
        this.end_date = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeriesDto)) return false;
        SeriesDto seriesDto = (SeriesDto) o;
        return getFrequency() == seriesDto.getFrequency() && Objects.equals(getEndDate(), seriesDto.getEndDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrequency(), getEndDate());
    }
}
