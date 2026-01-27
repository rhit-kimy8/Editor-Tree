package editortrees;
 
import java.util.ArrayList;

import editortrees.EditTree.BooleanContainer;
import editortrees.EditTree.RotationCounter;
 
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
 
	public String inOrderString() {
		if (this == NULL_NODE) {
			return "";
		}
		return this.left.inOrderString() + this.data + this.right.inOrderString();
	}
	
	public ArrayList<String> PreOrderRankString() {
		ArrayList<String> arr = new ArrayList<>();
		if (this == NULL_NODE) {
			return new ArrayList<String>();
		}
		String str = this.data + Integer.toString(this.rank);
		arr.add(str);
		arr.addAll(left.PreOrderRankString());
		arr.addAll(right.PreOrderRankString());
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
	
	public ArrayList<String> toDebugString() {
		ArrayList<String> arr = new ArrayList<>();
		if (this == NULL_NODE) {
			return new ArrayList<String>();
		}
		String str = this.data + Integer.toString(this.rank) + (this.balance).toString();		
		arr.add(str);
		arr.addAll(left.toDebugString());
		arr.addAll(right.toDebugString());
		return arr;
	}
	
	public Node add(char ch, int pos, RotationCounter rc) {
		if (this == NULL_NODE) return new Node(ch);
		
		if (pos <= this.rank) {
			this.rank++;
			this.left = this.left.add(ch, pos, rc);
			
			// Check Balance and Rotation
			if (this.balance == Node.Code.RIGHT) {
				this.balance = Node.Code.SAME;
			}else if (this.balance == Node.Code.SAME) {
				this.balance = Node.Code.LEFT;
			}else if (Math.abs((this.left.fastHeight() - this.right.fastHeight())) > 1){
				if (this.left.balance == Node.Code.RIGHT) {
					rc.countUp();
					rc.countUp();
					return doubleLeftRightRotation(this, this.left);
				}
				rc.countUp();
				return singleRotateRight(this, this.left);
			}
			return this;
		} else {
			int newIndex = pos-(this.rank+1);
			this.right = this.right.add(ch, newIndex, rc);
			
			if (this.balance == Node.Code.LEFT) {
				this.balance = Node.Code.SAME;
			}else if (this.balance == Node.Code.SAME) {
				this.balance = Node.Code.RIGHT;
			}else if(Math.abs((this.left.fastHeight() - this.right.fastHeight())) > 1){
				if (this.right.balance == Node.Code.LEFT) {
					rc.countUp();
					rc.countUp();
					return doubleRightLeftRotation(this, this.right);
				}
				rc.countUp();
				return singleRotateLeft(this, this.right);
			}	
		}
		this.updateBalance();
		return this;
	}
	
	public char get(int pos) {
		if (this.rank == pos) return this.data;
		if (pos < this.rank) {
			return this.left.get(pos);
		} else {
			return this.right.get(pos-(this.rank+1));
		}
	}
	
	public int fastHeight() {
		int height = -1;
		if (this == Node.NULL_NODE) return height;
		
		Node current = this;
		
		while (current != Node.NULL_NODE) {
			if (current.balance == Node.Code.LEFT) {
				current = current.left;
				height ++;
			}else if (current.balance == Node.Code.RIGHT) {
				current = current.right;
				height ++;
			}else {
				current = current.left;
				height ++;
			}
		}
		
		return height;
	}
	
	private void updateBalance() {
		int diff = this.left.fastHeight() - this.right.fastHeight();
		if (diff == 0) {
			this.balance = Node.Code.SAME;
		}else if (diff > 0) {
			this.balance = Node.Code.LEFT;
		}else {
			this.balance = Node.Code.RIGHT;
		}
	}
	
	private Node singleRotateLeft(Node parent, Node child) {
		Node temp = child.left;
		child.left = parent;
		parent.right = temp;
		parent.rank = parent.left.slowSize();
		child.rank = child.left.slowSize();
		parent.updateBalance();
		child.updateBalance();
		return child;
	}
	
	private Node singleRotateRight(Node parent, Node child) {
		Node temp = child.right;
		child.right = parent;
		parent.left = temp;
		parent.rank = parent.left.slowSize();
		child.rank = child.left.slowSize();
		parent.updateBalance();
		child.updateBalance();
		return child;
	}
	
	private Node doubleLeftRightRotation(Node parNode, Node childNode) {
			childNode = singleRotateLeft(childNode, childNode.right);
			return singleRotateRight(parNode, childNode);
		}
		 
	private Node doubleRightLeftRotation(Node parNode, Node childNode) {
			childNode = singleRotateRight(childNode, childNode.left);
			return singleRotateLeft(parNode, childNode);
		}
	
	public void balanceCodesAreCorrect(BooleanContainer isBalanced) {
		if (this == NULL_NODE) return;
		
		if (!isBalanced.isMatch) return;
		
		Node.Code balance;
		int diff = this.left.fastHeight() - this.right.fastHeight();
		if (diff == 0) {
			balance = Node.Code.SAME;
		}else if (diff > 0) {
			balance = Node.Code.LEFT;
		}else {
			balance = Node.Code.RIGHT;
		}
		if (this.balance == balance) {
			isBalanced.isMatch = true;
		}else isBalanced.isMatch = false;
		
		left.balanceCodesAreCorrect(isBalanced);
		right.balanceCodesAreCorrect(isBalanced);
		return;
	}

	
}