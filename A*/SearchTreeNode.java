/*
*  File name: SearchTreeNode.java
*  Author  : Erin Hurlburt 
*  Date    : 2-7-2022
*/

package main.pathfinder.informed;
//import java.lang.Math;

//import main.pathfinder.informed.Pathfinder.SearchTreeNode;

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 */
public class SearchTreeNode implements Comparable<SearchTreeNode> {
	
    //evaluation function: past-cost + future-cost
    // g(n) + h(n)
    // h(n) = manhattan distance
	// g(n) = .getCost() 


	public int compareTo(SearchTreeNode other) {
    	return this.value - other.value;
    }

    
	
    // [!] TODO: You're free to modify this class to your heart's content
    
    MazeState state; // Current state
    String action; // How we got here
    SearchTreeNode parent; // Which node we came from
    Integer value; // estimate to goal
    Integer cost; // cost of stepping here
    Integer distance; // distance from start
    // [!] TODO: Any other fields you want to add
    
    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state The MazeState (row, col) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    public SearchTreeNode (MazeState state, String action, SearchTreeNode parent, int value, int cost, int distance) {
        // [!] TODO: You may modify the constructor as you please
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.value = value;
        this.cost = cost;
        //this.ManhattanDistance(cost, distance);
        this.distance = distance;
    }
    
    // [!] TODO: Any methods you wish to define, private or otherwise
    
     
    
    //value (evauluation function), cost, distance (manhattan distance)
    //evaluation function = past cost + future cost
    //compare to method to add into the priority and the highest priority queue
    //method to constantly find get best goal state
    //create a method that uses his method
    
}

