package com.sergon146.yandexhackaton.Labirint;
// class to implement the algorithm
import java.util.*;



public class maze{

    static Random randomizer = new Random();

    static int size;

    static int[][] maze;

    static Scanner in = new Scanner(System.in);



    /*  Step 2: Grouping ungrouped cells to form new sets    */

    static Cel[] makeSet(Cel[] row) {

        for(int index = 0; index < row.length; ) {

            Cel cell = row[index++];

            if(cell.set == null) {

                List<Cel> list = new ArrayList<Cel>();

                list.add(cell);

                cell.set=list;

            }

        }

        return row;

    }

    /*********************************************************/



    /*  Step 3: Creating right walls                         */

    static Cel[] makeRightWalls(Cel[] row) {

        for(int i = 1; i < row.length; i++) {

            if(isContainsInList(row[i-1].set,row[i])) {

                row[i-1].right=true;

                continue;

            }

            if(randomizer.nextBoolean())

                row[i-1].right=true;

            else

                row=merge(row,i);

        }

        return row;

    }



    static Cel[] merge(Cel[] row,int i) {  //utility function

        List<Cel> currentList = row[i-1].set;

        List<Cel> nextList = row[i].set;

        for(Cel j : nextList) {

            currentList.add(j);

            j.set=currentList;

        }

        return row;

    }



    static boolean isContainsInList(List<Cel> set,Cel cell) {  //utility function

        for(Cel i : set) {

            if(i==cell)

                return true;

        }

        return false;

    }

    /*********************************************************/



    /*  Step 4: Creating down walls                          */

    static boolean isNotDone(List<Cel> set){    //utility function

        boolean rslt=true;

        for(Cel x:set)

            rslt=rslt&&x.down;

        return rslt;

    }



    static Cel[] makeDown(Cel[] row){

        for(int i=0;i<row.length;i++){

            for(Cel x:row[i].set)x.down=true;

            while(isNotDone(row[i].set)){

                do{

                    row[i].set.get(randomizer.nextInt(row[i].set.size())).down=false;

                }while(randomizer.nextBoolean() );

            }

        }

        return row;

    }

    /*********************************************************/



    /*  Driver function to execute the algorithm             */



    static public boolean[][] driver(int inputSize){


       // System.out.print("Enter size: ");

        size = inputSize;

        maze=new int[2*size+1][2*size+1];

        Cel[] cur=new Cel[size];

        for(int i=0;i<size;i++)

            cur[i]=new Cel(0,i);

        for(int i=2;i<=2*size;i++)

            for(int j=2;j<=2*size;j++)

                maze[i][j]=0;

        for(int i=0;i<size;i++){

            cur=makeSet(cur);

            cur=makeRightWalls(cur);

            cur=makeDown(cur);

            if(i==size-1)

                cur=end(cur);

            printMaze(cur,i);

            if(i!=size-1)

                cur=genNextRow(cur);

        }

        //Creating upper and left boundary

        for(int i=0;i<=2*size;i++)

            maze[i][0]=maze[0][i]=maze[i][2*size]=maze[2*size][i]=1;

        for(int i=2;i<=2*size;i+=2)

            for(int j=2;j<=2*size;j+=2)

                maze[i][j]=1;

        boolean result[][] = new boolean[maze.length][maze.length];

        for(int i=0;i<2*size+1;i++){

           // System.out.println();

            for(int j=0;j<2*size+1;j++)

            {
                if (maze[i][j] == 1) result[i][j] = true;
                else  result[i][j] = false;
               //System.out.print(maze[i][j] + " ");
            }
        }

        return result;


    }

    static Cel[] end(Cel[] row) {

        for(int i = 1; i < row.length; i++) {

            if(findPos(row[i-1].set,row[i]) == -1) {
                row[i-1].right=false;
                row=merge(row,i);
            }
        }

        return row;

    }



    static int findPos(List<Cel> set,Cel x){

        Cel[] tmpArray = new Cel[set.size()];

        tmpArray = set.toArray(tmpArray);

        for(int i=0;i<tmpArray.length;i++)

            if(tmpArray[i]==x)

                return i;

        return -1;

    }



    static Cel[] genNextRow(Cel[] pre){

        for(int i = 0; i < pre.length;i++ ) {

            pre[i].right=false;

            pre[i].x++;

            if(pre[i].down) {

                pre[i].set.remove(findPos(pre[i].set, pre[i]));

                pre[i].set=null;

                pre[i].down=false;

            }

        }

        return pre;

    }

    static void printMaze(Cel[] row,int rowPos){

        rowPos=2*rowPos+1;

        for(int i=0;i<row.length;i++){

            if(row[i].right)

                maze[rowPos][2*i+2]=1;

            if(row[i].down)

                maze[rowPos+1][2*i+1]=1;

        }

    }
}

