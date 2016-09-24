package search;

import java.io.*;
import java.util.*;


/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		Scanner sc = new Scanner(new File(docFile));
		HashMap<String, Occurrence> docKeyWordsIndex = new HashMap<String,Occurrence>(1000,2.0f);
		while(sc.hasNext()){
			String next = sc.next();
			
			String word = getKeyWord(next);
			//if the word is a legal keyword
			if(word != null){
				//if it's already in the hashtable, then update the frequency of the object there
				if(docKeyWordsIndex.containsKey(word)){
					docKeyWordsIndex.put(word, new Occurrence(docFile , docKeyWordsIndex.get(word).frequency + 1));
				}
				//make a new object if the word's never been seen before
				else{
					docKeyWordsIndex.put(word, new Occurrence(docFile, 1));
				}
			}
			
			
			
		}
		return docKeyWordsIndex;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		for (Map.Entry<String, Occurrence> entry : kws.entrySet()) {
		    String key = entry.getKey();
		    Occurrence value = entry.getValue();
		    if(!keywordsIndex.containsKey(key)){
		    	//edit this, still has to call insertlastoccurrence
		    	keywordsIndex.put(key, new ArrayList<Occurrence>());
		    	keywordsIndex.get(key).add(value);
		    	insertLastOccurrence(keywordsIndex.get(key));
		    }
		    else{
		    	keywordsIndex.get(key).add(value);
		    	insertLastOccurrence(keywordsIndex.get(key));
		    	/*keywordsIndex.get(key).add(value);
		    	//sort the arraylist
		    	int first;
		    	for(int i = keywordsIndex.get(key).size()-1; i > 0 ;i--){
		    		first = 0;
		    		for(int j = 1; j <= i; j++){
		    			if(keywordsIndex.get(key).get(j).frequency < keywordsIndex.get(key).get(first).frequency){
		    				first = j;
		    			}
		    		}
		    		Occurrence temp = keywordsIndex.get(key).get(first);
		    		keywordsIndex.get(key).set(first, keywordsIndex.get(key).get(i));
		    		keywordsIndex.get(key).set(i, temp);
		    	}*/
		    }
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE COMPILE
		String keyWord = word;
		int firstNonLetterIndex = -1;
		//if the first char isn't a letter, then it can't be a keyword
		//ex" . !a ---y !and 
		if(!Character.isLetter(keyWord.charAt(0))){
			return null;
		}
		//finds first nonletter
		for(int i = 0; i < keyWord.length(); i++){
			if(!Character.isLetter(keyWord.charAt(i))){
				firstNonLetterIndex = i;
				break;
			}
		}
		
		//checks if punctuation occurs in the middle of letters
		//aka whether or not first nonletter leads to TRAILING punctuation or not
		//starting from the first nonletter
		if(firstNonLetterIndex != -1){
			String halfWord = keyWord.substring(firstNonLetterIndex);
			for(int i = 0; i < halfWord.length(); i++){
				if(halfWord.charAt(i)!='.' && halfWord.charAt(i) != ',' &&halfWord.charAt(i)!=':' && halfWord.charAt(i) != '?' &&
						halfWord.charAt(i)!=';' && halfWord.charAt(i) != '!'){
					
					return null;
				}
			}
			//cuts all the trailing punctuation
			keyWord = keyWord.substring(0, firstNonLetterIndex);
		}
		//if the word is in the noisewords hashtable
		if(noiseWords.containsKey(keyWord.toLowerCase())){
			
			return null;
		}
		else{
			return keyWord.toLowerCase();
		}
		
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		//only works right now if there are at least two elements in occs
		//have to deal with case where there is only one element
		//doesn't work for lists of size 2 either
		if(occs.size()==1){
			return null;
		}
		int lo = 0;
		int hi = occs.size()-2;
		int addedValue = occs.get(occs.size()-1).frequency;
		ArrayList<Integer> midpoints = new ArrayList<Integer>();
		int mid = 0;
		while(lo <= hi){
			mid = (lo + hi)/2;
			midpoints.add(mid);
			//descending order
			//do u actually break out of the loop if this happens?
			if(occs.get(mid).frequency == addedValue){
				break;
			}
			else if(occs.get(mid).frequency < addedValue){
				hi = mid - 1;
			}
			else{
				lo = mid + 1;
			}
		}
		if(occs.get(mid).frequency <= addedValue){
			occs.add(mid, occs.get(occs.size()-1));
			
		}
		else{
			occs.add(mid+1, occs.get(occs.size()-1));
			
		}
		
		occs.remove(occs.size()-1);
		
		return midpoints;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		ArrayList<String> top5 = new ArrayList<String>();
		if(!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)){
			return null;
		}
		else if(!keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)){
			if(keywordsIndex.get(kw2).size() < 5){
				for(int i = 0; i < keywordsIndex.get(kw2).size() ; i++){
					top5.add(keywordsIndex.get(kw2).get(i).document);
				}
				return top5;
			}
			else{
				for(int i = 0; i < 5; i++){
					top5.add(keywordsIndex.get(kw2).get(i).document);
				}
				return top5;
			}
		}
		else if(keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)){
			if(keywordsIndex.get(kw1).size() < 5){
				for(int i = 0; i < keywordsIndex.get(kw1).size(); i++){
					top5.add(keywordsIndex.get(kw1).get(i).document);
				}
				
				return top5;
			}
			else{ 
				for(int i = 0; i < 5; i++){
					top5.add(keywordsIndex.get(kw1).get(i).document);
				}
				
				return top5;
			}
		}
		else{
			
			ArrayList<Occurrence> keywordOne = keywordsIndex.get(kw1);
			ArrayList<Occurrence> keywordTwo = keywordsIndex.get(kw2);
			int n = keywordOne.size();
			int m = keywordTwo.size();
			int i = 0;
			int j = 0;
			
			while(i < n && j < m && top5.size() < 5){
				//originally if the freqs were equal what I would do is check if docs were equal first 
				//then add the first keyword doc first if they had equal frequencies, 
				//the second keyword doc would be added if it had a greater freq
				//then proceed with the next iteration, 
				//either way both i and j get incremented in this if statement
				//also the next if statement would be >= instead of >
				//the result of the old way would be for ex
				// a b c d e and f g h i j all have the same frequency
				//the result woud have been a b c d e and now its a f b g c
				/*
				if(keywordOne.get(i).document.equals(keywordTwo.get(j).document)){
					if(keywordOne.get(i).frequency >= keywordTwo.get(j).frequency){
						top5.add(keywordOne.get(i).document);
					}
					else{
						top5.add(keywordTwo.get(j).document);
					}
					i++;
					j++;
					continue;
					
				}
				
				if(keywordOne.get(i).frequency >= keywordTwo.get(j).frequency){
				
					if(!top5.contains(keywordOne.get(i).document)){
					
						top5.add(keywordOne.get(i).document);
						
					}
					
					i++;
					
					continue;
				}
				else{
					if(!top5.contains(keywordTwo.get(j).document)){
						top5.add(keywordTwo.get(j).document);
					}
					j++;
					continue;
				}
				 */
				if(keywordOne.get(i).frequency == keywordTwo.get(j).frequency){
					if(!top5.contains(keywordOne.get(i).document)){
						top5.add(keywordOne.get(i).document);
					}
					if(!top5.contains(keywordTwo.get(j).document) && top5.size()<5) {
						top5.add(keywordTwo.get(j).document);
					}
					i++;
					j++;
					continue;
					
				}
				
				if(keywordOne.get(i).frequency > keywordTwo.get(j).frequency){
				
					if(!top5.contains(keywordOne.get(i).document)){
					
						top5.add(keywordOne.get(i).document);
						
					}
					
					i++;
					
					continue;
				}
				else{
					if(!top5.contains(keywordTwo.get(j).document)){
						top5.add(keywordTwo.get(j).document);
					}
					j++;
					continue;
				}
				
			}
			while(i < n && top5.size() < 5){
				if(!top5.contains(keywordOne.get(i).document)){
					top5.add(keywordOne.get(i).document);
				}
				i++;
			}
			while(j < m && top5.size() < 5){
				if(!top5.contains(keywordTwo.get(j).document)){
					top5.add(keywordTwo.get(j).document);
					
				}
				j++;
			} 
			
			/*
			ArrayList<Occurrence> merged = new ArrayList<Occurrence>();
			for(int i = 0; i < keywordOne.size(); i++){
				boolean counterPartfound = false;
		
				for(int j = 0; j < keywordTwo.size(); j++){
					if(keywordOne.get(i).document.equals(keywordOne.get(j).document)){
						counterPartfound = true;
						if(keywordOne.get(i).frequency < keywordTwo.get(j).frequency){
							merged.add(keywordTwo.get(j));
							
						}
						else{
							merged.add(keywordOne.get(i));
						}
						insertLastOccurrence(merged);
					}
					
				}
				if(!counterPartfound){
					merged.add(keywordOne.get(i));
					insertLastOccurrence(merged);
				}
			}
			for(int i = 0; i < keywordTwo.size(); i++){
				boolean counterPartfound = false;
				for(int j = 0; j < keywordOne.size(); j++){
					if(keywordTwo.get(i).document.equals(keywordOne.get(j).document)){
						counterPartfound = true;
					}
					
				}
				if(!counterPartfound){
					merged.add(keywordTwo.get(i));
					insertLastOccurrence(merged);
				}
			}
			if(merged.size() < 5){
				
				for(int i = 0; i < merged.size(); i++){
					
					top5.add(merged.get(i).document);
				}
			}
			else{
				for(int i = 0; i < 5; i++){
					top5.add(merged.get(i).document);
				}
			}
			*/
		}
		
		return top5;
	}
}
