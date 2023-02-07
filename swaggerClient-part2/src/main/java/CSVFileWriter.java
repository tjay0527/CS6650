import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVWriter;
public class CSVFileWriter {
    private static final String filePath = "data.csv";
    public void writeCSVHelper(Metrics metrics) throws IOException {
        try {
            File csvFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(csvFile);
            CSVWriter writer = new CSVWriter(fileWriter);
            String[] header = {"Start Time", "Request Type", "Latency", "Response Code"};
            writer.writeNext(header);

            List<Result> resultList = metrics.getResultList();
            for(Result res: resultList){
                String[] data = {res.getStartTime(), res.getType(), String.valueOf(res.getLatency()), res.getResponseCode()};
                writer.writeNext(data);
            }
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
