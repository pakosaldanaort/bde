package nearsoft.academy.bigdata.recommendation;




import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;


import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MovieRecommender {

    private int userIndex,movieIndex,numReviews;
    private String path;
    private File fileMovie;
    private Hashtable<String,Integer> users;
    private Hashtable<String,Integer> movies;

    public MovieRecommender(String path) throws IOException {
        this.path = path;
        this.fileMovie = new File(this.path);
        users = new Hashtable<String,Integer>();
        movies = new Hashtable<String,Integer>();
        movieIndex = 1;
        userIndex = 1;
        numReviews = 0;
        this.convertToCsv();



    }

    public void convertToCsv() throws IOException {
        FileReader fileReader = new FileReader(fileMovie);
        BufferedReader bufferReader = new BufferedReader(fileReader);
        File moviesCsv = new File("movies.csv");
        FileWriter fileWriter = new FileWriter(moviesCsv);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        String tempLine,titleLine,textLine,score;
        String [] lineParts;
        int numUser = 0,numMovie=0;
        double sco=0;
        while ((tempLine = bufferReader.readLine())!=null && movieIndex <=1000){
            if(tempLine.length()!=0){
                //System.out.println(tempLine);
                lineParts = tempLine.split(":");
                titleLine = lineParts[0];
                switch (titleLine){
                    case "product/productId":
                        textLine = lineParts[1].trim();
                        if(!movies.containsKey(textLine)){
                            movies.put(textLine,movieIndex);
                            numMovie = movieIndex;
                            movieIndex++;
                        }
                        else{
                            numMovie = movies.get(textLine);
                        }
                        break;

                    case "review/userId":
                        textLine = lineParts[1].trim();

                        if(!users.containsKey(textLine)){
                            users.put(textLine,userIndex);
                            numUser = userIndex;
                            userIndex++;
                        }
                        else{
                            numUser = users.get(textLine);
                        }
                        break;
                    case "review/score":
                        textLine = lineParts[1].trim();
                        score = textLine;
                        printWriter.println(numUser+","+numMovie+","+score);
                        numReviews++;
                        break;


                }

            }



        }
        fileReader.close();
        bufferReader.close();
        fileWriter.close();
        printWriter.close();
        serializerHash(users,"users");
        serializerHash(movies,"movies");
        searchUser("A141HP4LYPWMSR");




    }

    public void serializerHash(Hashtable<String,Integer> hashTable,String fileName) throws IOException {
        File output = new File(fileName+".map");
        FileOutputStream fos = new FileOutputStream(output);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(hashTable);
        oos.flush();
        oos.close();
        fos.close();
    }

    public Hashtable<String,Integer> deserializerHash(String fileName) throws IOException, ClassNotFoundException {
        File input = new File(fileName+".map");
        FileInputStream fis = new FileInputStream(input);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Hashtable<String,Integer> inputHash = (Hashtable<String,Integer>) ois.readObject();
        ois.close();
        fis.close();
        return inputHash;
    }

    public int getTotalReviews() throws IOException {
        return numReviews;
    }

    public int getTotalProducts(){
        System.out.println(movies.size());
        return movies.size();
    }

    public int getTotalUsers(){
        System.out.println(users.size());
        return users.size();
    }

    public void searchUser(String user){
        System.out.println(users.get(user));

    }
    public List<String> getRecommendationsForUser(String user) throws TasteException, IOException {

        int id = users.get(user);

        DataModel model = new FileDataModel(new File("m.csv"));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommender.recommend(2, 3);
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
        List<String> output = new ArrayList<>();

        return output;
    }

    public String getProductID(int value){
        for (String s : movies.keySet()) {
            if (movies.get(s)==value) {
                return s;
            }
        }
        return null;
    }
}
