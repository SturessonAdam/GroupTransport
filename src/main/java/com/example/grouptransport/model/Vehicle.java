package com.example.grouptransport.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String type;  //bil eller cykel
    private String location;
    private boolean isAvailable;
    private Integer busyForSeconds;

    @ManyToOne
    @JsonBackReference
    private Group group;

}
