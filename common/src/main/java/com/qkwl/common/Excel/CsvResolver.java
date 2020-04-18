package com.qkwl.common.Excel;

import com.opencsv.CSVReader;
import com.qkwl.common.util.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZKF on 2017/6/26.
 */
public class CsvResolver {

    public static LinkedList<PrivateAlipay> resolverPrivateAlipayCSV(InputStream is) throws IOException {
        // 转换流
        InputStreamReader isr = new InputStreamReader(is, "GBK");
        // 缓冲流
        BufferedReader br = new BufferedReader(isr);
        // CSV读取类
        CSVReader reader = new CSVReader(br);
        // 转数据List
        List<String[]> datalist = reader.readAll();

        LinkedList<PrivateAlipay> list = new LinkedList<>();
        for(int i = 13; i < datalist.size() - 1; i++) {
            String[] data = datalist.get(i);

            String serialNo = data[0];
            String timestamp = data[1]; //url
            String uid = checkUid(data[2]);
            String amount = data[4].equals("") ? "0":data[4];
            String source = data[7];

            PrivateAlipay temp = new PrivateAlipay();
            if(!uid.equals("0")){
                temp.setUid(Integer.valueOf(uid));
            }
            temp.setSerialNo(serialNo.trim());
            temp.setTimestamp(timestamp);
            temp.setAmount(new BigDecimal(amount));
            temp.setSource(source);
            if(temp.getAmount().compareTo(BigDecimal.ZERO) > 0){
                list.add(temp);
            }
        }

        return list;
    }

    public static LinkedList<PublicAlipay> resolverPublicAlipayCSV(InputStream is) throws IOException {

        // 转换流
        InputStreamReader isr = new InputStreamReader(is, "GBK");
        // 缓冲流
        BufferedReader br = new BufferedReader(isr);
        // CSV读取类
        CSVReader reader = new CSVReader(br);
        // 转数据List
        List<String[]> datalist = reader.readAll();

        LinkedList<PublicAlipay> list = new LinkedList<>();
        for(int i = 3; i < datalist.size() - 1; i++) {
            String[] data = datalist.get(i);

            String serialNo = data[2];
            String timestamp = data[1]; //url
            String account = data[12];
            String name = data[13];
            String amount = data[6];
            PublicAlipay temp = new PublicAlipay();
            temp.setSerialNo(serialNo.trim());
            temp.setTimestamp(timestamp);
            if(amount.trim().equals("")){
                temp.setAmount(BigDecimal.ZERO);
            } else{
                temp.setAmount(new BigDecimal(amount));
            }
            temp.setAccount(account.trim());
            temp.setName(name.trim());
            if(temp.getAmount().compareTo(BigDecimal.ZERO) > 0){
                list.add(temp);
            }
        }
        return list;
    }


    private static String checkUid(String str){
        if(StringUtils.isEmpty(str)){
            return "0";
        }
        str = str.trim().toLowerCase().replace("uid","");
        Boolean isNumber = StringUtils.isNumeric(str);
        if(isNumber)
            return str;
        return  "0";
    }



}
