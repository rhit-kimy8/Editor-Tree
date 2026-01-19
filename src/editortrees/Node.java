package editortrees;
 
import java.util.ArrayList;

import editortrees.EditTree.BooleanContainer;
 
/**
* A node in a height-balanced binary tree with rank. Except for the NULL_NODE,
* one node cannot belong to two different trees.
*
* @author <<Ciel Kim & Eden Kim>>
*/
public class Node {
	DisplayableNodeWrapper displayableNodeWrapper;
	
	enum Code {
		SAME, LEFT, RIGHT;
 
		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}
 
	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to the
	// "publicly visible" effects
 
	char data;
	Node left, right; // subtrees
	int rank; // inorder position of this node within its own subtree.
	Code balance;
 
	// Feel free to add other fields that you find useful.
	// You probably want a NULL_NODE, but you can comment it out if you decide
	// otherwise.
	// The NULL_NODE uses the "null character", \0, as its data, null children,
	// rank 0, and balance Code.SAME, but they could be anything since you
	// shouldn't ever actually refer to them in your code.
	static final Node NULL_NODE = new Node('\0', null, null, 0, Code.SAME);
	
	// Node parent; You may want parent, but think twice: keeping it up-to-date
	// takes effort too, maybe more than it's worth.
 
	// Standard Node constructor that allows us to plug in all the values.
	public Node(char data, Node left, Node right, int rank, Code balance) {
		this.data = data;
		this.left = left;
		this.right = right;
		this.rank = rank;
		this.balance = balance;
		displayableNodeWrapper = new DisplayableNodeWrapper(this);
	}
 
	// Constructor for making a leaf node
	public Node(char data) {
		this(data, NULL_NODE, NULL_NODE, 0, Code.SAME);
	}
 
	// Provided to you to enable testing, please don't change.
	int slowHeight() {
		if (this == NULL_NODE) {
			return -1;
		}
		return Math.max(left.slowHeight(), right.slowHeight()) + 1;
	}
 
	// Provided to you to enable testing, please don't change.
	public int slowSize() {
		if (this == NULL_NODE) {
			return 0;
		}
		return left.slowSize() + right.slowSize() + 1;
	}
 
	public char get(int pos) {
		if (this.rank == pos) return this.data;
		if (pos < this.rank) {
			return this.left.get(pos);
		} else {
			return this.right.get(pos-(this.rank+1));
		}
	}
 
 
	public void add(char ch, int pos) {
		if (pos <= this.rank) {
			this.rank++;
			if (pos == 0 && this.left == NULL_NODE) {
				this.left = new Node(ch);
				return;
			}
			this.left.add(ch, pos);
		} else {
			int newIndex = pos-(this.rank+1);
			if (newIndex == 0 && this.right == NULL_NODE) {
				this.right = new Node(ch);
				return;
			}
			this.right.add(ch, newIndex);
		}
		
	}
	
	public String inOrderString() {
		if (this == NULL_NODE) {
			return "";
		}
		return this.left.inOrderString() + this.data + this.right.inOrderString();
	}
	
	public ArrayList<String> inOrderRankString() {
		ArrayList<String> arr = new ArrayList<>();
		if (this == NULL_NODE) {
			return new ArrayList<String>();
		}
		String str = this.data + Integer.toString(this.rank);
		arr.add(str);
		arr.addAll(left.inOrderRankString());
		arr.addAll(right.inOrderRankString());
		return arr;
	}
	
	public void ranksMatchSize(BooleanContainer bc) {
		if (this == NULL_NODE) {
			return;
		}
		if (!bc.isMatch) return;
		
		if (this.rank != this.left.slowSize()) {
			bc.isMatch = false;
			return;
		}
		
		left.ranksMatchSize(bc);
		right.ranksMatchSize(bc);
	}
	
	public boolean hasLeft() {
		return this.left != NULL_NODE;
	}

	public boolean hasRight() {
		return this.right != NULL_NODE;
	}

	public boolean hasParent() {
		return false;
	}

	public Node getParent() {
		return NULL_NODE;
	}
	// You will probably want to add more constructors and many other
	// recursive methods here. I added 47 of them - most were tiny helper methods
	// to make the rest of the code easy to understand. My longest method was
	// delete(): 20 lines of code other than } lines. Other than delete() and one of
	// its helpers, the others were less than 10 lines long. Well-named helper
	// methods are more effective than comments in writing clean code.
	// TODO: By the end of milestone 1, consider if you want to use the graphical debugger. See
	// the unit test throwing an error and the README.txt file.
}