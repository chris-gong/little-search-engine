package search;

import java.io.FileNotFoundException;




public class Test {
	
	public static void main(String [ ] args) throws FileNotFoundException
	{
		LittleSearchEngine engine = new LittleSearchEngine();
		engine.makeIndex("docs.txt","noisewords.txt");
		
		System.out.println(engine.keywordsIndex);
		
		System.out.println(engine.top5search("bus","car"));
		
	}
	
}
