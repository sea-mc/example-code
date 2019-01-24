package edu.wit.cs.comp2350;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/* Sorts geographic points in place in an array
 * by surface distance to a specific point 
 * 
 * Wentworth Institute of Technology
 * COMP 2350
 * Assignment 3
 * 
 */

public class A3 {

	//TODO: document this method
	public static void quickSort(Coord[] destinations) {
		int left = 0;
		int right = destinations.length-1;
		quickSort(destinations,left,right);								//passing to second method that will take left & right as parameters
	}
	
	private static void quickSort(Coord[] destinations, int left, int right) {
		if(left < right) {
			int pivot = partition(destinations, left, right);
			quickSort(destinations, left, pivot-1);						//allow arr to be broken into subarr's & sorted from both sides of pivot.
			quickSort(destinations,pivot+1,right);
		}
	}
	
	private static int partition(Coord[] destinations, int left, int right) {
		Coord pivot = destinations[left];
		int i = left;
		
		for(int j=left+1;j<=right;j++) {						//looping through piece of arr 
			if(destinations[j].getDist() <= pivot.getDist()) {  //compare current to pivot
				i = i+1;										//if current < pivot - inc i
				Coord temp = destinations[i];
				destinations[i] = destinations[j];				//swap current with element at loc i
				destinations[j] = temp;
			}
		}
		Coord temp = destinations[i];
		destinations[i] = destinations[left];					//place pivot correctly in arr
		destinations[left] = temp;
		
		return i;
	}

	//TODO: document this method
	public static void randQuickSort(Coord[] destinations) {
		int left = 0;
		int right = destinations.length-1;
		randQuickSort(destinations,left,right);						// same idea as before w/o randoms
	}
	
	private static void randQuickSort(Coord[] destinations, int left, int right) {
		if(left<right) {
			int pivot = randPartition(destinations, left, right);
			randQuickSort(destinations, left, pivot-1);
			randQuickSort(destinations, pivot+1, right);
		}
	}
	
	private static int randPartition(Coord[] destinations, int left, int right)	{
		Random ranIndex = new Random();
		int z = ranIndex.nextInt((right-left)+1)+left;				//get our pivot as a random index no matter what subarr we are in
		   	 
		Coord temp = destinations[left];							
		destinations[left] = destinations[z];   	 				//swap the random with left for a more reliable quick-sort
		destinations[z] = temp;
		
		return partition(destinations, left, right);				//didn't notice mechanics were the same until I read the textbook
	}



	/********************************************
	 * 
	 * You shouldn't modify anything past here
	 * 
	 ********************************************/

	// Call system sort with a lambda expression on the comparator
	public static void systemSort(Coord[] destinations) {
		Arrays.sort(destinations, (a, b) -> Double.compare(a.getDist(), b.getDist()));
	}

	// Insertion sort eventually sorts an array
	public static void insertionSort(Coord[] a) {

		for (int i = 1; i < a.length; i++) {
			Coord tmpC = a[i];
			int j;
			for (j = i-1; j >= 0 && tmpC.getDist() < a[j].getDist(); j--)
				a[j+1] = a[j];
			a[j+1] = tmpC;
		}
	}

	private static Coord getOrigin(Scanner s) {
		double lat = s.nextDouble();
		double lon = s.nextDouble();

		Coord ret = new Coord(lat, lon);
		return ret;
	}

	private static Coord[] getDests(Scanner s, Coord start) {
		ArrayList<Coord> a = new ArrayList<>();

		while (s.hasNextDouble()) {
			a.add(new Coord(s.nextDouble(), s.nextDouble(), start));
		}

		Coord[] ret = new Coord[a.size()];
		a.toArray(ret);

		return ret;
	}

	private static void printCoords(Coord start, Coord[] a) {
		
		System.out.println(start.toColorString("black"));
				
		for (int i = 0; i < a.length; ++i) {
			System.out.println(a[i].toColorString("red"));
		}
		System.out.println();
		System.out.println("Paste these results into http://www.hamstermap.com/custommap.html if you want to visualize the coordinates.");
	}

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);

		System.out.printf("Enter the sorting algorithm to use [i]nsertion sort, [q]uicksort, [r]andomized quicksort, or [s]ystem quicksort): ");
		char algo = s.next().charAt(0);

		System.out.printf("Enter your starting coordinate in \"latitude longitude\" format as doubles: (e.g. 42.3366322 -71.0942150): ");
		Coord start = getOrigin(s);

		System.out.printf("Enter your end coordinates one at a time in \"latitude longitude\" format as doubles: (e.g. 38.897386 -77.037400). End your input with a non-double character:%n");
		Coord[] destinations = getDests(s, start);

		s.close();

		switch (algo) {
		case 'i':
			insertionSort(destinations);			
			break;
		case 'q':
			quickSort(destinations);
			break;
		case 'r':
			randQuickSort(destinations);
			break;
		case 's':
			systemSort(destinations);
			break;
		default:
			System.out.println("Invalid search algorithm");
			System.exit(0);
			break;
		}

		printCoords(start, destinations);

	}

}
