package editortrees;
 
import java.util.ArrayList;
 
import editortrees.EditTree.dataContainer;
 
/**
* A node in a height-balanced binary tree with rank. Except for the NULL_NODE,
* one node cannot belong to two different trees.
*
* @author <<Ciel Kim & Eden Kim>>
*/
public class Node {
//	DisplayableNodeWrapper displayableNodeWrapper;
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
	int height = 0;
	int size = 1;
//	public DisplayableNodeWrapper displayableNodeWrapper;
	public DisplayableNodeWrapper displayableNodeWrapper;
 
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
 
		if (data == '\0') {
			this.height = -1;
			this.size = 0;
		}
//		displayableNodeWrapper = new DisplayableNodeWrapper(this);
	}
 
	// Constructor for making a leaf node
	public Node(char data) {
		this(data, NULL_NODE, NULL_NODE, 0, Code.SAME);
		if (data == '\0') {
			this.height = -1;
			this.size = 0;
		}
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
		if (this.rank == pos)
			return this.data;
		if (pos < this.rank) {
			return this.left.get(pos);
		} else {
			return this.right.get(pos - (this.rank + 1));
		}
	}
	
	public void getStr(int pos, int length ,StringBuilder sb) {
		if (this == NULL_NODE) return;
		if (pos < this.rank) {
			this.left.getStr(pos, length, sb);
		}
		if (this.rank >= pos && this.rank < pos + length) {
			sb.append(this.data);
		}
		if ((this.rank+1) < (pos+length)) {
			this.right.getStr((pos - (this.rank + 1)), length, sb);
		}
		return;
	}
 
	public Node add(char ch, int pos, dataContainer dc) {
		if (this == NULL_NODE)
			return new Node(ch);
 
		if (pos <= this.rank) {
			this.rank++;
			this.left = this.left.add(ch, pos, dc);
		} else {
			int newIndex = pos - (this.rank + 1);
			this.right = this.right.add(ch, newIndex, dc);
		}
		return updateBalancing(dc);
	}
 
	private Node successor() {
		if (this == NULL_NODE)
			return NULL_NODE;
		if (this.left == NULL_NODE) {
			return this;
		}
		return this.left.successor();
	}
 
	public Node delete(int pos, dataContainer dc) {
		if (this == NULL_NODE) throw new IndexOutOfBoundsException("nice try");
//		2. store its data in cc & check how to deal with its children
//		3. how many rotations do we need to maintain balance of the tree
		if (pos == this.rank) {
			dc.data = this.data;
			if (this.left != NULL_NODE && this.right != NULL_NODE) {
				this.data = this.right.successor().data;
				this.right = this.right.delete(0, dc);
			} else if (this.left != NULL_NODE) {
				return this.left;
			} else if (this.right != NULL_NODE) {
				return this.right;
			} else {
				return NULL_NODE;
			}
		} else if (pos < this.rank) {
			this.left = this.left.delete(pos, dc);
		} else {
			int newIndex = pos - (this.rank + 1);
			this.right = this.right.delete(newIndex, dc);
		}
		return updateBalancing(dc);
	}
 
	private Node updateBalancing(dataContainer dc) {
		Code prevBalance = this.balance;
		this.update();
		int diff = left.height - right.height;
		if (diff > 1&& this.balance == prevBalance) {
			if (this.left.balance == Code.RIGHT) {
				dc.countUp();
				dc.countUp();
				return doubleLeftRightRotation(this, left);
			} else {
				dc.countUp();
				return singleRotateRight(this, left);
			}
		} else if (diff < -1 && this.balance == prevBalance) {
 
			if (this.right.balance == Code.LEFT) {
				dc.countUp();
				dc.countUp();
				return doubleRightLeftRotation(this, right);
			} else {
				dc.countUp();
				return singleRotateLeft(this, right);
			}
		}
		return this;
	}
 
	private void update() {
		this.rank = left.size;
		this.height = Math.max(left.height, right.height) + 1;
		this.size = left.size + right.size + 1;
 
		int diff = left.height - right.height;
		if (diff == 0) {
			this.balance = Node.Code.SAME;
		} else if (diff > 0) {
			this.balance = Node.Code.LEFT;
		} else {
			this.balance = Node.Code.RIGHT;
		}
	}
 
	private Node singleRotateLeft(Node parent, Node child) {
		Node temp = child.left;
		child.left = parent;
		parent.right = temp;
 
		parent.update();
		child.update();
		return child;
	}
 
	private Node singleRotateRight(Node parent, Node child) {
		Node temp = child.right;
		child.right = parent;
		parent.left = temp;
 
		parent.update();
		child.update();
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
 
	public void ranksMatchSize(dataContainer bc) {
		if (this == NULL_NODE) {
			return;
		}
		if (!bc.isMatch)
			return;
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
	// You will probably want to add more constructors and many other
	// recursive methods here. I added 47 of them - most were tiny helper methods
	// to make the rest of the code easy to understand. My longest method was
	// delete(): 20 lines of code other than } lines. Other than delete() and one of
	// its helpers, the others were less than 10 lines long. Well-named helper
	// methods are more effective than comments in writing clean code.
	// TODO: By the end of milestone 1, consider if you want to use the graphical
	// debugger. See
	// the unit test throwing an error and the README.txt file.
 
	public void balanceCodesAreCorrect(dataContainer isBalanced) {
		if (this == NULL_NODE)
			return;
		if (!isBalanced.isMatch)
			return;
		Node.Code balance;
		int diff = this.left.height - this.right.height;
		if (diff == 0) {
			balance = Node.Code.SAME;
		} else if (diff > 0) {
			balance = Node.Code.LEFT;
		} else {
			balance = Node.Code.RIGHT;
		}
		if (this.balance == balance) {
			isBalanced.isMatch = true;
		} else
			isBalanced.isMatch = false;
		left.balanceCodesAreCorrect(isBalanced);
		right.balanceCodesAreCorrect(isBalanced);
		return;
	}
 
	public int fastHeight() {
		if (this == Node.NULL_NODE)
			return -1;
 
		if (this.balance == Node.Code.LEFT)
			return this.left.height + 1;
		if (this.balance == Node.Code.RIGHT)
			return this.right.height + 1;
 
		return this.left.fastHeight() + 1;
	}
	

	public Node buildStringTree(String s, int first, int last) {
		if (first > last) return NULL_NODE;
		// remember Binary Search Method!!
		int mid = (first + last) / 2;
		Node curNode = new Node(s.charAt(mid));
		curNode.left = buildStringTree(s, first, mid - 1);
		curNode.right = buildStringTree(s, mid+1, last);
		curNode.update();
		return curNode;
	}
 
}