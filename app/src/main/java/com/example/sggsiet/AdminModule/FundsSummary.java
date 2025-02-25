package com.example.sggsiet.AdminModule;

public class FundsSummary {
    private String requestType;
    private int totalFunds;

    public FundsSummary() { } // Empty constructor for Firebase

    public FundsSummary(String requestType, int totalFunds) {
        this.requestType = requestType;
        this.totalFunds = totalFunds;
    }

    public String getRequestType() { return requestType; }
    public int getTotalFunds() { return totalFunds; }
}
