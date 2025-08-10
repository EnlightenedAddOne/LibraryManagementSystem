package com.example.library.dto;

import java.util.Map;

public class LibraryStatsDTO {
    private int totalBooks;
    private String totalBooksChange; // 例如 "+8%"

    private int totalReaders;
    private String totalReadersChange;

    private int borrowCountThisMonth;
    private String borrowCountChange;

    private int newReadersThisMonth;
    private String newReadersChange;

    private Map<String, Integer> bookCategoryCount; // 分类及数量

    private Map<String, Integer> lastWeekBorrowCountPerDay; // 上周每天借阅量，key为日期字符串
    private int lastWeekTotalBorrow;                        // 上周总借阅量
    private int currentBorrowingCount;                      // 当前在借图书数
    private double lastWeekAvgBorrow;                       // 上周日均借阅
    private String lastWeekReturnRate; 
    
    private int totalBorrowCount;

    private int newBooksThisMonth;

    // getter 和 setter 方法
    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public String getTotalBooksChange() {
        return totalBooksChange;
    }

    public void setTotalBooksChange(String totalBooksChange) {
        this.totalBooksChange = totalBooksChange;
    }

    public int getTotalReaders() {
        return totalReaders;
    }

    public void setTotalReaders(int totalReaders) {
        this.totalReaders = totalReaders;
    }

    public String getTotalReadersChange() {
        return totalReadersChange;
    }

    public void setTotalReadersChange(String totalReadersChange) {
        this.totalReadersChange = totalReadersChange;
    }

    public int getBorrowCountThisMonth() {
        return borrowCountThisMonth;
    }

    public void setBorrowCountThisMonth(int borrowCountThisMonth) {
        this.borrowCountThisMonth = borrowCountThisMonth;
    }

    public String getBorrowCountChange() {
        return borrowCountChange;
    }

    public void setBorrowCountChange(String borrowCountChange) {
        this.borrowCountChange = borrowCountChange;
    }

    public int getNewReadersThisMonth() {
        return newReadersThisMonth;
    }

    public void setNewReadersThisMonth(int newReadersThisMonth) {
        this.newReadersThisMonth = newReadersThisMonth;
    }

    public String getNewReadersChange() {
        return newReadersChange;
    }

    public void setNewReadersChange(String newReadersChange) {
        this.newReadersChange = newReadersChange;
    }

    public Map<String, Integer> getBookCategoryCount() {
        return bookCategoryCount;
    }

    public void setBookCategoryCount(Map<String, Integer> bookCategoryCount) {
        this.bookCategoryCount = bookCategoryCount;
    }

    public int getLastWeekTotalBorrow() {
        return lastWeekTotalBorrow;
    }

    public void setLastWeekTotalBorrow(int lastWeekTotalBorrow) {
        this.lastWeekTotalBorrow = lastWeekTotalBorrow;
    }

    public int getCurrentBorrowingCount() {
        return currentBorrowingCount;
    }

    public void setCurrentBorrowingCount(int currentBorrowingCount) {
        this.currentBorrowingCount = currentBorrowingCount;
    }

    public double getLastWeekAvgBorrow() {
        return lastWeekAvgBorrow;
    }

    public void setLastWeekAvgBorrow(double lastWeekAvgBorrow) {
        this.lastWeekAvgBorrow = lastWeekAvgBorrow;
    }

    public String getLastWeekReturnRate() {
        return lastWeekReturnRate;
    }

    public void setLastWeekReturnRate(String lastWeekReturnRate) {
        this.lastWeekReturnRate = lastWeekReturnRate;
    }

    public int getTotalBorrowCount() {
        return totalBorrowCount;
    }

    public void setTotalBorrowCount(int totalBorrowCount) {
        this.totalBorrowCount = totalBorrowCount;
    }

    public Map<String, Integer> getLastWeekBorrowCountPerDay() {
        return lastWeekBorrowCountPerDay;
    }

    public void setLastWeekBorrowCountPerDay(Map<String, Integer> lastWeekBorrowCountPerDay) {
        this.lastWeekBorrowCountPerDay = lastWeekBorrowCountPerDay;
    }

    public int getNewBooksThisMonth() {
        return newBooksThisMonth;
    }

    public void setNewBooksThisMonth(int newBooksThisMonth) {
        this.newBooksThisMonth = newBooksThisMonth;
    }
}