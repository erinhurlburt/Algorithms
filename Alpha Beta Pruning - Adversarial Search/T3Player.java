/**
 *  File name: T3Player.java
 *  Author: Erin Hurlburt
 *  Date: 02/21/22
 */
package main.t3;

import java.util.*;

/**
 * Artificial Intelligence responsible for playing the game of T3!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */

class Pair {
	public int score;
	public T3Action action;
	public Pair(int s, T3Action a) {
		score = s;
		action = a;
	}
};

	
public class T3Player {
    
    /**
     * Workhorse of an AI T3Player's choice mechanics that, given a game state,
     * makes the optimal choice from that state as defined by the mechanics of
     * the game of Tic-Tac-Total.
     * Note: In the event that multiple moves have equivalently maximal minimax
     * scores, ties are broken by move col, then row, then move number in ascending
     * order (see spec and unit tests for more info). The agent will also always
     * take an immediately winning move over a delayed one (e.g., 2 moves in the future).
     * @param state The state from which the T3Player is making a move decision.
     * @return The T3Player's optimal action.
     */
    public T3Action choose (T3State state) {
    	Map<T3Action, T3State> moves = state.getTransitions();
		for (T3Action childAction : moves.keySet()) {
			if(moves.get(childAction).isWin()) {
				return childAction;
			}
		}

        return alphaBeta(state, Integer.MIN_VALUE, Integer.MAX_VALUE, true).action;
    }
    
    /**
     *  Returns the action and score for the player to win, finds the  most optimal
     *  action and uses alpha-beta pruning to efficiently search for the best move.
     *  Returns a pair that consists of an integer representing the score of the move
     *  and a T3Action which is the column, row, and move value.
     *  Takes in four parameters of a T3State, integer to represent alpha, integer
     *  to represent beta, and a boolean telling which player's turn it is.
     *  @return The pair of action and score that is most optimal for the player
     */
    private Pair alphaBeta(T3State node, int alpha, int beta, boolean oddTurn) {
    	//base cases
    	if(node.isWin() && !oddTurn) {
    		return new Pair(1, null);
    	}
    	
    	if(node.isWin() && oddTurn) {
    		return new Pair(-1, null);
    	}
    	
    	if(node.isTie()) {
    		return new Pair(0, null);
    	}

    	
    	if(oddTurn) {
    		//T3Action move = null;
    		int v = Integer.MIN_VALUE;
    		int finalV = Integer.MIN_VALUE;
    		Pair optimalMove = new Pair(0, null);
    		
    		Map<T3Action, T3State> moves = node.getTransitions();
    		for (T3Action childAction : moves.keySet()) {
    		//for(Map.Entry<T3Action, T3State> entry : node.getTransitions().entrySet()) {
    			v = Math.max(v, alphaBeta(moves.get(childAction), alpha, beta, !oddTurn).score);
    			//v = Math.max(v, alphaBeta(entry.getValue(), alpha, beta, false).score);
    			alpha = Math.max(alpha, v);
    			
    			if (beta <= alpha) {
    				break;
    			}
    		
    			if (v > finalV) {
    				//v = p.score;
    				//move = childAction;
    				finalV = v;
    				optimalMove.action = childAction;
    				//optimalMove.action = entry.getKey();
    				optimalMove.score = v;
    			}

    			
    		}
    		
    		//Pair optimalMove = new Pair(v, move);
    		return optimalMove;
          
    	} else {
    		//T3Action move = null;
    		int v = Integer.MAX_VALUE;
    		int finalV = Integer.MAX_VALUE;
    		Pair optimalMove = new Pair(0, null);
    		
    		Map<T3Action, T3State> moves = node.getTransitions();
    		for (T3Action childAction : moves.keySet()) {
    		//for(Map.Entry<T3Action, T3State> entry : node.getTransitions().entrySet()) {
    			v = Math.min(v, alphaBeta(moves.get(childAction), alpha, beta, !oddTurn).score);
    			//v = Math.min(v, alphaBeta(entry.getValue(), alpha, beta, true).score);
    			beta = Math.min(beta, v);

    			if (beta <= alpha) {
    				break;
    			}
    			
    			if (v > finalV) {
    				//v = p.score;
    				//move = p.action;
    				finalV = v;
    				optimalMove.action = childAction;
    				//optimalMove.action = entry.getKey();
    				optimalMove.score = v;
    			}
    			
    		
    		}

    		
    		//Pair optimalMove = new Pair(v, childAction);
    		return optimalMove;
    	}
    	
    }
    
}

