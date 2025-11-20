package com.hwpok.commons.math;


import com.hwpok.commons.util.math.SnowId;

public class SnowIdTestCase {
    public static void main(String[] args) {
       SnowId snowId =  SnowId.createDefault(10);
       System.out.println(snowId.nextId());
    }
}
