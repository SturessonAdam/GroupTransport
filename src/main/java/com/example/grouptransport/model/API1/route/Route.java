package com.example.grouptransport.model.API1.route;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * @author Tobias Heidlund
 */
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED, force=true)
@Data
@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    final TypeOfTravel typeOfTravel;
    final Double distance;
    final Double time;
    final String start;
    final String stop;
    @ElementCollection
    List<String> waypoints;


}
