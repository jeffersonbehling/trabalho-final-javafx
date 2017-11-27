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
public class Projector {
    private int id;
    private String brand;
    private String model;
    private String serialNumber;
    private String purchaseDate;
    private int ansiLumens;
    private String dateLastLampChange;
    private String projectorState;
    
    public Projector() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getAnsiLumens() {
        return ansiLumens;
    }

    public void setAnsiLumens(int ansiLumens) {
        this.ansiLumens = ansiLumens;
    }

    public String getDateLastLampChange() {
        return dateLastLampChange;
    }

    public void setDateLastLampChange(String dateLastLampChange) {
        this.dateLastLampChange = dateLastLampChange;
    }

    public String getProjectorState() {
        return projectorState;
    }

    public void setProjectorState(String projectorState) {
        this.projectorState = projectorState;
    }
}
