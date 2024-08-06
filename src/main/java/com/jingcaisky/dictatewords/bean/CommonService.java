package com.jingcaisky.dictatewords.bean;
import com.jingcaisky.dictatewords.domain.Words;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CommonService {
    private final String PATH = "src/main/resources/words.xlsx";
    /**
     * 根据给定的类型和数量从Excel中选择单词。
     *
     * @param count 需要选择的单词数量。
     * @param type 单词的类型。
     * @return 包含选中单词的ArrayList。
     */
    public List<Words> selectWords(int count, String type) {
        ArrayList<Words> list = new ArrayList<Words>();
        List<Words> subList = new ArrayList<>();
        File file = new File(PATH);
        String sheetName = "Sheet1";
        try {
            //XSSFWorkbook 是读取2007以上版本的表格，及.xlsx结尾,所以使用XSSFWorkbook
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
            Sheet sheet = workbook.getSheet(sheetName); //获取sheet
            int firstRowIndex = sheet.getFirstRowNum() + 1;//起始行，第一行是标题栏不需要读取
            int lastRowIndex = sheet.getLastRowNum();//结束行
            int sumIndex = 0;
            //循环行
            for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++){
                Row row = sheet.getRow(rIndex);
                if (row != null) {
                    //存储遍历的每行用例的元素值
                    String[] listcase = new String[row.getLastCellNum() - row.getFirstCellNum()];
                    //起始列
                    int firstCellIndex = row.getFirstCellNum();
                    //结束列
                    int lastCellIndex = row.getLastCellNum();
                    for (int cIndex = firstCellIndex; cIndex <= lastCellIndex; cIndex++) {
                        Cell cell = row.getCell(cIndex);
                        if(cell != null){
                            listcase[cIndex] = cell.toString();
                        }
                    }
//                    System.out.println(listcase[0] + "__" + listcase[1] + "__" + listcase[2]);
                    if(listcase[2].equals(type)){
                        Words words = new Words();
                        sumIndex++;
                        words.setId(sumIndex);
                        words.setEn(listcase[0]);
                        words.setZh(listcase[1]);
                        words.setType(listcase[2]);
                        list.add(words);
                    }
                }
            }
            //截取列表
            Collections.shuffle(list);//乱序
            if(list.size() >= count){
                subList = list.subList(0, count);
            }else {
                subList = list;
            }
            workbook.close();//关闭
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return subList;
    }


    /**
     * 从给定的单词列表中随机选择单词生成一个新的列表。
     * 此方法通过随机选择原列表中的单词来创建一个新列表，确保新列表中的每个单词都是从原列表中随机抽取的。
     * 这种方法的用途可以是在需要随机样本集合的情况下，比如随机选取一组单词进行测试或展示。
     *
     * @param oldList 原始单词列表，作为随机选择的源。
     * @return 新的单词列表，包含从原始列表中随机选择的单词。
     */
    public ArrayList<Words> getRandomWords(ArrayList<Words> oldList) {
        // 创建一个新的空列表，用于存储随机选择的单词。
        ArrayList<Words> newList = new ArrayList<Words>();
        // 实例化一个随机数生成器，用于生成随机索引。
        Random random = new Random();
        // 遍历原始列表中的每个元素。
        for (int i = 0; i < oldList.size(); i++) {
            // 生成一个随机索引，用于从原始列表中选择一个单词。
            int index = random.nextInt(oldList.size());
            // 将随机选择的单词添加到新列表中。
            newList.add(oldList.get(index));
        }

        // 返回包含随机选择单词的新列表。
        return newList;
    }

    /**
     * 获取所有单词
     * @return
     */
    public List<Words> selectAllWords() {
        List<Words> list = new ArrayList<>();
        File file = new File(PATH);
        String sheetName = "Sheet1";
        try {
            //XSSFWorkbook 是读取2007以上版本的表格，及.xlsx结尾,所以使用XSSFWorkbook
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
            Sheet sheet = workbook.getSheet(sheetName); //获取sheet
            int firstRowIndex = sheet.getFirstRowNum() + 1;//起始行，第一行是标题栏不需要读取
            int lastRowIndex = sheet.getLastRowNum();//结束行
            int sumIndex = 0;
            //循环行
            for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++){
                Row row = sheet.getRow(rIndex);
                if (row != null) {
                    //存储遍历的每行用例的元素值
                    String[] listcase = new String[row.getLastCellNum() - row.getFirstCellNum()];
                    //起始列
                    int firstCellIndex = row.getFirstCellNum();
                    //结束列
                    int lastCellIndex = row.getLastCellNum();
                    for (int cIndex = firstCellIndex; cIndex <= lastCellIndex; cIndex++) {
                        Cell cell = row.getCell(cIndex);
                        if(cell != null){
                            listcase[cIndex] = cell.toString();
                        }
                    }
//                    System.out.println(listcase[0] + "__" + listcase[1] + "__" + listcase[2]);
                    Words words = new Words();
                    sumIndex++;
                    words.setId(sumIndex);
                    words.setEn(listcase[0]);
                    words.setZh(listcase[1]);
                    words.setType(listcase[2]);
                    list.add(words);
                }
            }
            workbook.close();//关闭
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * 统计单词信息。
     *
     * 该方法用于计算给定单词列表中的单词总数，并以字符串形式返回此统计信息。
     *
     * @param wordsList 单词列表，包含待统计的单词。
     * @return 包含单词总数的字符串信息。
     */
    public String statisticalInformation(List<Words> wordsList, List<Words> wrongWordsList) {
        // 初始化结果字符串
        String result = "";
        // 计算单词列表中的单词总数
        int countAllWords = wordsList.size();
        int countWrongWords = wrongWordsList.size();
        // 将单词总数添加到结果字符串中
        result += "总单词数：" + countAllWords + "，错词数：" + countWrongWords + "\n";
        // 返回包含单词总数的字符串
        return result;
    }

    public ArrayList<String> selectTypes(List<Words> wordsList){
        return (ArrayList<String>) wordsList.stream().map(Words::getType).distinct().collect(Collectors.toList());
    }

    /**
     * 根据给定的类型列表筛选单词列表
     *
     * 该方法通过过滤给定的单词列表，仅返回与指定类型匹配的单词
     * 它使用Java 8的流API来执行过滤操作，提供了一种更简洁和直观的方式来处理集合
     *
     * @param wordsList 单词列表，包含各种类型的单词
     * @param types 类型列表，用于筛选单词只有这些类型中的单词会被包含在返回的列表中
     * @return 返回一个列表，该列表包含与给定类型匹配的所有单词
     */
    public List<Words> filterWordsByType(List<Words> wordsList, ArrayList<String> types) {
        return wordsList.stream()
                .filter(words -> types.contains(words.getType()))
                .collect(Collectors.toList());
    }
}
