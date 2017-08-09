package com.tmoncorp.admin.file;

import com.tmoncorp.admin.domain.SynonymCategory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sk2rldnr on 2017-07-14.
 */
// csv 파일을 읽어서 Synonym Category table과 같은 형식으로 변환
public class ReadCSV {

    public List<SynonymCategory> readFromFile(String fileContent) throws Exception {

        List<SynonymCategory> synonymCategoryList = new ArrayList<>();
        String[] fileContentLines = fileContent.split("\n");
        int row = 0;

        try {
            for (row = 1; row < fileContentLines.length; row++) {
                String[] cell = fileContentLines[row].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                double weight = Double.parseDouble(cell[7]);
                if (weight > 100 || weight < 0)
                    weight = 0;

                synonymCategoryList.add(new SynonymCategory(Integer.parseInt(cell[0]), cell[1].replace("\"", ""),
                        cell[2].replace("\"", ""), cell[3].replace("\"", ""),
                        cell[4].replace("\"", ""), cell[5].replace("\"", ""),
                        cell[6].replace("\"", ""), weight, Boolean.valueOf(cell[8]),
                        cell[9].replace("\"", ""), stringToTimestamp(cell[10])));
            }
        } catch (IndexOutOfBoundsException e) {
            throw new Exception(row + "'s row is wrong!", e);
        } catch (ParseException | NumberFormatException e) {
            throw new Exception(row + "'s row date format is wrong!", e);
        }

        return synonymCategoryList;
    }

    private Timestamp stringToTimestamp(String stringTime) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date parsedTimeStamp = dateFormat.parse(stringTime);

        return new Timestamp(parsedTimeStamp.getTime());
    }

}
