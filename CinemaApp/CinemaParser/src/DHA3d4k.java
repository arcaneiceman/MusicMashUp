import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DHA3d4k {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Document doc= null;
		try {
			doc = Jsoup.connect("http://dha3d4kcinema.com/").get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Counldnt get url");
			e.printStackTrace();
		}
		Elements tables= doc.getElementsByTag("table");
//		while(tables.has)
//		System.out.println()
		Elements table = doc.getElementsByClass("imagetable");
		ArrayList<String> Answer=new ArrayList<String>();
		Iterator<Element>clean=table.select("td").iterator();
		
//		for(int i=0; i<Answer.size(); i++){
//			if(!(Answer.get(i).contains("<td>") && Answer.get(i).contains("</td>"))){
//				Answer.remove(i);
//			}
//		}
		
		while(clean.hasNext()){
			String toCheck=clean.next().toString();
			if(!(toCheck.contains("<td>") && toCheck.contains("</td>"))){
				clean.remove();
			}
		}

		Iterator<Element>ite= table.select("td").iterator();
		while(ite.hasNext()){
			Answer.add(ite.next().text());
		}
		
		
		for(int i=1; i<Answer.size(); i=i+3){
			try{
				System.out.println("ShowTime : " + Answer.get(i));
				System.out.println("Movie Name : " + Answer.get(i+1));
				if(Answer.get(i+2).equals("---")){
					System.out.println("Type : 2D");
				}
				else{
					System.out.println("Type : " + Answer.get(i+2) );
				}
			}
			catch(Exception e){
				System.out.println("E");
			}
		}
		
	}



}
