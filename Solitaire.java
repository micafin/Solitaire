
package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.NoSuchElementException;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		CardNode tmp=deckRear,prev=null;
		//going through entire deck
		for(int i=0;i<28;i++){
			prev=tmp;
			tmp=tmp.next;
			
			//when card equals 27
			if(tmp.cardValue==27){
				CardNode val=tmp.next;
				//changing the rear
				if(tmp==deckRear){
					deckRear=val;
				}
				else if(val==deckRear){
					deckRear=tmp;
				}
				tmp.next=tmp.next.next;
				val.next=tmp;
				prev.next=val;
				break;
			}
		}
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
		CardNode tmp=deckRear,after1=deckRear.next,after2=after1.next,prev=null;
		//going through deck
		for(int i=0;i<28;i++){
			prev=tmp;
			tmp=tmp.next;
			after1=tmp.next;
			after2=after1.next;
			
			//when card equals 28
			if(tmp.cardValue==28){
				
				//changing the rear
				if(deckRear.cardValue==28)
					deckRear=after1;
				
				else if(after1==deckRear)
					deckRear=deckRear.next;
				
				else if(after2==deckRear)
					deckRear=tmp;
					
				//swapping the references
				tmp.next=after2.next;
				after2.next=tmp;
				prev.next=after1;
				
				break;
			}
		}
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		CardNode tmp=deckRear.next,prev=deckRear,first=null,second=null;
		boolean boolfirst=true;
		
		//check if the first card is a joker and last card is joker
		if(deckRear.cardValue>26&&tmp.cardValue>26)
			return;
		
		//check if first card is joker
		else if(tmp.cardValue>26){
			for(int i=1;i<28;i++){
				prev=tmp;
				tmp=tmp.next;
				if(tmp.cardValue>26){
					deckRear=tmp;
					return;
				}
			}
		}
		
		//check if last card is joker
		else if(deckRear.cardValue>26){
			for(int i=1;i<27;i++){
				prev=tmp;
				tmp=tmp.next;
				if(tmp.cardValue>26){
					deckRear=prev;
					return;
				}
			}
		}
		
		//going through deck
		for(int i=0;i<28;i++){
			if(tmp.cardValue>=27&&boolfirst){
				first=prev;
				boolfirst=false;
			}
			else if(tmp.cardValue>=27){
				second=tmp;
				break;
			}
			
			prev=prev.next;
			tmp=tmp.next;
		}
		
		//swap everything before the joker with everything after the joker
		CardNode ptr=deckRear.next;
		deckRear.next=first.next;
		first.next=second.next;
		second.next=ptr;
		deckRear=first;
	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {		
		int count=deckRear.cardValue;
		
		CardNode tmp=deckRear.next,first=null,sec=null;
		if(count!=28&&count!=27){
			for(int i=0;i<28;i++){
				if(i==count-1)
					first=tmp;
				
				if(tmp.next==deckRear)
					sec=tmp;
				
				tmp=tmp.next;
			}

			CardNode tmp2=deckRear.next;
			deckRear.next=first.next;
			sec.next=tmp2;
			first.next=deckRear;

		}
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		int key=0;
		do{
			jokerA();
//			System.out.println("A");
//			printList(deckRear);
			jokerB();
//			System.out.println("B");
//			printList(deckRear);
			tripleCut();
//			System.out.println("T");
//			printList(deckRear);
			countCut();
//			System.out.println("C");
//			printList(deckRear);
			CardNode tmp=deckRear;
			int num=tmp.next.cardValue;
			
			//if the first number is 28, change its value to 27
			if(num==28)
				num=27;
			//go through deck
			for(int i=0;i<28;i++){
				tmp=tmp.next;
				if(i+1==num){
					key=tmp.next.cardValue;
					break;
				}
				
			}
		}while(key==27||key==28);
		return key;
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		String encrypt="";
		for(int i=0;i<message.length();i++){
			char c=Character.toUpperCase(message.charAt(i));
			if(!Character.isLetter(c))
				continue;
			
			int ks=c-'A'+1+getKey();
			if(ks>26)
				ks=ks-26;
			c=(char)(ks-1+'A');
			encrypt=encrypt+c;
		}
	    return encrypt;

	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		
		String decrypt="";
		for(int i=0;i<message.length();i++){
			char c=Character.toUpperCase(message.charAt(i));
			if(!Character.isLetter(c))
				continue;
			
			int d=c-'A'+1;
			
			int g=getKey();
			if(d<=g)
				d=d+26;
			
			int mess=d-g;
			c=(char)(mess+'A'-1);
			
			decrypt=decrypt+c;
		}
	    return decrypt;
	}
}