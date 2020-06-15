package com.matus.BillingApp.service.impl;

import com.matus.BillingApp.domain.Currency;
import com.matus.BillingApp.domain.ExchangeRate;
import com.matus.BillingApp.repository.ExchangeRateRepository;
import com.matus.BillingApp.service.ExchangeRateService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public ExchangeRate saveNewExchangeRate(ExchangeRate exchangeRate) {
        return exchangeRateRepository.save(exchangeRate);
    }

    @Override
    public List<ExchangeRate> findByCurrency(Currency currency) {
        return exchangeRateRepository.findByCurrencyFrom(currency);
    }

    @Override
    public Double findAverageExchangeRate(List<ExchangeRate> list) {
        List<Double> listOfValues = new ArrayList<>();

        for (ExchangeRate exchangeRate : list){
            listOfValues.add(exchangeRate.getExchangeRateValue());
        }

        return listOfValues.stream().mapToDouble(value -> value).average().orElse(0.0);
    }

    @Override
    public List<ExchangeRate> findAll() {
        return exchangeRateRepository.findAll();
    }

    @Override
    public List<ExchangeRate> getExchangeRateForLastMonth(List<ExchangeRate> exchangeRates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        List<ExchangeRate> list = new ArrayList<>();

        for (ExchangeRate exchangeRate : exchangeRates){
            Month now = LocalDate.now().getMonth();
            String date  = exchangeRate.getDate();
            Month monthDB = LocalDate.parse(date, formatter).getMonth();
            if(monthDB.equals(now)){
                list.add(exchangeRate);
            }
        }
        return list;
    }

    @Override
    public List<ExchangeRate> getNewExchangeRate(String path) {
        List<ExchangeRate> rates = new ArrayList<>();
        try {
            FileInputStream inputStream = new FileInputStream(new File(path));
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.rowIterator();

            while(rows.hasNext()){
                Row nextRow = rows.next();
                if(nextRow.getRowNum()==0){
                    continue;
                }
                Iterator<Cell> cells = nextRow.cellIterator();
                ExchangeRate exchangeRate = new ExchangeRate();
                while(cells.hasNext()){
                    Cell nextCell = cells.next();
                    int columnIndex = nextCell.getColumnIndex();
                    switch (columnIndex){
                        case 0:
                            Date date = nextCell.getDateCellValue();
                            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                            exchangeRate.setDate(formatter.format(date));
                            break;
                        case 1:
                            if(nextCell.getStringCellValue().equalsIgnoreCase("ZAR")){
                                exchangeRate.setCurrencyFrom(Currency.ZAR);
                            }else if(nextCell.getStringCellValue().equalsIgnoreCase("USD")) {
                                exchangeRate.setCurrencyFrom(Currency.USD);
                            }
                            break;
                        case 2:
                            if(nextCell.getStringCellValue().equalsIgnoreCase("ZAR")){
                                exchangeRate.setCurrencyTo(Currency.ZAR);
                            }else if(nextCell.getStringCellValue().equalsIgnoreCase("USD")) {
                                exchangeRate.setCurrencyTo(Currency.USD);
                            }
                            break;
                        case 3:
                            continue;
                        case 4:
                            exchangeRate.setExchangeRateValue(nextCell.getNumericCellValue());
                            break;
                    }
                }
                rates.add(exchangeRate);
            }
            workbook.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rates;
    }

    @Override
    public List<ExchangeRate> findByDate(String exchangeRateDate) {
        return exchangeRateRepository.findByDate(exchangeRateDate);

    }

    public static void main(String[] args) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        System.out.println(dtf.format(now));


        String filePath = "C:/Users/Matus/Desktop/box/exchange_rates_to_USD.xlsx";
        List<ExchangeRate> rates = new ArrayList<>();
        try {
            FileInputStream inputStream = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.rowIterator();

            while(rows.hasNext()){
                Row nextRow = rows.next();
                if(nextRow.getRowNum()==0){
                    continue;
                }
                Iterator<Cell> cells = nextRow.cellIterator();
                ExchangeRate exchangeRate = new ExchangeRate();
                while(cells.hasNext()){
                    Cell nextCell = cells.next();
                    int columnIndex = nextCell.getColumnIndex();
                    switch (columnIndex){
                        case 0:
                            Date date = nextCell.getDateCellValue();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            exchangeRate.setDate(formatter.format(date));
                            break;
                        case 1:
                            if(nextCell.getStringCellValue().equalsIgnoreCase("ZAR")){
                                exchangeRate.setCurrencyFrom(Currency.ZAR);
                            }else if(nextCell.getStringCellValue().equalsIgnoreCase("USD")) {
                                exchangeRate.setCurrencyFrom(Currency.USD);
                            }
                            break;
                        case 2:
                            if(nextCell.getStringCellValue().equalsIgnoreCase("ZAR")){
                                exchangeRate.setCurrencyTo(Currency.ZAR);
                            }else if(nextCell.getStringCellValue().equalsIgnoreCase("USD")) {
                                exchangeRate.setCurrencyTo(Currency.USD);
                            }
                            break;
                        case 3:
                            continue;
                        case 4:
                            exchangeRate.setExchangeRateValue(nextCell.getNumericCellValue());
                            break;
                    }
                }
                rates.add(exchangeRate);
            }
            workbook.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(rates.get(0).getDate());
    }
}
