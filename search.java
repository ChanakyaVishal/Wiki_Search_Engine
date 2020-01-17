import DocIdTitleMapping.getIdTitleMap;
import Query.QueryInit;
import Query.QueryVariables;

import java.util.Scanner;

public class search {
    public static void main(String args[]){
        //Driver.main(null);
        //getIdTitleMap.main(null);
        Scanner userInput = new Scanner(System.in);
        while(true){
            System.out.println("Search Query: ");
            String query = userInput.nextLine();
            String[] arg = new String[1];
            arg[0] = query;
            QueryInit.main(arg);
        }
    }
}
