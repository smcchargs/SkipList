 // This is a basic template showing how to set up your Node and SkipList classes
// to be in the same file. For my test cases to work properly, you will need to
// implement your Node class here, in this file, outside of the SkipList class.
//
// Note: You will need to modify these class declarations to make your Node
// and SkipList containers generic and capable of holding any type of Comparable
// object.
//
// You should rename this file "SkipList.java" and remove this comment before
// submitting.
// Dakota Riediger Downing
// PID: 4319691 NID: da337992
// COP 3503, Spring 2020

import java.io.*;
import java.util.*;
import java.lang.Math;

class Node<AnyType>
{
  ArrayList<Node<AnyType>> nextReference;
  AnyType data;
  int height;
  Node(AnyType data, int height)
  {
    int i;
    nextReference = new ArrayList<Node<AnyType>>();
    this.data = data;
    this.height = height;
    if (height == 0)
    {
      nextReference.add(null);
    }
    else
    {
      for (i = 0; i < height; i++)
      {
        nextReference.add(null);
      }
    }
  }
  Node(int height)
  {
    int i;
    nextReference = new ArrayList<Node<AnyType>>();
    this.height = height;
    if (height == 0)
    {
      nextReference.add(null);
    }
    else
    {
      for (i = 0; i < height; i++)
      {
        nextReference.add(null);
      }
    }
  }
  public AnyType value()
  {
    return this.data;
  }
  public int height()
  {
    return this.height;
  }
  public Node<AnyType> next(int level)
  {
    if (level < 0 || level > (this.height()))
    {
      return null;
    }
    return nextReference.get(level);
  }
  public void setNext(int level, Node<AnyType> node)
  {
    this.nextReference.set(level, node);
  }
  public void grow()
  {
    nextReference.add(null);
    this.height++;
  }
  public void maybeGrow()
  {
    Random probability = new Random();
    if (probability.nextBoolean())
    {
      this.nextReference.add(null);
      this.height++;
    }
  }
  public void trim(int height)
  {
    int i;
    for (i = this.nextReference.size() - 1; i >= 0 ; i++)
    {
      if (i == height)
      {
        break;
      }
      this.nextReference.remove(i);
    }
  }
}

public class SkipList<AnyType extends Comparable<AnyType>>
{
  Node<AnyType> head;
  int counter = 0;
  // int height = 0;
  SkipList()
  {
    head = new Node<AnyType>(0);
  }
  SkipList(int height)
  {
    head = new Node<AnyType>(height);
  }
  public int size()
  {
    return counter;
  }
  public int height()
  {
    return head.height();
  }
  public Node<AnyType> head()
  {
    return head;
  }
  public void insert(AnyType data)
  {
    Node<AnyType> current = head;
    Node<AnyType> nextNode;
    int i = 0;
    counter++;
    int traversalHeight = generateRandomHeight(getMaxHeight(counter) - 1);
    // System.out.println("TraversalHeight: " + (generateRandomHeight(0)));
    Node<AnyType> insertNode = new Node<AnyType>(data, traversalHeight + 1);
    if (head.height() < getMaxHeight(counter))
    {
      growSkipList();
    }
    while (i < counter)
    {
      if (current.next(traversalHeight) == null)
      {
        while (traversalHeight >= 0)
        {
          if(current.next(traversalHeight) != null)
          {
            nextNode = current.next(traversalHeight);
            current.setNext(traversalHeight, insertNode);
            current.next(traversalHeight).setNext(traversalHeight, nextNode);
            traversalHeight--;
          }
          else
          {
            current.setNext(traversalHeight, insertNode);
            traversalHeight--;
          }
        }
        break;
      }
      else if (data.compareTo(current.next(traversalHeight).data) <= 0)
      {
        while (traversalHeight >= 0)
        {
          if(current.next(traversalHeight) != null)
          {
            nextNode = current.next(traversalHeight);
            current.setNext(traversalHeight, insertNode);
            current.next(traversalHeight).setNext(traversalHeight, nextNode);
            traversalHeight--;
          }
          else
          {
            current.setNext(traversalHeight, insertNode);
            traversalHeight--;
          }
        }
        break;
      }
      else
      {
          current = current.next(traversalHeight);
      }
      i++;
    }
    // System.out.println("insertedNode Height: " + insertNode.height() + " Head Height: " + head.height());
  }
  public void insert(AnyType data, int height)
  {
    Node<AnyType> current;
    Node<AnyType> nextNode;
    counter++;
    Node<AnyType> insertNode = new Node<AnyType>(data, height);
    current = head;
    int i = 0;
    int traversalHeight = height - 1;
    if (head.height() < getMaxHeight(counter))
    {
      growSkipList();
    }
    while (i < this.counter)
    {
      if (current.next(traversalHeight) == null)
      {
        while (traversalHeight >= 0)
        {
          if(current.next(traversalHeight) != null)
          {
            nextNode = current.next(traversalHeight);
            current.setNext(traversalHeight, insertNode);
            current.next(traversalHeight).setNext(traversalHeight, nextNode);
            traversalHeight--;
          }
          else
          {
            current.setNext(traversalHeight, insertNode);
            traversalHeight--;
          }
        }
        break;
      }
      else if (data.compareTo(current.next(traversalHeight).value()) <= 0)
      {
        while (traversalHeight >= 0)
        {
          if(current.next(traversalHeight) != null)
          {
            nextNode = current.next(traversalHeight);
            current.setNext(traversalHeight, insertNode);
            current.next(traversalHeight).setNext(traversalHeight, nextNode);
            traversalHeight--;
          }
          else
          {
            current.setNext(traversalHeight, insertNode);
            traversalHeight--;
          }
        }
        break;
      }
      else
      {
          current = current.next(traversalHeight);
      }
      i++;
    }
  }
  // TO DO: fix this shit
  public boolean contains(AnyType data)
  {
    Node<AnyType> current = head;
    int traversalHeight = head.height() - 1;
    int i;
    for (i = traversalHeight - 1; i > 0; i++)
    {
      if (current.next(traversalHeight).value().compareTo(data) <= 0)
      {
        current = current.next(traversalHeight);
      }
      else if (current.next(traversalHeight).value().compareTo(data) > 0)
      {
        traversalHeight--;
        i = traversalHeight - 1;
      }
      else if (current.next(traversalHeight).value().compareTo(data) > 0 && current.value().compareTo(data) == 0)
      {
        return true;
      }
    }
    return false;
  }
  private static int getMaxHeight(int n)
  {
    if (n == 0)
    {
      return 1;
    }
    if (n == 1)
    {
      return 1;
    }
    return (int)(Math.ceil(Math.log(n) / Math.log(2)));
  }
  private static int generateRandomHeight(int maxHeight)
  {
    int newHeight = 0;
    Random height = new Random();
    while ((height.nextBoolean()) && (newHeight < maxHeight))
    {
      newHeight++;
    }
    return newHeight;
  }
  private void growSkipList()
  {
    Node<AnyType> currentNode = head;
    Node<AnyType> previousNode = head;
    head.grow();
    int level = head.height() - 1;
    int i;
    while(currentNode.next(level) != null)
    {
      currentNode = currentNode.next(level);
      currentNode.maybeGrow();
      if (currentNode.height() == level + 1)
      {
        previousNode.setNext(currentNode.height(), currentNode);
        previousNode = currentNode;
      }
    }
  }
  // Returns the difficultyRating that I inputed
  public static double difficultyRating()
  {
    return 4.5;
  }
  // Returns the amount of hours spent that I inputed
  public static double hoursSpent()
  {
    return 20.0;
  }

  public void printList()
	{

		Node<AnyType> temp = this.head;

		int j = (temp.height - 1);

		System.out.println("The size of the list is: " + this.size());

		System.out.println("The height of the head is: " + head.height);
		// System.out.println("The height of this skiplist is: " + this.height + "\n\n");

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

	public static void main (String args[])
	{
		SkipList<Integer> s = new SkipList<Integer>();

		s.insert(10);
		s.insert(20);
		s.insert(3);
		s.insert(15);
		s.insert(5);

		s.printList();
	}
}
