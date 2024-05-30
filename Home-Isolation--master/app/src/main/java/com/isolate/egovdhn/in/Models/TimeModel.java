package com.isolate.egovdhn.in.Models;

public class TimeModel {

    Double pulse,temp,bp_low,bp_high,SpO2,Resp_rate;

    public TimeModel(Double pulse, Double temp, Double bp_low, Double bp_high, Double spO2, Double resp_rate) {
        this.pulse = pulse;
        this.temp = temp;
        this.bp_low = bp_low;
        this.bp_high = bp_high;
        SpO2 = spO2;
        Resp_rate = resp_rate;
    }

    public Double getPulse() {
        return pulse;
    }

    public void setPulse(Double pulse) {
        this.pulse = pulse;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getBp_low() {
        return bp_low;
    }

    public void setBp_low(Double bp_low) {
        this.bp_low = bp_low;
    }

    public Double getBp_high() {
        return bp_high;
    }

    public void setBp_high(Double bp_high) {
        this.bp_high = bp_high;
    }

    public Double getSpO2() {
        return SpO2;
    }

    public void setSpO2(Double spO2) {
        SpO2 = spO2;
    }

    public Double getResp_rate() {
        return Resp_rate;
    }

    public void setResp_rate(Double resp_rate) {
        Resp_rate = resp_rate;
    }
}
