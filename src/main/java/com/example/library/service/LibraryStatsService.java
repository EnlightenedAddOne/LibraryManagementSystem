package com.example.library.service;

import com.example.library.dto.LibraryStatsDTO;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDateTime;

@Service
public class LibraryStatsService {
    // LocalDate 转 Date
    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    public LibraryStatsDTO getLibraryStats() {
        LibraryStatsDTO dto = new LibraryStatsDTO();

        // 1. 馆藏图书总数及变化
        int totalBooks = bookRepository.countAll();
        int lastMonthBooks = bookRepository.countByCreateTimeBetween(
                toDate(YearMonth.now().minusMonths(1).atDay(1)),
                toDate(YearMonth.now().atDay(1))
        );
        dto.setTotalBooks(totalBooks);
        dto.setTotalBooksChange(calcChange(totalBooks, lastMonthBooks));

        // 2. 读者总数及变化
        int totalReaders = userRepository.countAll();
        LocalDateTime startOfLastMonth = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime startOfThisMonth = YearMonth.now().atDay(1).atStartOfDay();
        int lastMonthReaders = userRepository.countByCreatedAtBetween(
                startOfLastMonth,
                startOfThisMonth
        );
        dto.setTotalReaders(totalReaders);
        dto.setTotalReadersChange(calcChange(totalReaders, lastMonthReaders));

        // 3. 本月借阅量及变化
        int borrowThisMonth = borrowRecordRepository.countByBorrowDateBetween(
                toDate(YearMonth.now().atDay(1)),
                toDate(LocalDate.now())
        );
        int borrowLastMonth = borrowRecordRepository.countByBorrowDateBetween(
                toDate(YearMonth.now().minusMonths(1).atDay(1)),
                toDate(YearMonth.now().atDay(1).minusDays(1))
        );
        dto.setBorrowCountThisMonth(borrowThisMonth);
        dto.setBorrowCountChange(calcChange(borrowThisMonth, borrowLastMonth));

        // 4. 本月新增读者及变化
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfLastMonth = startOfThisMonth.minusSeconds(1);

        int newReadersThisMonth = userRepository.countByCreatedAtBetween(
                startOfThisMonth,
                now
        );
        int newReadersLastMonth = userRepository.countByCreatedAtBetween(
                startOfLastMonth,
                endOfLastMonth
        );
        dto.setNewReadersThisMonth(newReadersThisMonth);
        dto.setNewReadersChange(calcChange(newReadersThisMonth, newReadersLastMonth));

        // 5. 各类书籍总数
        Map<String, Integer> categoryCount = new HashMap<>();
        List<Object[]> categoryList = bookRepository.countGroupByCategory();
        for (Object[] row : categoryList) {
            categoryCount.put((String) row[0], ((Number) row[1]).intValue());
        }
        dto.setBookCategoryCount(categoryCount);


         // 计算上周一和上周日
        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate lastSunday = lastMonday.plusDays(6);

        // 1. 上周每天借阅量
        Map<String, Integer> lastWeekBorrowCountPerDay = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int lastWeekTotalBorrow = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate day = lastMonday.plusDays(i);
            int count = borrowRecordRepository.countByBorrowDateBetween(
                    day.atStartOfDay(), day.plusDays(1).atStartOfDay().minusSeconds(1)
            );
            lastWeekBorrowCountPerDay.put(day.format(formatter), count);
            lastWeekTotalBorrow += count;
        }
        dto.setLastWeekBorrowCountPerDay(lastWeekBorrowCountPerDay);

        // 2. 上周总借阅量
        dto.setLastWeekTotalBorrow(lastWeekTotalBorrow);

        // 3. 当前在借图书数（未归还的记录数）
        int currentBorrowingCount = borrowRecordRepository.countByReturnDateIsNull();
        dto.setCurrentBorrowingCount(currentBorrowingCount);

        // 4. 上周日均借阅
        dto.setLastWeekAvgBorrow(lastWeekTotalBorrow / 7.0);

        // 5. 上周归还率
        int lastWeekReturnCount = borrowRecordRepository.countByReturnDateBetween(
                lastMonday.atStartOfDay(), lastSunday.plusDays(1).atStartOfDay().minusSeconds(1)
        );
        String returnRate = lastWeekTotalBorrow == 0 ? "0%" :
                String.format("%.1f%%", (lastWeekReturnCount * 100.0 / lastWeekTotalBorrow));
        dto.setLastWeekReturnRate(returnRate);

        int totalBorrowCount = borrowRecordRepository.countAllBorrowRecords();
        dto.setTotalBorrowCount(totalBorrowCount);

        return dto;
    }

    private String calcChange(int now, int last) {
        if (last == 0) return "+100%";
        double change = ((double) (now - last) / last) * 100;
        return (change >= 0 ? "+" : "") + String.format("%.1f", change) + "%";
    }
}