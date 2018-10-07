/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author sabbir
 */
public class DecisionTree {

    /**
     * @param args the command line arguments
     */
    private int numOfData;
    private int numOfTrainData;
    private int numOfTestData;
    private final int numOfAttributes=9; //change for more attributes
    private final int percentOfTrain=80;
    private final int numOfAttribValues=10;
    private final int Ntimes=100;
    
    private Node root;
    
    //private double entropy;
  
    CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> data;
    CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> trainData;
    CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> testData;
    
    private String dataFileName="";
    
    double accuracyAvg,precisionAvg,recallAvg,f_measureAvg,g_meanAvg;
    
    public DecisionTree(String fileName) throws FileNotFoundException{
        dataFileName=fileName;
        data=new CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>();
        trainData=new CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>();
        testData=new CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>();
        //System.out.println("Hi");
        readData(dataFileName);
        //showData();
        numOfData=data.size();
        
        accuracyAvg=precisionAvg=recallAvg=f_measureAvg=g_meanAvg=0;
        
        /*
        selectTrainAndTestData(percentOfTrain);
        //entropy=initialEntropy();
        //entropy=calculateEntropy(trainData);
        
        CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> example;
        example=trainData;
       
        CopyOnWriteArraySet<Integer> A=new CopyOnWriteArraySet<Integer>();
        for(int i=0;i<numOfAttributes;i++){
            A.add(i);
        }
        
        //bestAttribute(entropy,A,example);
        
        root=new Node();
        root.parent=null;
        
        ID3(root, A, example);
        
        test();
        * 
        */
        runNTimes();
    }
    
    private void runNTimes(){
        int i;
        for(i=0;i<Ntimes;i++){
            //clear for each iteration
            trainData.clear();
            testData.clear();
            
            //start actual work
            selectTrainAndTestData(percentOfTrain);
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> example;
            example=trainData;
            CopyOnWriteArraySet<Integer> A=new CopyOnWriteArraySet<Integer>();
            for(int j=0;j<numOfAttributes;j++){
                A.add(j);
            }
            root=new Node();
            root.parent=null;

            ID3(root, A, example);

            test();
        }
        accuracyAvg/=Ntimes;
        precisionAvg/=Ntimes;
        recallAvg/=Ntimes;
        f_measureAvg/=Ntimes;
        g_meanAvg/=Ntimes;
        
        System.out.printf("Accuracy= \t%.12f\n",accuracyAvg);
        System.out.printf("Preicision= \t%.12f\n",precisionAvg);
        System.out.printf("Recall= \t%.12f\n",recallAvg);
        System.out.printf("F-measure= \t%.12f\n",f_measureAvg);
        System.out.printf("G-mean= \t%.12f\n",g_meanAvg);
    }
    
    private void readData(String fileName) throws FileNotFoundException{
        String line="";
        Scanner in = new Scanner(new FileReader(new File(fileName)));
        
        while(in.hasNextLine()){
            line=in.nextLine();
            line=line.trim();
            String[] split=line.split(",");
            CopyOnWriteArrayList<Integer> temp=new CopyOnWriteArrayList<Integer>();
            for(int l=0;l<numOfAttributes+1;l++){
                temp.add(Integer.parseInt(split[l]));
            }
            data.add(temp);
        }
        in.close();
    }
    /*
    private void showData(){//for test only
        int size=data.size();
        for(int i=0;i<size;i++){
            System.out.print(i+".");
            for(int l=0;l<numOfAttributes+1;l++){
                System.out.print(data.get(i).get(l)+",");
            }
            System.out.println();
        }
    }
    */
    
    private void selectTrainAndTestData(int percent){
        int i;
        numOfTrainData= (int)((numOfData*percent)/100);
        numOfTestData=numOfData-numOfTrainData;
        ArrayList<Integer> random=new ArrayList<Integer>();
        for(i=0;i<numOfData;i++){
            random.add(i);
        }
        Collections.shuffle(random);//create array of random integers
        /*
        for(i=0;i<numOfData;i++){
            System.out.print(random.get(i)+" ");
        }
        System.out.println("\n");
        * 
        */
        
        //select train data with first numOfTrainData integers
        for(i=0;i<numOfTrainData;i++){
            //System.out.print(random.get(i)+",");
            trainData.add(data.get(random.get(i)));
        }
        //System.out.println("\n");
        //test data
        for(;i<numOfData;i++){
            //System.out.print(random.get(i)+",");
            testData.add(data.get(random.get(i)));
        }
        //System.out.println("\n"+i);
        
    }
    
    private double calculateEntropy(CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> example){//information gain for one attribute
        double entropy=0;
        int countPlus=0;
        int exSize=example.size();
        for(int i=0;i<exSize;i++){
            if(example.get(i).get(numOfAttributes)==1)
                countPlus++;
        }
        double p1=countPlus/(exSize*1.0);
        double p2=(exSize-countPlus)/(exSize*1.0);
        entropy=-p1*Math.log(p1)-p2*Math.log(p2);
        //System.out.println("Entropy= "+entropy);
        return entropy;
    }
    
    private void ID3(Node node,CopyOnWriteArraySet<Integer> attributes,CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> example){
        int i,target_attrib;
        int size=example.size();
        if(size==0){//example empty
            node.classval=-1;
            return;
        }
        int val=example.get(0).get(numOfAttributes);
        for(i=1;i<size;i++){
            if(example.get(i).get(numOfAttributes)!=val){//check class
                break;
            }
        }
        if(i==size){//all examples are in same class with value 'val'
            node.classval=val;
            node.child=null;
            return;
        }
        int data0=0,data1=0;
        for(i=0;i<size;i++){
            if(example.get(i).get(numOfAttributes)==0){//check class
                data0++;
            }
            else{
                data1++;
            }
        }
        if(data0>=data1){
            target_attrib=0;
        }
        else{
            target_attrib=1;
        }
        int sizeAtt=attributes.size();
        if(sizeAtt==0){//attributes empty
            //add value for class\
            node.classval=target_attrib;
            node.child=null;
            return;
        }
        
        
        //no termination
        double currentEntropy=calculateEntropy(example);
        int index=bestAttribute(currentEntropy, attributes, example);//decision attribute 
        //System.out.println("bestAttriv: "+index);
        
        CopyOnWriteArraySet<Integer> attributesUpdated=new CopyOnWriteArraySet<Integer>(attributes);
        attributesUpdated.remove(index);
        //System.out.println("After delete attributes:"+attributes);
        //System.out.println("attributesUpdated:"+attributesUpdated);
        
        //update node value
        node.classval=-1;//not a classifier
        node.attribute=index;
        
        //split for the best attribute index
        CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> splittedData;
        splittedData=new CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>>();
        
        for(i=0;i<numOfAttribValues;i++){
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> temp;
            temp=splitData(i+1,index,example);
            splittedData.add(temp);
        }
        /*
        for(i=0;i<numOfAttribValues;i++){
            System.out.println(splittedData.get(i));
        }
        * 
        */
        
        for(i=0;i<numOfAttribValues;i++){
            CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> temp;
            temp=splittedData.get(i);
            Node n=new Node();
            n.parent=node;
            node.child.add(n);
            
            if(temp.size()==0){
                //set with target attribute
                n.classval=target_attrib;
                n.child=null;
            }
            else{
                //System.out.println("tempsize:"+temp.size());
                //System.out.println("temp:"+temp);
                ID3(n, attributesUpdated, temp);
            }
            
        }
        
    }
    
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> splitData(int val,int index,CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> example){
        CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> splitted=new CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>();
        
        int size=example.size();
        for(int i=0;i<size;i++){
            if(example.get(i).get(index)==val){
                splitted.add(example.get(i));
            }
        }
        return splitted;
    }
    
    private int bestAttribute(double currentEntropy,CopyOnWriteArraySet<Integer> attributes,CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> example){
        int index=0;
        double bestGain=-100;
        double tempGain;
        int exampleSize=example.size();
        for(Integer i:attributes){
            //System.out.println("attrib: "+i);
            tempGain=informationGain(i,exampleSize, currentEntropy,example);
            //System.out.println("gain: "+tempGain);
            if(bestGain<tempGain){
                bestGain=tempGain;
                index=i;
            }
        }
        //System.out.println("bestAttriv: "+index);
        return index;
    }
    
    private double informationGain(int index,int totalData,double currentEntropy,CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> example){//for one atribute
        //,
        //int index=0;
        int i;
        //int totalData;
        //attributes.size();
        int exSize=example.size();
        CopyOnWriteArrayList<Integer> class0=new CopyOnWriteArrayList<Integer>();
        CopyOnWriteArrayList<Integer> class1=new CopyOnWriteArrayList<Integer>();
        for(i=0;i<numOfAttribValues+1;i++){
            class0.add(0);
            class1.add(0);
        }
        
        for(i=0;i<exSize;i++){
            if(example.get(i).get(numOfAttributes)==0){
                class0.set(example.get(i).get(index),class0.get(example.get(i).get(index))+1 ); //0 is j
            }
            else if(example.get(i).get(numOfAttributes)==1){
                class1.set(example.get(i).get(index),class1.get(example.get(i).get(index))+1 ); //0 is j
            }
        }
        //int total=0;
        double gain=0;
        int data0,data1;
        double p1,p2,tempEntropy=0;
        for(i=1;i<numOfAttribValues+1;i++){//value range 1-10
            //System.out.println("i="+i);
            data0=class0.get(i);
            data1=class1.get(i);
            
            //total+=(data0+data1);
            
            //System.out.println("data0="+data0+"\tdata1="+data1);
            if(data0==0 && data1==0){//to make log value 0
                p1=1;
                p2=1;
            }
            else{
                p1=data0/((data0+data1)*1.0);
                p2=data1/((data0+data1)*1.0);
                //System.out.println("p1="+p1+"\tp2="+p2);
                if(p1==0)p1=1;//to calculate log(0)=0 making it log(1) which is 0
                if(p2==0)p2=1;
            }
            tempEntropy+=(((data0+data1)/(totalData*1.0))*(-p1*Math.log(p1)-p2*Math.log(p2)));
        }
        
        gain=currentEntropy-tempEntropy;
        //System.out.println("gain="+gain);
        //System.out.println("totalData="+totalData);
        //System.out.println("total="+total);
        
        return gain;
    }
    
    private void test(){
        Node currentNode;
        currentNode=root;
        
        int tp=0;
        int tn=0;
        int fp=0;
        int fn=0;
        
        double accuracy,precision,recall,f_measure,g_mean;
        
        for(int i=0;i<numOfTestData;i++){
            CopyOnWriteArrayList<Integer> temp;
            temp=testData.get(i);
            
            //System.out.println(temp);
            while(currentNode.child!=null){
                int index=currentNode.attribute;
                int attribVal=temp.get(index);
                currentNode=currentNode.child.get(attribVal-1);
            }
            //System.out.println(""+currentNode.classval);
            if(temp.get(numOfAttributes)==1 && currentNode.classval== 1){
                tp++;
            }
            else if(temp.get(numOfAttributes)==0 && currentNode.classval== 0){
                tn++;
            }
            else if(temp.get(numOfAttributes)==0 && currentNode.classval== 1){
                fp++;
            }
            else if(temp.get(numOfAttributes)==1 && currentNode.classval== 0){
                fn++;
            }
            
            currentNode=root;
        }
        accuracy=(tp+tn)/((tp+tn+fp+fn)*1.0);
        precision=tp/((tp+fp)*1.0);
        recall=tp/((tp+fn)*1.0);
        f_measure=(2*precision*recall)/(precision+recall);
        g_mean=Math.sqrt(precision*recall);
        
        accuracyAvg+=accuracy;
        precisionAvg+=precision;
        recallAvg+=recall;
        f_measureAvg+=f_measure;
        g_meanAvg+=g_mean;
        
        //show all
        //System.out.println(accuracy+"\t"+precision+"\t"+recall+"\t"+f_measure+"\t"+g_mean);
        //System.out.printf("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n",accuracy,precision,recall,f_measure,g_mean);
    }
    
    
    
    public static void main(String[] args){
        // TODO code application logic here
        String fileName="//Users//ahmadsabbir//Documents//workspace//DecisionTree//src//data.txt";
        try {
            new DecisionTree(fileName);
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(DecisionTree.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("File Not Found!");
        }
    }
}

class Node{
    public int classval=-1;
    public CopyOnWriteArrayList<Node> child=new CopyOnWriteArrayList<Node>();
    public Node parent;
    public int attribute;
}