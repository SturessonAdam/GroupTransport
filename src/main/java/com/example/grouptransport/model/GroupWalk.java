package com.example.grouptransport.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class GroupWalk {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String route;

    @ManyToOne
    private Group group;
}
