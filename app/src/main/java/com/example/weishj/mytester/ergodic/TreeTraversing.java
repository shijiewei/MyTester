package com.example.weishj.mytester.ergodic;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * 多叉树遍历
 */
public class TreeTraversing {

	public static TreeNode makeTree() {
		TreeNode root = new TreeNode("A", null);

		TreeNode B = new TreeNode("B", root);
		TreeNode C = new TreeNode("C", root);
		TreeNode D = new TreeNode("D", root);

		root.addChild(B);
		root.addChild(C);
		root.addChild(D);


		TreeNode E = new TreeNode("E", B);
		TreeNode F = new TreeNode("F", B);

		B.addChild(E);
		B.addChild(F);

		TreeNode G = new TreeNode("G", D);

		D.addChild(G);


		TreeNode H = new TreeNode("H", E);
		TreeNode I = new TreeNode("I", E);
		TreeNode J = new TreeNode("J", E);

		E.addChild(H);
		E.addChild(I);
		E.addChild(J);

		return root;
	}

	/**
	 * 递归遍历
	 * 如果树太深的话，会出现java.lang.StackOverflowError
	 *
	 * @param root
	 */
	public static void recursion(TreeNode root) {
		System.out.println(root.getName());
		for (TreeNode child : root.getChildren()) {
			recursion(child);
		}
	}

	/**
	 * 广度优先需要构建一个先进先出的队列
	 *
	 * @param root
	 */
	public static void breadthFirst(TreeNode root) {
		Deque<TreeNode> nodeDeque = new LinkedList<>();
		TreeNode node = root;
		nodeDeque.add(node);
		while (!nodeDeque.isEmpty()) {
			node = nodeDeque.pop();
			System.out.println(node.getName());
			nodeDeque.addAll(node.getChildren());
		}

	}

	/**
	 * 深度优先需要构建一个后进先出的栈
	 *
	 * @param root
	 */
	public static void depthFirst(TreeNode root) {
		Deque<TreeNode> nodeDeque = new LinkedList<>();
		TreeNode node = root;
		nodeDeque.push(node);
		while (!nodeDeque.isEmpty()) {
			node = nodeDeque.pop();
			System.out.println(node.getName());
			List<TreeNode> children = node.getChildren();
			//注意这里要从后向前遍历
			for (int i = children.size() - 1; i >= 0; i--) {
				nodeDeque.push(children.get(i));
			}
		}
	}

	public static void main(String[] args) {
		TreeNode root = makeTree();
		//recursion(root);
		//breadthFirst(root);
		depthFirst(root);
	}


}

class TreeNode {

	private String name;
	private TreeNode parent;
	private List<TreeNode> children = new ArrayList<>();

	public TreeNode(String name, TreeNode parent) {
		this.name = name;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public void addChild(TreeNode child) {
		children.add(child);
	}
}

