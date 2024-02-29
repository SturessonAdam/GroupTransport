package com.example.grouptransport.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteRequestDTO {
    private String startPos;
    private String endLoc;
}
