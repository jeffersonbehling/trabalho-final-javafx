/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Model;

/**
 *
 * @author jefferson
 */
public class Location {
    private int id;
    private String locationDate;
    private String devolutionDate;
    private float price;
    private boolean returned;
    private int clientId;
    private int projectorId;
    private String clientName;
    private String projectorBrandModel;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getProjectorBrandModel() {
        return projectorBrandModel;
    }

    public void setProjectorBrandModel(String projectorBrandModel) {
        this.projectorBrandModel = projectorBrandModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationDate() {
        return locationDate;
    }

    public void setLocationDate(String locationDate) {
        this.locationDate = locationDate;
    }

    public String getDevolutionDate() {
        return devolutionDate;
    }

    public void setDevolutionDate(String devolutionDate) {
        this.devolutionDate = devolutionDate;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getProjectorId() {
        return projectorId;
    }

    public void setProjectorId(int projectorId) {
        this.projectorId = projectorId;
    }
    
    
}
