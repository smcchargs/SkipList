import java.io.*;
import java.util.*;
import java.lang.*;


// Extends Comparable to use generic data types
class Node<AnyType extends Comparable<AnyType>>
{
	// Each node has some data, a height, and an array of references to other nodes
	AnyType data;
	int height;
	ArrayList<Node<AnyType>> next = new ArrayList<>();

	// Creates a node with just a height (no data)
	// Mainly used for creating the head node
	Node(int height)
	{
		this.height = height;
		for (int i = 0; i < height; i++)
			this.next.add(null);
	}

	// Create a node with a specified data and height
	Node(AnyType data, int height)
	{
		this.height = height;
		this.data = data;
		for (int i = 0; i < height; i++)
			this.next.add(null);

	}	

	// Method to get a nodes data
	public AnyType value()
	{
		return this.data;
	}	

	// Method to get a nodes height
	public int height()
	{
		return this.height;
	}

	// Method to get a node reference within it's ArrayList of references
	public Node<AnyType> next(int level)
	{
		if (level < 0 || level > height - 1)
			return null;

		return this.next.get(level);
	}

	// Method to designate a node to be referenced by this node at a given level
	public void setNext(int level, Node<AnyType> node)
	{
		this.next.set(level, node);
	}

	// Method to grow a specific node by a height of 1
	public void grow()
	{
		this.height += 1;
		this.next.add(null);
	}

	// Method to flip a coin on rather or not to increase a node's height by 1
	public void maybeGrow()
	{
		if (Math.random() > 0.5)
		{
			this.height += 1;
			this.next.add(null);	
		}

	}

	// Method to reduce a node's height to a given height
	public void trim(int height)
	{
		for (int j = height; j < height; j++)
			next.set(j, null);

		this.height = height;
	}

}

// SkipList class also extends comparable in order to create a generic type of skiplist
public class SkipList<AnyType extends Comparable<AnyType>>
{

	// Each skiplist has a head node, a height (log2(n)), and a size (number of nodes)
	private Node<AnyType> head;
	private int height;
	private int size;

	// A skiplist can be created with an initial height of 1 that will grow accordingly to the size
	SkipList()
	{
		this.height = 1;
		this.head = new Node<AnyType>(this.height);
		this.size = 0;
	}

	// A skiplist can also be given a size to start at, and can also grow in height if needed
	SkipList(int height)
	{
		if(height < 1)
			height = 1;

		this.height = height;
		this.head = new Node<AnyType>(height);
		this.size = 0;
	}

	// Method to recive the current number of nodes in the skiplist
	public int size()
	{
		return this.size;
	}

	// Method to get the current height of the skiplist
	public int height()
	{
		return this.height;
	}

	// Returns the head node of the skiplist
	public Node<AnyType> head()
	{
		return this.head;
	}

	// Inserts a node into the skiplist with a generated height based on the # of nodes in the list
	public void insert(AnyType data)
	{
		this.size++;

		// Calculates a height for the new node 
		int newHeight = generateRandomHeight(Math.max(this.height , getMaxHeight(this.size())));
		
		Node<AnyType> newNode = new Node<>(data, newHeight);

		// Used for determining if the newly generated node is going to cause an increase in height
		boolean bigNode = false;

		Node<AnyType> temp = this.head;

		// This arraylist saves what nodes were used to traverse the skiplist to find the insertion point
		ArrayList<Node<AnyType>> update = new ArrayList<>();
		for (int k = 0; k < newHeight; k++)
			update.add(null);

		// If the height of the new node is greater than the height of the list, increase the height of the list
		if (newHeight > head.height)
		{
			head.height = newHeight;
			bigNode = true;
			growSkipList();

		}

		// Start at the top and run through the list until you find insertion point
		// runs in O(log(n)) time 
		for (int i = (head.height - 1); i >= 0; i--)
		{
			while((temp.next(i) != null) && temp.next(i).data.compareTo(data) < 0)
				temp = temp.next(i);

			// We don't want to change the reference of a node if it goes over the insertion point
			// therefore if i is at a level that new node does not reach, theres no need to change the pointers at that level
			if (i < newHeight)
			{
				update.set(i, temp);
			}

			// If we have a node that goes above the current level of list we need to adjust pointers above the current level
			if (bigNode)
			{
				update.set(i, temp);
			}
		}

		// Now actaully go through update and rereference all nodes that were effected by the insertion
		for (int j = 0; j < newHeight; j++)
		{
			newNode.setNext(j, update.get(j).next(j));

			update.get(j).setNext(j, newNode);

		}
		// After inserting the size of the skiplist is increased this effects calculation for the 
		// list height and head heght, so those get recalculated with the new size
		this.height = Math.max(this.height, getMaxHeight(this.size));
		for (int a = 0; a < (this.height - head.height); a++)
			head.next.add(null);		


		head.height = this.height;
	}

	// Inserts a node with a given height, slightly different then a generated height
	public void insert(AnyType data, int height)
	{

		Node<AnyType> newNode = new Node<>(data, height);
		for (int h = 0; h < height; h++)
			newNode.next.add(null);
	
		// Used for determining if the newly generated node is going to cause an increase in height
		boolean bigNode = false;

		Node<AnyType> temp = this.head;

		// This arraylist saves what nodes were used to traverse the skiplist to find the insertion point
		ArrayList<Node<AnyType>> update = new ArrayList<>(height);
		for (int k = 0; k < height; k++)
			update.add(null);

		// If the height of the new node is greater than the height of the list, increase the height of the list
		if (height > head.height)
		{
			head.height = height;
			bigNode = true;
			growSkipList();

		}
		
		// Start at the top and run through the list until you find insertion point
		// runs in O(log(n)) time
		for (int i = (head.height - 1); i >= 0; i--)
		{
			while((temp.next(i) != null) && temp.next(i).data.compareTo(data) < 0)
				temp = temp.next(i);

			// We don't want to change the reference of a node if it goes over the insertion point
			// therefore if i is at a level that new node does not reach, theres no need to change the pointers at that level
			if (i < height)
			{
				update.set(i, temp);
			}

			// If we have a node that goes above the current level of list we need to adjust pointers above the current level
			if (bigNode)
			{
				update.set(i, temp);
			}
		}

		// Now actaully go through update and rereference all nodes that were effected by the insertion
		for (int j = 0; j < height; j++)
		{
			newNode.setNext(j, update.get(j).next(j));

			update.get(j).setNext(j, newNode);
		}

		// After inserting the size of the skiplist is increased this effects calculation for the 
		// list height and head heght, so those get recalculated with the new size
		this.size++;
		this.height = Math.max(this.height, getMaxHeight(this.size));
		for (int a = 0; a < (this.height - head.height); a++)
			head.next.add(null);		


		head.height = this.height;

	}

	// Deletes a node from the skiplist (the leftmost if theres duplicates)
	public void delete(AnyType data)
	{
		
		Node<AnyType> temp = this.head;

		Node<AnyType> node2Del = null;

		// Used to determine if the node is actually in the list
		boolean hasTheNodeBeenFound = false;
		
		// ArrayList for rereferencing nodes effected by the deletion
		ArrayList<Node<AnyType>> update = new ArrayList<>(this.height);
		for (int k = 0; k < this.height; k++)
			update.add(null);

		int j = (head.height - 1);
		if (head.next(j) == null)
			while(head.next(j) == null)
				j -= 1;

		// Run through the list and stopping at nodes who point to the node we want to delete
	 	while (j >= 0)
	 	{	
	 		// Runs through nodes until we hit null of something greater than node we want to delete
	  		while (temp.next(j) != null && temp.next(j).data.compareTo(data) <= 0)
	  		{
	  			// Once we find a node we want to delete we know it is in the list
	  			// We can also add the node that point to it into the update array
	  			if (temp.next(j).data.compareTo(data) == 0)
	  			{
	  				// Only at the bottom level can we determine which node we want to delete (in the case of duplicates with different heights) 
	  				if (j == 0)
	  				{
	  					node2Del = temp.next(j);
	  				}

	  				hasTheNodeBeenFound = true;
	  				update.set(j, temp);

	  			  	break;
	  			}

	  			temp = temp.next(j);
	  		}

	  		j--;
	  	}

	  	// If the node was not found in the list than stop the function and do nothing
	  	if (!hasTheNodeBeenFound)
	  	{
	  		return;
	  	}

	  	// Once the node we want to delete was found we need to... 
	  	else
	  	{
	  		// rereference all nodes that pointed to the deleted node (basically now point through the deleted node)
	  		for (int l = 0; l < node2Del.height(); l++)
	  		{
	  			update.get(l).setNext(l, node2Del.next(l));
	  		}

	  		int sizeBeforeDeletion = Math.max(getMaxHeight(this.size), this.height());
	  		this.size--;

	  		// If the new maximum height of the skiplist is changed due to the deletion we need to trim the entire list
	  		if (getMaxHeight(this.size) < sizeBeforeDeletion)
	  		{
	  			trimSkipList();
	  		}

	  		// heights are recalculated based on the new size of the skiplist
	  		this.height = getMaxHeight(this.size);
			for (int a = 0; a < (this.height - head.height); a++)
				head.next.add(null);		


			head.height = this.height;

	  	}
	}

	// Method that runs through the list and attempt to find a node with a given data
	public boolean contains(AnyType data)
	{
		Node<AnyType> temp = this.head;

		int j = (this.height - 1);

	 	while (j >= 0)
	 	{	

	 		// Run through the list
	  		while (temp.next(j) != null && temp.next(j).data.compareTo(data) <= 0)
	  		{
	  			// if the data is found return true
	  			if (temp.next(j).data.compareTo(data) == 0)
	  			{
	  				return true;
	  			}
	  			temp = temp.next(j);
	  		}

	  		j--;
	  	}

	  	// if the data was never found we return false
	  	return false;
	}

	// Similar to the contains method but instead returns the actual node's reference with the given data to find
	public Node<AnyType> get(AnyType data)
	{

		Node<AnyType> temp = this.head;

		int j = (this.height - 1);

	 	while (j >= 0)
	 	{	
	  		while (temp.next(j) != null && temp.next(j).data.compareTo(data) <= 0)
	  		{
	  			// If the node with that data is found we returns the node's reference it was found at
	  			if (temp.next(j).data.compareTo(data) == 0)
	  			{
	  				return temp.next(j);
	  			}
	  			temp = temp.next(j);
	  		}

	  		j--;
	  	}

	  	// if the data was never found we return null
	  	return null;

	}

	private static int getMaxHeight(int n)
	{
		// returns the max height of a skip list with n nodes
		if (n < 2)
			n = 2;

		// Change base formula
		double val = Math.log(n)/Math.log(2);

		val = Math.ceil(val);

		// casting the value to an int, causes it to round up
		int maxHeight = (int)(val);

		return maxHeight;
	}

	private static int generateRandomHeight(int maxHeight)
	{
		// simulates flipping a coin and result keeps track of the amount of succesful flips
		int result = 1;
		while (true)
		{
			if (Math.random() > 0.5)
			{
				result++;

				if(result > maxHeight)
					return maxHeight;
			}
			else
				break;
		}
		return result;

	}

	// Runs through each node that has a maxed height and increases their height by 1 50% of the time
	private void growSkipList()
	{

		// updates the pointers of each node
		ArrayList<Node<AnyType>> update = new ArrayList<>(head.height);

		// index to traverse the old top level of the list
		int i = (head.height - 2);
		head.next.add(null);

		Node<AnyType> temp = head;

		// Run through the old top level and 50% chance of increasing the height by 1
		while (temp.next(i) != null)
		{
			if (Math.random() < 0.5)
			{
				temp.next(i).height += 1;
				temp.next(i).next.add(null);
				update.add(temp.next(i));
			}

			temp = temp.next(i);
		}

		temp = head;

		// update references to all nodes
		for (int j = 0; j < update.size(); j++)
		{
			temp.setNext((head.height - 1), update.get(j));
			temp = temp.next(head.height - 1);
		}

	}

	private void trimSkipList()
	{
		// Every node that exceeds the new max height is brought down to the max height
		int i = getMaxHeight(this.size) - 1;
		Node<AnyType> temp = this.head;

		while (temp.next(i) != null)
		{
			temp.next(i).height = i + 1;
	
			temp = temp.next(i);
		}

	}




	public static double difficultyRating()
	{

	 	double rating =  5.0;

	 	return rating;

	}


	public static double hoursSpent()
	{

	  	double timeISpentConfused = 64.0;

	 	return timeISpentConfused;
	}

	// Prints a given level of the skip list
	public void printLevel(int level)
	{
		Node<AnyType> temp = this.head;

		while (temp.next(level) != null)
		{
			System.out.print(temp.next(level).data + " ");
			temp = temp.next(level);
		}
		System.out.println("\n\n\n");
	}

	// Prints the entire skip list at each level
	public void printList()
	{

		Node<AnyType> temp = this.head;

		int j = (temp.height - 1);

		System.out.println("The size of the list is: " + this.size());

		System.out.println("The height of the head is: " + head.height);
		System.out.println("The height of this skiplist is: " + this.height + "\n\n");

	 	while (j >= 0)
	 	{	
	  		while (temp.next(j) != null && j < temp.next.size())
	  		{
	  			System.out.print(temp.next(j).data + " " + "(" + temp.next(j).height() + ") ");

	  			temp = temp.next(j);
	  		}
	  		temp = this.head;
	  		System.out.println("");
	  		j--;
	  	}

	}
}
