/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Clients;

import src.Controller.Clients.*;
import src.Model.Client;

/**
 *
 * @author jefferson
 */
public class Context {
	private final static Context instance = new Context();

    public static Context getInstance() {
        return instance;
    }

    private Client client = new Client();

    public Client client() {
        return client;
    }
}
