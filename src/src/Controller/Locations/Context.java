/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Locations;

import src.Model.Location;

/**
 *
 * @author jefferson
 */
public class Context {
    private final static Context instance = new Context();

    public static Context getInstance() {
        return instance;
    }
    
    
    private Location location;

    public Location location() {
        location = new Location();
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public Location getLocation() {
        return location;
    }
}
