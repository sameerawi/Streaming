package com.checkout;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Starting the EventProducer!");

        new EventProducer().produce();
    }
}