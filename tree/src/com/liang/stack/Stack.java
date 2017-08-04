package com.liang.stack;

import com.liang.tree.Node;

import java.util.ArrayList;

/**
 * Created by setup on 2017/7/19.
 */
public class Stack<T> {
    private ArrayList<T> arrayList ;
    private int size;
    public Stack(int size){
        this.size = size;
        arrayList = new ArrayList<T>(size);
    }
    public void push(T i){
        if(arrayList.size()==size){
            System.out.println(" can't push any T ");
        }else {
            arrayList.add(i);
        }
    }
    public T pop(){
        if(isEmpty()){
            throw new ArrayIndexOutOfBoundsException(" stack is empty ,can't pop any T  ");
        }
        T i = arrayList.get(arrayList.size()-1);
        arrayList.remove(arrayList.size()-1);
        return i;

    }
    public boolean isEmpty(){
        return arrayList.size()==0;
    }
    public T getTop(){
        if(isEmpty()){
            return null;
        }
        return arrayList.get(arrayList.size()-1);
    }

    public static void main(String[] args){
        Stack stack = new Stack(4);
        stack.push(1);
        stack.push(2);
        stack.push(new Node(3));
        stack.push("4");
        stack.push("5");

        System.out.println(" stack "+stack);
        Object i = stack.pop();
        System.out.println(" i "+i);
         i = stack.pop();
        System.out.println(" i "+i);
         i = stack.pop();
        System.out.println(" i "+i);
         i = stack.pop();
        System.out.println(" i "+i);

        System.out.println(" stack "+stack);
    }

    @Override
    public String toString() {
        return "Stack{" +
                "arrayList=" + arrayList +
                '}';
    }
}
