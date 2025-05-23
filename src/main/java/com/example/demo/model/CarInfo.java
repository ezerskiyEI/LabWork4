package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cars")
public class CarInfo {
    @Id
    private String vin;

    private String make;
    private String model;
    private int year;

    @ManyToMany(mappedBy = "cars")
    @JsonIgnore
    private Set<Owner> owners = new HashSet<>();

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public Set<Owner> getOwners() { return owners; }
    public void setOwners(Set<Owner> owners) { this.owners = owners; }
}