/* UNIVERSITY OF SUFFOLK - INTRODUCTION TO PROGRAMMING 
 * Module assignment
 * 
 * Module Lead: Dr. Kakia Chatsiou
 * Last updated 2022-02-25
 * 
 * The assignment starter code consists of 3 files:
 * 
 * a) store.java: this file contains starting code for the inventory
 * management control system. See assignment brief document for 
 * more information on how to build the rest of the application.
 * 
 * b) items.txt: this file contains a list of all items in the inventory
 * with information about their quantities and total price in stock. See 
 * assignment text for more information.
 * 
 * c) transactions.txt: this file contains a list of all the transactions
 * for the day. You will be using it to print out the report of transactions
 * Each time a transaction happens i.e. an item is added or removed, 
 * a record should be stored in transactions.txt
 *  
 *
 * You are asked to work on expanding the starter code so that your Java app can do the following:
 * 
 *  - read and output to the 2 files (transactions.txt, items.txt) as appropriate
 *  - autogenerate a (5-digit) item id ie. 00001 for each new item
 *  - add a new item to the inventory (by appending a line to items.txt) 
 *  - update the quantity of an item already in store (in items.txt)
 *  - remove an item from the inventory (by removing relevant entry in items.txt)
 *  - search for an item in the inventory (items.txt)
 *  - generate and print a daily transaction report (using transactions.txt)
 * 
 * Check out the full assignment brief for more information about the report.
 */


import java.io.*;
import java.util.Scanner;

public class store
{
	public static void main(String args[])
	{

		Scanner input = new Scanner(System.in);
		
		System.out.println("I N V E N T O R Y    M A N A G E M E N T    S Y S T E M");
		System.out.println("-----------------------------------------------");
		System.out.println("1. ADD NEW ITEM");
		System.out.println("2. UPDATE QUANTITY OF EXISTING ITEM");
		System.out.println("3. REMOVE ITEM");
		System.out.println("4. VIEW DAILY TRANSACTION REPORT");
		System.out.println("---------------------------------");
		System.out.println("5. Exit");
		
		
		System.out.print("\n Enter a choice and Press ENTER to continue[1-5]:");
		int userinput = input.nextInt();
			

		while(userinput !=5)
		{
			if (userinput>5 || userinput<1) {
				System.out.println("This doesn't appear to be a valid option...!");
				break;
			}
			if (userinput == 1)	{
				System.out.print("\n New Item Added");
				break;
			}
			else if (userinput == 2) {		
				System.out.print("\n Item quantity updated");
				break;
			}
			else if (userinput == 3) {
				System.out.print("\n Item Removed");
				break;
			}	
			else if (userinput == 4) {
				System.out.print("\n Report printed");
				break;
			}	
			
		}
		
	System.out.println("\n\n Thanks for using this program...!");
	}
}