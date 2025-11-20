package com.hwp.commons.math;


import com.hwp.commons.util.math.SnowId;

public class SnowIdTestCase {
    public static void main(String[] args) {
       SnowId snowId =  SnowId.createDefault(10);
       System.out.println(snowId.nextId());
    }
}
