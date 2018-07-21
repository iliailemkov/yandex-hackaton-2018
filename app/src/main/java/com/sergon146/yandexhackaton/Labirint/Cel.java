package com.sergon146.yandexhackaton.Labirint;
// a utility class to represent single cell
import java.util.*;

public class Cel{
    public boolean right,down;

    public List<Cel> set;

    public int x,y;

    Cel(int a,int b){

        x=a;

        y=b;

        right=false;

        down=true;

        set=null;

    }

}