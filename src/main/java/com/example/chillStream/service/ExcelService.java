package com.example.chillStream.service;

import com.example.chillStream.constant.Country;
import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.dto.ItemCrawlDto;
import com.example.chillStream.entity.Content;
import com.example.chillStream.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Arrays;
import java.util.AbstractMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

@Service
@RequiredArgsConstructor
public class ExcelService {
    
    private final TMDBService tmdbService;

    public ByteArrayInputStream generateExcelReport() throws IOException {
        List<ItemCrawlDto> contents = tmdbService.getAllCrawledItems();
        List<ItemCrawlDto> monthlyContents = tmdbService.getMonthlyTrending();
        
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // 스타일 정의
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            // 표지 시트 생성
            Sheet coverSheet = workbook.createSheet("표지");
            createCoverSheet(coverSheet, titleStyle, contents);
            
            // 요약 시트 생성
            Sheet summarySheet = workbook.createSheet("요약");
            createSummarySheet(summarySheet, headerStyle, dataStyle, contents);
            
            // 상세 분석 시트들 생성
            Sheet genreSheet = workbook.createSheet("장르 분석");
            createGenreAnalysis(genreSheet, headerStyle, dataStyle, contents);
            
            Sheet countryGenreSheet = workbook.createSheet("국가별 장르 분석");
            createCountryGenreAnalysis(countryGenreSheet, headerStyle, dataStyle, contents);
            
            Sheet monthlySheet = workbook.createSheet("월간 인기 콘텐츠");
            createMonthlyAnalysis(monthlySheet, headerStyle, dataStyle, monthlyContents);
            
            Sheet popularSheet = workbook.createSheet("인기 콘텐츠");
            createPopularContents(popularSheet, headerStyle, dataStyle, contents);
            
            // 모든 시트의 열 너비 자동 조정
            for (Sheet sheet : new Sheet[]{coverSheet, summarySheet, genreSheet, countryGenreSheet, monthlySheet, popularSheet}) {
                for (int i = 0; i < 10; i++) {
                    sheet.autoSizeColumn(i);
                }
            }
            
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private void createCoverSheet(Sheet sheet, CellStyle titleStyle, List<ItemCrawlDto> contents) {
        // 제목 행
        Row titleRow = sheet.createRow(1);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("CHILLSTREAM 콘텐츠 분석 보고서");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 5));
        
        // 날짜 정보
        Row dateRow = sheet.createRow(3);
        Cell dateCell = dateRow.createCell(1);
        dateCell.setCellValue("작성일자: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        
        // 기본 통계 정보
        Row statsRow = sheet.createRow(5);
        statsRow.createCell(1).setCellValue("전체 콘텐츠 수: " + contents.size());
        
        double avgRating = contents.stream()
            .mapToDouble(ItemCrawlDto::getVoteAverage)
            .average()
            .orElse(0.0);
        Row avgRow = sheet.createRow(6);
        avgRow.createCell(1).setCellValue(String.format("평균 평점: %.2f", avgRating));
    }

    private void createSummarySheet(Sheet sheet, CellStyle headerStyle, CellStyle dataStyle, List<ItemCrawlDto> contents) {
        // 1. 전체 요약 섹션
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("■ 전체 통계 요약");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));  // 2칸만 병합
        
        int rowNum = 2;
        createSummaryRow(sheet, rowNum++, "전체 콘텐츠 수", String.valueOf(contents.size()) + "개", dataStyle);
        createSummaryRow(sheet, rowNum++, "평균 평점", String.format("%.2f점", getAverageRating(contents)), dataStyle);
        createSummaryRow(sheet, rowNum++, "평균 인기도", String.format("%.2f", getAveragePopularity(contents)), dataStyle);
        
        // 2. 장르별 통계 섹션
        rowNum += 2;
        Row genreTitle = sheet.createRow(rowNum++);
        Cell genreTitleCell = genreTitle.createCell(0);
        genreTitleCell.setCellValue("■ 장르별 통계");
        genreTitleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 2));  // 3칸만 병합
        
        // 장르별 헤더
        Row genreHeader = sheet.createRow(rowNum++);
        genreHeader.createCell(0).setCellValue("장르");
        genreHeader.createCell(1).setCellValue("콘텐츠 수");
        genreHeader.createCell(2).setCellValue("비율(%)");
        for (Cell cell : genreHeader) {
            cell.setCellStyle(headerStyle);
        }
        
        // 장르별 데이터
        Map<MainGenre, Long> genreCounts = contents.stream()
            .filter(c -> c.getMainGenre() != null)
            .collect(Collectors.groupingBy(ItemCrawlDto::getMainGenre, Collectors.counting()));
        
        for (Map.Entry<MainGenre, Long> entry : genreCounts.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey().name());
            row.createCell(1).setCellValue(entry.getValue());
            row.createCell(2).setCellValue(String.format("%.1f", (entry.getValue() * 100.0) / contents.size()));
            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }
        
        // 3. 국가별 통계 섹션
        rowNum += 2;
        Row countryTitle = sheet.createRow(rowNum++);
        Cell countryTitleCell = countryTitle.createCell(0);
        countryTitleCell.setCellValue("■ 국가별 통계");
        countryTitleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 3));  // 4칸만 병합
        
        // 국가별 헤더
        Row countryHeader = sheet.createRow(rowNum++);
        countryHeader.createCell(0).setCellValue("국가");
        countryHeader.createCell(1).setCellValue("콘텐츠 수");
        countryHeader.createCell(2).setCellValue("비율(%)");
        countryHeader.createCell(3).setCellValue("주요 장르");
        for (Cell cell : countryHeader) {
            cell.setCellStyle(headerStyle);
        }
        
        // 국가별 데이터
        Map<String, List<ItemCrawlDto>> countryGroups = getCountryGroups(contents);
        for (Map.Entry<String, List<ItemCrawlDto>> entry : countryGroups.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            List<ItemCrawlDto> countryContents = entry.getValue();
            
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(countryContents.size());
            row.createCell(2).setCellValue(String.format("%.1f", (countryContents.size() * 100.0) / contents.size()));
            row.createCell(3).setCellValue(getMostFrequentGenreForCountry(countryContents));
            
            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private void createGenreAnalysis(Sheet sheet, CellStyle headerStyle, CellStyle dataStyle, List<ItemCrawlDto> contents) {
        // 제목 추가
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("■ 장르별 콘텐츠 분석");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        
        // 헤더 생성
        Row headerRow = sheet.createRow(2);
        String[] headers = {"장르 구분", "콘텐츠 수", "비율(%)", "평균 평점", "평균 인기도"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 데이터 입력
        int rowNum = 3;
        Map<MainGenre, Long> mainGenreCounts = contents.stream()
            .filter(content -> content != null && content.getMainGenre() != null)
            .collect(Collectors.groupingBy(ItemCrawlDto::getMainGenre, Collectors.counting()));
        
        for (MainGenre genre : MainGenre.values()) {
            Row row = sheet.createRow(rowNum++);
            long count = mainGenreCounts.getOrDefault(genre, 0L);
            
            row.createCell(0).setCellValue(genre.name());
            row.createCell(1).setCellValue(count);
            row.createCell(2).setCellValue(String.format("%.1f", (count * 100.0) / contents.size()));
            row.createCell(3).setCellValue(String.format("%.1f", getAverageRating(filterByGenre(contents, genre))));
            row.createCell(4).setCellValue(String.format("%.1f", getAveragePopularity(filterByGenre(contents, genre))));
            
            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private void createMonthlyAnalysis(Sheet sheet, CellStyle headerStyle, CellStyle dataStyle, List<ItemCrawlDto> monthlyContents) {
        // 제목 추가
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("■ 월간 인기 콘텐츠 TOP 10");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        
        // 헤더 생성
        Row headerRow = sheet.createRow(2);
        String[] headers = {"순위", "제목", "메인 장르", "서브 장르", "평점", "인기도"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 데이터 입력 (기존 코드와 동일하지만 시작 행이 3으로 변경)
        int rowNum = 3;
        for (ItemCrawlDto content : monthlyContents) {
            if (content == null) continue;
            
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowNum - 2);  // 순위는 1부터 시작
            row.createCell(1).setCellValue(content.getTitle());
            row.createCell(2).setCellValue(content.getMainGenre().name());
            row.createCell(3).setCellValue(content.getSubGenre().name());
            row.createCell(4).setCellValue(String.format("%.1f", content.getVoteAverage()));
            row.createCell(5).setCellValue(String.format("%.1f", content.getPopularity()));
            
            // 스타일 적용
            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
            rowNum++;
        }
    }

    private void createPopularContents(Sheet sheet, CellStyle headerStyle, CellStyle dataStyle, List<ItemCrawlDto> contents) {
        // 제목 추가
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("■ 전체 인기 콘텐츠 TOP 10");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        
        // 헤더 생성
        Row headerRow = sheet.createRow(2);
        String[] headers = {"순위", "제목", "메인 장르", "서브 장르", "평점", "인기도"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 인기도 기준으로 정렬하여 상위 10개 추출
        List<ItemCrawlDto> popularContents = contents.stream()
            .sorted((c1, c2) -> Double.compare(c2.getPopularity(), c1.getPopularity()))
            .limit(10)
            .collect(Collectors.toList());
        
        // 데이터 입력
        int rowNum = 3;
        for (ItemCrawlDto content : popularContents) {
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowNum - 2);  // 순위는 1부터 시작
            row.createCell(1).setCellValue(content.getTitle());
            row.createCell(2).setCellValue(content.getMainGenre().name());
            row.createCell(3).setCellValue(content.getSubGenre().name());
            row.createCell(4).setCellValue(String.format("%.1f", content.getVoteAverage()));
            row.createCell(5).setCellValue(String.format("%.1f", content.getPopularity()));
            
            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
            rowNum++;
        }
    }

    private void createCountryGenreAnalysis(Sheet sheet, CellStyle headerStyle, CellStyle dataStyle, List<ItemCrawlDto> contents) {
        // 제목 추가
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("■ 국가별 장르 분석");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        
        // 헤더 생성
        Row headerRow = sheet.createRow(2);
        String[] headers = {"국가", "주요 장르", "콘텐츠 수", "평균 평점", "평균 인기도"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 데이터 입력
        int rowNum = 3;
        Map<String, List<ItemCrawlDto>> countryGroups = getCountryGroups(contents);
        
        for (Map.Entry<String, List<ItemCrawlDto>> entry : countryGroups.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            List<ItemCrawlDto> countryContents = entry.getValue();
            
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(getMostFrequentGenreForCountry(countryContents));
            row.createCell(2).setCellValue(countryContents.size());
            row.createCell(3).setCellValue(String.format("%.1f", getAverageRating(countryContents)));
            row.createCell(4).setCellValue(String.format("%.1f", getAveragePopularity(countryContents)));
            
            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private void createSummaryRow(Sheet sheet, int rowNum, String label, String value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        Cell valueCell = row.createCell(1);
        
        labelCell.setCellValue(label);
        valueCell.setCellValue(value);
        
        labelCell.setCellStyle(style);
        valueCell.setCellStyle(style);
    }

    private double getAverageRating(List<ItemCrawlDto> contents) {
        return contents.stream()
            .mapToDouble(ItemCrawlDto::getVoteAverage)
            .average()
            .orElse(0.0);
    }

    private double getAveragePopularity(List<ItemCrawlDto> contents) {
        return contents.stream()
            .mapToDouble(ItemCrawlDto::getPopularity)
            .average()
            .orElse(0.0);
    }

    private void createSummaryTable(Sheet sheet, int startRow, List<ItemCrawlDto> contents, CellStyle headerStyle, CellStyle dataStyle) {
        String[][] summaryData = {
            {"총 콘텐츠 수", String.valueOf(contents.size()) + "개"},
            {"평균 평점", String.format("%.2f점", getAverageRating(contents))},
            {"평균 인기도", String.format("%.2f", getAveragePopularity(contents))},
            {"최다 제작 국가", getMostFrequentCountry(contents)},
            {"최다 장르", getMostFrequentGenre(contents)}
        };

        for (int i = 0; i < summaryData.length; i++) {
            Row row = sheet.createRow(startRow + i);
            Cell labelCell = row.createCell(0);
            Cell valueCell = row.createCell(1);
            
            labelCell.setCellValue(summaryData[i][0]);
            valueCell.setCellValue(summaryData[i][1]);
            
            labelCell.setCellStyle(headerStyle);
            valueCell.setCellStyle(dataStyle);
        }
    }

    private String getMostFrequentCountry(List<ItemCrawlDto> contents) {
        return contents.stream()
            .filter(c -> c.getProductionCountries() != null)
            .flatMap(c -> Arrays.stream(c.getProductionCountries().split(",")))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.groupingBy(c -> c, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }

    private String getMostFrequentGenre(List<ItemCrawlDto> contents) {
        return contents.stream()
            .filter(c -> c.getMainGenre() != null)
            .collect(Collectors.groupingBy(ItemCrawlDto::getMainGenre, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> e.getKey().name())
            .orElse("N/A");
    }

    private void createGenreAnalysisTable(Sheet sheet, int startRow, List<ItemCrawlDto> contents, CellStyle headerStyle, CellStyle dataStyle) {
        // 헤더 생성
        Row headerRow = sheet.createRow(startRow++);
        String[] headers = {"장르", "콘텐츠 수", "평균 평점", "평균 인기도"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 장르별 데이터 수집 및 출력
        Map<MainGenre, List<ItemCrawlDto>> genreGroups = contents.stream()
            .filter(c -> c.getMainGenre() != null)
            .collect(Collectors.groupingBy(ItemCrawlDto::getMainGenre));

        for (Map.Entry<MainGenre, List<ItemCrawlDto>> entry : genreGroups.entrySet()) {
            Row row = sheet.createRow(startRow++);
            List<ItemCrawlDto> genreContents = entry.getValue();

            row.createCell(0).setCellValue(entry.getKey().name());
            row.createCell(1).setCellValue(genreContents.size());
            row.createCell(2).setCellValue(String.format("%.2f", getAverageRating(genreContents)));
            row.createCell(3).setCellValue(String.format("%.2f", getAveragePopularity(genreContents)));

            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private void createCountryAnalysisTable(Sheet sheet, int startRow, List<ItemCrawlDto> contents, CellStyle headerStyle, CellStyle dataStyle) {
        // 헤더 생성
        Row headerRow = sheet.createRow(startRow++);
        String[] headers = {"국가", "콘텐츠 수", "주요 장르", "평균 평점", "평균 인기도"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 국가별 데이터 수집
        Map<String, List<ItemCrawlDto>> countryGroups = contents.stream()
            .filter(c -> c.getProductionCountries() != null)
            .flatMap(c -> Arrays.stream(c.getProductionCountries().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(country -> new AbstractMap.SimpleEntry<>(country, c)))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
            ));

        // 데이터 출력
        for (Map.Entry<String, List<ItemCrawlDto>> entry : countryGroups.entrySet()) {
            Row row = sheet.createRow(startRow++);
            List<ItemCrawlDto> countryContents = entry.getValue();

            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(countryContents.size());
            row.createCell(2).setCellValue(getMostFrequentGenreForCountry(countryContents));
            row.createCell(3).setCellValue(String.format("%.2f", getAverageRating(countryContents)));
            row.createCell(4).setCellValue(String.format("%.2f", getAveragePopularity(countryContents)));

            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private String getMostFrequentGenreForCountry(List<ItemCrawlDto> contents) {
        return contents.stream()
            .filter(c -> c.getMainGenre() != null)
            .collect(Collectors.groupingBy(ItemCrawlDto::getMainGenre, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> e.getKey().name())
            .orElse("N/A");
    }

    private Map<String, List<ItemCrawlDto>> getCountryGroups(List<ItemCrawlDto> contents) {
        return contents.stream()
            .filter(c -> c.getProductionCountries() != null)
            .flatMap(c -> Arrays.stream(c.getProductionCountries().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(country -> new AbstractMap.SimpleEntry<>(country, c)))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
            ));
    }

    private List<ItemCrawlDto> filterByGenre(List<ItemCrawlDto> contents, MainGenre genre) {
        return contents.stream()
            .filter(c -> c != null && c.getMainGenre() == genre)
            .collect(Collectors.toList());
    }
}