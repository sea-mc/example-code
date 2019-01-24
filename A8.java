package edu.wit.cs.comp2350;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/* Provides a solution to the 0-1 knapsack problem 
 * 
 * Wentworth Institute of Technology
 * COMP 2350
 * Assignment 8
 * 
 */

public class A8 {
	
	public static Item[] FindDynamic(Item[] table, int capacity) {
		int W = capacity;				
		int n = table.length;							//table dimensions are nxW, as was used in class.
		
		ArrayList<Item> items = new ArrayList<Item>(); 	//using something more easy to construct dynamically
		int Knapsack[][] = new int[n+1][W+1]; 			//table representing my Knapsack, constructed from top left to bottom right
		int counter = 0;
		
		for(int item = 0; item <= n; item++) { 	
			for(int weight = 0; weight <= W; weight++) {
				if(item == 0 || weight == 0) {
					Knapsack[item][weight] = 0;			//set edges to 0 like in LCS
				}
				else if(table[item-1].weight <= weight) {
					Knapsack[item][weight] = Math.max(table[item-1].price + Knapsack[item-1][weight-table[item-1].weight], Knapsack[item-1][weight]);
				}
				else {
					Knapsack[item][weight] = Knapsack[item-1][weight];
				}
			}
		}
		int w = capacity;								//traverse back through table from bottom right finding which items we add to items[]
		int r = Knapsack.length-1;
		int maxVal = Knapsack[r][w];
		while(r > 0 && w > 0) {
			if(maxVal == Knapsack[r-1][w]) {
				r--;
			}
			else {
				items.add(table[r-1]);
				maxVal -= table[r-1].price;
				w -= table[r-1].weight;
			}
		}
		Item arr[] = new Item[items.size()];			//move ArrayList items into Item array for returning
		for(int k = 0; k < items.size(); k++) {
			arr[k] = items.get(k);
		}
		best_price = Knapsack[n][W];					//best price will be held in the last (bottom-right) cell.
		return arr;										//return array of items used to get best price.
	}
	

	/********************************************
	 * 
	 * You shouldn't modify anything past here
	 * 
	 ********************************************/

	// set by calls to Find* methods
	private static int best_price = -1;
	
	public static class Item {
		public int weight;
		public int price;
		public int index;

		public Item(int w, int p, int i) {
			weight = w;
			price = p;
			index = i;
		}

		public String toString() {
			return "(" + weight + "#, $" + price + ")"; 
		}
	}

	// enumerates all subsets of items to find maximum price that fits in knapsack
	public static Item[] FindEnumerate(Item[] table, int capacity) {

		if (table.length > 31) {	// bitshift fails for larger sizes
			System.err.println("Problem size too large. Exiting");
			System.exit(0);
		}
		
		int nCr = 1 << table.length; // bitmask for included items
		int bestSum = -1;
		boolean[] bestUsed = {}; 
		boolean[] used = new boolean[table.length];
		
		for (int i = 0; i < nCr; i++) {	// test all combinations
			int temp = i;

			for (int j = 0; j < table.length; j++) {
				used[j] = (temp % 2 == 1);
				temp = temp >> 1;
			}

			if (TotalWeight(table, used) <= capacity) {
				if (TotalPrice(table, used) > bestSum) {
					bestUsed = Arrays.copyOf(used, used.length);
					bestSum = TotalPrice(table, used);
				}
			}
		}

		int itemCount = 0;	// count number of items in best result
		for (int i = 0; i < bestUsed.length; i++)
			if (bestUsed[i])
				itemCount++;

		Item[] ret = new Item[itemCount];
		int retIndex = 0;

		for (int i = 0; i < bestUsed.length; i++) {	// construct item list
			if (bestUsed[i]) {
				ret[retIndex] = table[i];
				retIndex++;
			}
		}
		best_price = bestSum;
		return ret;

	}

	// returns total price of all items that are marked true in used array
	private static int TotalPrice(Item[] table, boolean[] used) {
		int ret = 0;
		for (int i = 0; i < table.length; i++)
			if (used[i])
				ret += table[i].price;

		return ret;
	}

	// returns total weight of all items that are marked true in used array
	private static int TotalWeight(Item[] table, boolean[] used) {
		int ret = 0;
		for (int i = 0; i < table.length; i++) {
			if (used[i])
				ret += table[i].weight;
		}

		return ret;
	}

	// adds items to the knapsack by picking the next item with the highest
	// price:weight ratio. This could use a max-heap of ratios to run faster, but
	// it runs in n^2 time wrt items because it has to scan every item each time
	// an item is added
	public static Item[] FindGreedy(Item[] table, int capacity) {
		boolean[] used = new boolean[table.length];
		int itemCount = 0;

		while (capacity > 0) {	// while the knapsack has space
			int bestIndex = GetGreedyBest(table, used, capacity);
			if (bestIndex < 0)
				break;
			capacity -= table[bestIndex].weight;
			best_price += table[bestIndex].price;
			used[bestIndex] = true;
			itemCount++;
		}

		Item[] ret = new Item[itemCount];
		int retIndex = 0;

		for (int i = 0; i < used.length; i++) { // construct item list
			if (used[i]) {
				ret[retIndex] = table[i];
				retIndex++;
			}
		}

		return ret;
	}
	
	// finds the available item with the best price:weight ratio that fits in
	// the knapsack
	private static int GetGreedyBest(Item[] table, boolean[] used, int capacity) {

		double bestVal = -1;
		int bestIndex = -1;
		for (int i = 0; i < table.length; i++) {
			double ratio = (table[i].price*1.0)/table[i].weight;
			if (!used[i] && (ratio > bestVal) && (capacity >= table[i].weight)) {
				bestVal = ratio;
				bestIndex = i;
			}
		}

		return bestIndex;
	}

	public static int getBest() {
		return best_price;
	}
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		String file1;
		int capacity = 0;
		System.out.printf("Enter <objects file> <knapsack capacity> <algorithm>, ([d]ynamic programming, [e]numerate, [g]reedy).\n");
		System.out.printf("(e.g: objects/small 10 g)\n");
		file1 = s.next();
		capacity = s.nextInt();

		ArrayList<Item> tableList = new ArrayList<Item>();

		try (Scanner f = new Scanner(new File(file1))) {
			int i = 0;
			while(f.hasNextInt())
				tableList.add(new Item(f.nextInt(), f.nextInt(), i++));
		} catch (IOException e) {
			System.err.println("Cannot open file " + file1 + ". Exiting.");
			System.exit(0);
		}

		Item[] table = new Item[tableList.size()];
		for (int i = 0; i < tableList.size(); i++)
			table[i] = tableList.get(i);

		String algo = s.next();
		Item[] result = {};

		switch (algo.charAt(0)) {
		case 'd':
			result = FindDynamic(table, capacity);
			break;
		case 'e':
			result = FindEnumerate(table, capacity);
			break;
		case 'g':
			result = FindGreedy(table, capacity);
			break;
		default:
			System.out.println("Invalid algorithm");
			System.exit(0);
			break;
		}

		s.close();

		System.out.printf("Index of included items: ");
		for (int i = 0; i < result.length; i++)
			System.out.printf("%d ", result[i].index);
		System.out.printf("\nBest total price: %d\n", best_price);	
	}

}
