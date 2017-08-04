package com.liang.tree;

/**
 * Created by setup on 2017/7/18.
 */
public class Node {
    public Object data;
    public Node left;
    public Node right;
    public int state=0;
    public Node(Object data){
        this.data=data;
    }


    @Override
    public String toString() {
        return "Node{" +
                "data=" + data +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
