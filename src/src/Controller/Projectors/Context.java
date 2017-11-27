/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Projectors;

import src.Model.Projector;

/**
 *
 * @author jefferson
 */
public class Context {
	private final static Context instance = new Context();

    public static Context getInstance() {
        return instance;
    }

    private Projector projector = new Projector();

    public Projector projector() {
        return projector;
    }
}
