package me.ajlane.loadcypher;

import me.ajlane.loadcypher.BuildingFactory;
import me.ajlane.loadcypher.BuildingType;

public class Main {

	/*
    private static void print(BuildingType building) {
        System.out.println("Building Info: " +
                building.getBuildingId() + " " +
                building.getBuildingName() + " " +
                building.getBuildingAddress());
    }
    */

    /**
     * Create three building objects by calling the create() method of
     * the factory.
     */

    public static void main(String[] args) {
    	
    	GraphLoaderFactory factory = new GraphLoaderFactory();
    	
    	/*
        BuildingFactory factory = new BuildingFactory();
        print(factory.create("BUILDING-A", "100 WEST MAIN", "1"));
        print(factory.create("BUILDING-B", "110 WEST MAIN", "2"));
        print(factory.create("BUILDING-C", "120 WEST MAIN", "3"));
    	 */
    }

}