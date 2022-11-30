/*
*  File name: Pathfinder.java
*  Author  : Erin Hurlburt 
*  Date    : 2-7-2022
*/

package main.pathfinder.informed;

import java.util.*;
import java.util.Map.Entry;


/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search.
 */
public class Pathfinder {
    
    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
	
    public static ArrayList<String> solve (MazeProblem problem) {
        MazeState initialState = problem.getInitialState();
        MazeState keyState = problem.getKeyState();
        if (keyState == null) {
        	return null;
        }
        Set<MazeState> finalState = problem.getGoalStates();
        HashSet<MazeState> keys = new HashSet<MazeState>();
        keys.add(keyState);
        List<String> path1 = AStar(initialState, keys, problem);
        if(path1 == null) {
        	return null;
        }
        List<String> path2 = AStar(keyState, finalState, problem);
        ArrayList<String> finalPath = new ArrayList<String>();
        finalPath.addAll(path1);
        finalPath.addAll(path2);
        return finalPath;
        
    }
    
  
  
  public static ArrayList<String> AStar (MazeState initial, Set<MazeState> goal, MazeProblem problem) {
    	PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<SearchTreeNode>();
        frontier.add(new SearchTreeNode(initial, null, null, 0, 0, 0));
        
        HashSet<MazeState> visitedNodes = new HashSet<MazeState>();
        
        while (!frontier.isEmpty()) {
    		SearchTreeNode current = frontier.poll();
    		
    		visitedNodes.add(current.state);
    		if (goal.contains(current.state)) {
			    return getSolution(current);
			}
    		
    		Map<String, MazeState> children = problem.getTransitions(current.state);
    		//List<SearchTreeNode> childList = new ArrayList<SearchTreeNode>();
    		Set<Map.Entry<String, MazeState>> childrenSet = children.entrySet();
    		

    		
    		for (Entry<String, MazeState> child : childrenSet) {
    			if (!visitedNodes.contains(child.getValue())) {
	                int cost = problem.getCost(child.getValue());
	                //int value = getManhattanDistance(child.getValue(), goal.get(0));
	                int value = 1000000;
	                for (MazeState st : goal) {
	                  if (getManhattanDistance(child.getValue(), st) < value) {
	                    value = getManhattanDistance(child.getValue(), st);
	                  }
	                }
	
	    			SearchTreeNode childNode = new SearchTreeNode(child.getValue(), child.getKey(), current,
	                                                              value, cost, current.distance + cost);
	    			
	    			
	    		
	                
	        		frontier.add(childNode);
    			}
    		}
    		
    	}
    	
    	return null;    
  }
  
  
  private static int getManhattanDistance (MazeState currentState, MazeState goalState) {
  	int currentCol = currentState.col;
  	int currentRow = currentState.row;
  	int goalCol = goalState.col;
  	int goalRow = goalState.row;
  	int mDistance = Math.abs(currentCol - goalCol) + Math.abs(currentRow - goalRow);
  	return mDistance;
  }
  
  
  
 
  
    
    
  
    
  private static ArrayList<String> getSolution (SearchTreeNode goal) {
    	ArrayList<String> solutionReversed = new ArrayList<>();
    	SearchTreeNode current = goal;
    	
    	while (current.parent != null) {
    		solutionReversed.add(current.action);
    		current = current.parent;
    	}
    	
    	ArrayList<String> solution = new ArrayList<>();
    	
    	for (int i = 0; i < solutionReversed.size(); i++) {
    		solution.add(solutionReversed.get(solutionReversed.size() - 1 - i));
    	}
    	
    	return solution;
    }
    
}



// find the key and then store the path you took to get to the key
// iterate through set, calculate how far they are/cost and then choose the lowest one

