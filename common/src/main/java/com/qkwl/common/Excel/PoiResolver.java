package com.qkwl.common.Excel;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * Created by ZKF on 2017/6/17.
 */
public class PoiResolver {

    public static LinkedList<PrivateAlipay> resolverAlipayExcel(InputStream is) throws IOException {

        HSSFWorkbook wb = new HSSFWorkbook(is);
        //Excel工作表
        HSSFSheet sheet = wb.getSheetAt(0);

        LinkedList<PrivateAlipay> list = new LinkedList<>();
        for(int i=1; i<sheet.getLastRowNum()+1; i++) {
            HSSFRow row = sheet.getRow(i);

            String serialNo = row.getCell(0).getStringCellValue(); //名称
            Double timestamp = row.getCell(1).getNumericCellValue(); //url
            Integer uid = validateUidCell(row.getCell(2), row.getCell(3));
            String amount = String.valueOf(row.getCell(4).getNumericCellValue());
            String source = row.getCell(7).getStringCellValue();

            PrivateAlipay temp = new PrivateAlipay();
            if(uid != 0){
                temp.setUid(uid);
            }
            temp.setSerialNo(serialNo.trim());
            //temp.setTimestamp(DateUtil.getJavaDate(timestamp));
            temp.setAmount(new BigDecimal(amount));
            temp.setSource(source);

            list.add(temp);
        }

        return list;
    }


    private static Integer validateUidCell(Cell name, Cell remark){
        Integer temp = 0;
        if(name != null && name.getCellTypeEnum().equals(CellType.NUMERIC)){
            temp = (int) name.getNumericCellValue();
        } else if(name != null && name.getCellTypeEnum().equals(CellType.STRING)){
            if(!StringUtils.isEmpty(name.getStringCellValue())){
                temp = Integer.valueOf(checkUid(name.getStringCellValue()));
            }
        }

        if(temp == 0 && remark != null && remark.getCellTypeEnum().equals(CellType.NUMERIC)){
            if(remark.getCellTypeEnum().equals(CellType.NUMERIC)){
                temp = (int) remark.getNumericCellValue();
            } else if(remark != null && remark.getCellTypeEnum().equals(CellType.STRING)){
                if(!StringUtils.isEmpty(remark.getStringCellValue())){
                    temp = Integer.valueOf(checkUid(remark.getStringCellValue()));
                }
            }
        }
        return temp;
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
