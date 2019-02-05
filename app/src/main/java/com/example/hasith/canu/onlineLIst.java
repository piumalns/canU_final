package com.example.hasith.canu;

public class onlineLIst {

    private String image;
    private String name;
    private String machineType;

    public onlineLIst(String image, String name, String machineType) {
        this.image = image;
        this.name = name;
        this.machineType = machineType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }
}
