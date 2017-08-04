package com.liang.tree;


import com.liang.stack.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by setup on 2017/7/18.
 */
public class BinarySearchTree<T extends Comparable<T>> {

    public Node root;

    @Override
    public String toString() {
        return "BinarySearchTree{" +
                "root=" + root +
                '}';
    }

    public boolean insert(T i){
        if(root == null){
            root = new Node(i);
            return true;
        }
        Node current = root;
        while(true){
            if(i.compareTo((T)current.data)<0){
                //小于，则与当前节点的左节点做比较
                if(current.left==null){
                    current.left = new Node(i);
                    break;
                }else {
                    current = current.left;
                }
            }else {
                //大于，则与当前节点的右节点左比较
                if(current.right==null){
                    current.right = new Node(i);
                    break;
                }else {
                    current = current.right;
                }
            }
        }
        return true;

    }

    public Node getNode(T i){
        if(root == null){
            return null;
        }
        Node current = root;
        while (true){
            if(i.compareTo((T)current.data)==0){
                break;
            }else if(i.compareTo((T)current.data)<0){
                current = current.left;
            }else if(i.compareTo((T)current.data)>0){
                current = current.right;
            }
        }
        return current;
    }
    public boolean contanis(T i){
        Node current = root;
        boolean result = false;
        while (true){
            if(current == null){
                break;
            }
            if(i.compareTo((T)current.data)==0){
                result = true;
                break;
            }else if(i.compareTo((T)current.data)<0){
                current = current.left;
            }else if(i.compareTo((T)current.data)>0){
                current = current.right;
            }
        }
        return result;
    }

    /**
     * 递归前序遍历
     * @param node
     */
    public void preOrder(Node node){
        System.out.println(" node = "+node.data);
        if(node.left!=null){
            preOrder(node.left);
        }
        if(node.right!=null){
            preOrder(node.right);
        }
    }

    /**
     * 非递归前序遍历
     *
     */
    public void preOrderWithoutRecures(){
        if(root==null){
            return;
        }
        Stack<Node> stack = new Stack(64);
        stack.push(root);
        Node current;
        while (!stack.isEmpty()){
            current=stack.getTop();
            if(current.state==0){
                System.out.println(current.data);
                current.state=1;
            }
            else if(current.state==1){
                if(current.left!=null){
                    stack.push(current.left);
                }
                current.state=2;
            }
            else if(current.state==2){
                if(current.right!=null){
                    stack.push(current.right);
                }
                current.state=3;
            }
            else if(current.state==3){
                stack.pop();
                current.state=0;
            }
        }

    }


    public void midOrder(Node node){
        if(node.left!=null){
            midOrder(node.left);

        }
        System.out.println(" node = "+node.data);

        if(node.right!=null){
            midOrder(node.right);
        }
    }
    /**
     * 非递归中序遍历
     *
     */
    public List midOrderWithoutRecures(){
        List array = new ArrayList();
        if(root==null){
            return null;
        }
        Stack<Node> stack = new Stack(64);
        stack.push(root);
        Node current;
        int i = 0;
        while (!stack.isEmpty()){
            current=stack.getTop();
            if(current.state==0){
                if(current.left!=null){
                    stack.push(current.left);
                }
                current.state=1;
            }
           else if(current.state==1){
                array.add(current.data);
                current.state=2;
                i++;
            }
            else if(current.state==2){
                if(current.right!=null){
                    stack.push(current.right);
                }
                current.state=3;
            }
            else if(current.state==3){
                stack.pop();
                current.state=0;
            }

        }
        return array;
    }


    public void endOrder(Node n){
        if(n.left!=null){
            endOrder(n.left);
        }
        if(n.right!=null){
            endOrder(n.right);
        }
        System.out.println(" node = "+n.data);

    }

    /**
     * 非递归后序遍历
     *
     */
    public void endOrderWithoutRecures(){
        if(root==null){
            return;
        }
        Stack<Node> stack = new Stack(64);
        stack.push(root);
        Node current;
        while (!stack.isEmpty()){
            current=stack.getTop();
            if(current.state==0){
                if(current.left!=null){
                    stack.push(current.left);
                }
                current.state=1;
            }
            else if(current.state==1){
                if(current.right!=null){
                    stack.push(current.right);
                }
                current.state=2;
            }else if(current.state==2){
                System.out.println(current.data);
                current.state=3;
            }
            else if(current.state==3){
                stack.pop();
                current.state=0;
            }
        }

    }
    /**
     * 非递按层遍历-有问题
     *
     */
    public void layerOrderWithoutRecures(){
        if(root==null){
            return;
        }
        Stack<Node> stack = new Stack(64);
        stack.push(root);
        Node current;
        while (!stack.isEmpty()){
            current=stack.getTop();
            if(current.state==0){
                System.out.println(current.data);
                current.state=1;
            }
            else if(current.state==1){
                if(current.left!=null){
                    stack.push(current.left);
                }
                if(current.right!=null){
                    stack.push(current.right);
                }
                current.state=2;
            }
            else if(current.state==2){
                stack.pop();
                current.state=0;
            }

        }

    }
    public static void main(String[] args){
        BinarySearchTree binarySearchTree = new BinarySearchTree();
        int[] srcArray = new int[]{-4, -3, -2, -1, 2, 3, 5, 6, 7, 9, 12, 33, 35};
        for(int i:srcArray){
            binarySearchTree.insert(i);
        }
        List<Integer> orderedArray =  binarySearchTree.midOrderWithoutRecures();
        System.out.println("排序后数组："+orderedArray);
        /*LinkedHashMap是有序不重复数组*/
        Map childArray = new LinkedHashMap();//存放当前连续数组
        Map longestChildArray = new LinkedHashMap();//存放当前最长连续数组
        boolean serial = false;//标记变量，标记目前是否是连续的
        for(int j=0;j<orderedArray.size()-1;j++){
            if(orderedArray.get(j+1)-orderedArray.get(j)==1){
                serial = true;
                childArray.put(orderedArray.get(j),1);
                childArray.put(orderedArray.get(j+1),1);
            }else {
                serial = false;
            }
            //serial是false，说明当前连续已被断开
            if(!serial){
                //通过比较本次连续子数组的长度和之前连续子数组的长度来看是否要替换
                if(childArray.size()>longestChildArray.size()){
                    longestChildArray = childArray;
                }
                //清空childArray，放置新的连续数组
                childArray = new LinkedHashMap();//
            }
        }
        System.out.println("连续最长子数组："+longestChildArray.keySet());

    }

}
