package main.csp;

import java.time.LocalDate;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
public class CSPSolver {

    // Backtracking CSP Solver
    // --------------------------------------------------------------------------------------------------------------
    
    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
        List<MeetingDomain> meetings = new ArrayList<>();
        for(int i = 0; i < nMeetings; i++) {
        	meetings.add(new MeetingDomain(rangeStart, rangeEnd));
        }
        nodeConsistency(meetings, constraints);
        arcConsistency(meetings, constraints);
        
        List<LocalDate> reversedList = solveHelper(meetings, 0, constraints);
        if(reversedList == null) {
        	return null;
        }
        Collections.reverse(reversedList);
        return reversedList;
    }
    
    public static List<LocalDate> solveHelper(List<MeetingDomain> meeting, int index, Set<DateConstraint> constraints) {
    	System.out.println("Meeting: " + meeting.size());
    	System.out.println("Index: " + index);
    	for(MeetingDomain m : meeting) {
    		if(m.domainValues.isEmpty()) {
    			return null;
    		}
    	}
    	if(index == meeting.size()) {
    		return new ArrayList<>(meeting.size());
    	}
    	
    	MeetingDomain mClone = meeting.get(index);
    	for(LocalDate d : mClone.domainValues) {
    		ArrayList<MeetingDomain> temp = new ArrayList<>(meeting);
    		temp.set(index, new MeetingDomain(d, d));
    		arcConsistency(temp, constraints);
    		List<LocalDate> solution = solveHelper(temp, index+1, constraints);
    		if(solution == null) {
    			continue;
    		} else {
    			solution.add(d);
    			return solution;
    		}
    	}
    	return null;
    }
    
    public static List<Arc> buildArcs(Set<DateConstraint> constraints) {
    	List<Arc> ArcList = new ArrayList<>();
    	
    	for(DateConstraint c : constraints) {
    		if(c instanceof BinaryDateConstraint) {
    			BinaryDateConstraint b = (BinaryDateConstraint)c;
    			BinaryDateConstraint b2 = new BinaryDateConstraint(b.R_VAL, b.getSymmetricalOp(), b.L_VAL);
    			ArcList.add(new Arc(b.L_VAL, b.R_VAL, b));
    			ArcList.add(new Arc(b.R_VAL, b.L_VAL, b2));
    		}
    	}
    	
    	return ArcList;
    	
    }
    
    
    // Filtering Operations
    // --------------------------------------------------------------------------------------------------------------
    
    /**
     * Enforces node consistency for all variables' domains given in varDomains based on
     * the given constraints. Meetings' domains correspond to their index in the varDomains List.
     * @param varDomains List of MeetingDomains in which index i corresponds to D_i
     * @param constraints Set of DateConstraints specifying how the domains should be constrained.
     * [!] Note, these may be either unary or binary constraints, but this method should only process
     *     the *unary* constraints! 
     */
    public static void nodeConsistency (List<MeetingDomain> varDomains, Set<DateConstraint> constraints) {
    	// the only hint here: note a DateConstraint's isSatisfiedBy method!
    	for(DateConstraint c : constraints) {
    		if(c.getClass() == UnaryDateConstraint.class) {
	        	MeetingDomain m = varDomains.get(c.L_VAL);
	        	Set<LocalDate> clone = new HashSet<LocalDate>(m.domainValues);
	        	for(LocalDate d : clone) {
	        		if(!c.isSatisfiedBy(d, ((UnaryDateConstraint)c).R_VAL)) {
	        			m.domainValues.remove(d);
	        		}
	        	}
	        }
    	}
    }
    
    /**
     * Enforces arc consistency for all variables' domains given in varDomains based on
     * the given constraints. Meetings' domains correspond to their index in the varDomains List.
     * @param varDomains List of MeetingDomains in which index i corresponds to D_i
     * @param constraints Set of DateConstraints specifying how the domains should be constrained.
     * [!] Note, these may be either unary or binary constraints, but this method should only process
     *     the *binary* constraints using the AC-3 algorithm! 
     */
    public static void arcConsistency (List<MeetingDomain> varDomains, Set<DateConstraint> constraints) {
    	List<Arc> ArcList = buildArcs(constraints);
    	Set<Arc> localVar = new HashSet<>(ArcList);
    	while(!localVar.isEmpty()) {
    		Arc nextC = localVar.iterator().next();
    		localVar.remove(nextC);
    		if(removeInconsistentVals(varDomains.get(nextC.TAIL), varDomains.get(nextC.HEAD), nextC.CONSTRAINT)) {
    			for(Arc c1 : ArcList) {
    				if(c1.HEAD == nextC.TAIL) {
    					localVar.add(c1);
    				}
    			}
    		}	
    	}
        
    }
    
    public static boolean removeInconsistentVals(MeetingDomain tail, MeetingDomain head, DateConstraint constraint) {
    	boolean changed = false;
    	Set<LocalDate> clone = new HashSet<>(tail.domainValues);
    	for(LocalDate d1 : clone) {
    		boolean keepd1 = false;
    		for(LocalDate d2 : head.domainValues) {
    			if(constraint.isSatisfiedBy(d1, d2)) {
    				keepd1 = true;
    				break;
    			}
    		}
    		if(!keepd1) {
    			tail.domainValues.remove(d1);
    			changed = true;
    		}
    	}
    	
    	return changed;
    	
    }
    
    /**
     * Private helper class organizing Arcs as defined by the AC-3 algorithm, useful for implementing the
     * arcConsistency method.
     * [!] You may modify this class however you'd like, its basis is just a suggestion that will indeed work.
     */
    private static class Arc {
        
        public final DateConstraint CONSTRAINT;
        public final int TAIL, HEAD;
        
        /**
         * Constructs a new Arc (tail -> head) where head and tail are the meeting indexes
         * corresponding with Meeting variables and their associated domains.
         * @param tail Meeting index of the tail
         * @param head Meeting index of the head
         * @param c Constraint represented by this Arc.
         * [!] WARNING: A DateConstraint's isSatisfiedBy method is parameterized as:
         * isSatisfiedBy (LocalDate leftDate, LocalDate rightDate), meaning L_VAL for the first
         * parameter and R_VAL for the second. Be careful with this when creating Arcs that reverse
         * direction. You may find the BinaryDateConstraint's getReverse method useful here.
         */
        public Arc (int tail, int head, DateConstraint c) {
            this.TAIL = tail;
            this.HEAD = head;
            this.CONSTRAINT = c;
        }
        
        @Override
        public boolean equals (Object other) {
            if (this == other) { return true; }
            if (this.getClass() != other.getClass()) { return false; }
            Arc otherArc = (Arc) other;
            return this.TAIL == otherArc.TAIL && this.HEAD == otherArc.HEAD && this.CONSTRAINT.equals(otherArc.CONSTRAINT);
        }
        
        @Override
        public int hashCode () {
            return Objects.hash(this.TAIL, this.HEAD, this.CONSTRAINT);
        }
        
        @Override
        public String toString () {
            return "(" + this.TAIL + " -> " + this.HEAD + ")";
        }
        
    }
    
}
