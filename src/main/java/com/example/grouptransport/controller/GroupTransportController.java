package com.example.grouptransport.controller;

import com.example.grouptransport.model.API1.route.ComputedRoute;
import com.example.grouptransport.model.Group;
import com.example.grouptransport.model.User;
import com.example.grouptransport.model.Vehicle;
import com.example.grouptransport.service.GroupService;
import com.example.grouptransport.service.GroupWalkService;
import com.example.grouptransport.service.UserService;
import com.example.grouptransport.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grouptransport")
public class GroupTransportController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupWalkService groupWalkService;
    @Autowired
    private UserService userService;
    @Autowired
    private VehicleService vehicleService;

    //*enpoint för att registrera en användare som sedan personidentifieras i varje API anrop
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user){
        //kontrollerar om användarnamnet är null eller om det redan är upptaget
        if (user.getUsername() == null || userService.findUserByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(400).body(user);
        }
        userService.createUser(user);
        return ResponseEntity.status(201).body(user);
    }

    //*endpoint för att skapa en grupp av en användare
    @PostMapping("/createGroup")
    public ResponseEntity<?> createGroup(@RequestHeader("Username") String username, @RequestBody Group group){
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("Ingen användare hittad");
        }
        Group createdGroup = groupService.createGroup(group,user);
        return ResponseEntity.status(201).body(createdGroup);
    }

    //*endpoint för att regirstrera en användare till gruppen
    @PostMapping("/{groupId}/addUser")
    public ResponseEntity<Group> addUserToGroup(@RequestHeader("Username") String username, @PathVariable Long groupId, @RequestBody User newUser){
        User requestingUser = userService.findUserByUsername(username);
        if (requestingUser == null) {
            return ResponseEntity.status(400).body(null);
        }
        Group updatedGroup = groupService.addUserToGroup(newUser, groupId);
        return ResponseEntity.status(201).body(updatedGroup);
    }

    //*endpoint för att lägga till fordon i gruppen
    @PostMapping("/{groupId}/addVehicle")
    public ResponseEntity<Group> addVehicleToGroup(@RequestHeader("Username") String username, @PathVariable Long groupId, @RequestBody Vehicle newVehicle){
        User requestingUser = userService.findUserByUsername(username);
        if (requestingUser == null) {
            return ResponseEntity.status(400).body(null);
        }
        Group updatedGroup = groupService.addVehicleToGroup(newVehicle, groupId);
        return ResponseEntity.status(201).body(updatedGroup);
    }

    //*endpoint för att hämta en lista över fordon som finns i gruppen och om det är upptaget eller ej
    @GetMapping("/{groupId}/vehicles")
    public ResponseEntity<List<Vehicle>> getVehiclesInGroup(@RequestHeader("Username") String username, @PathVariable Long groupId) {
        User requestingUser = userService.findUserByUsername(username);
        if (requestingUser == null) {
            return ResponseEntity.status(400).body(null);
        }

        List<Vehicle> vehicles = groupService.getVehiclesInGroup(groupId);
        return ResponseEntity.status(200).body(vehicles);
    }

    //*endpoint för att ta bort fordon från gruppen
    @DeleteMapping("/{groupId}/removeVehicle/{vehicleId}")
    public ResponseEntity<?> removeVehicleFromGroup(@RequestHeader("Username") String username, @PathVariable Long groupId, @PathVariable Long vehicleId) {
        User requestingUser = userService.findUserByUsername(username);
        if (requestingUser == null) {
            return ResponseEntity.status(400).body(null);
        }

        groupService.removeVehicleFromGroup(groupId, vehicleId);
        return ResponseEntity.ok().build();
    }

    //*enpoint för att sätta ett visst fordon i gruppen som är upptagen. Det ska även gå att sätta hur länge fordonet förväntas vara upptagen
    //Tiden ska utgå från en rutt hämtad från api:et som hanterar enskild transport API 1 (Tobias).
    @PostMapping("/{groupId}/vehicles/{vehicleId}/setVehicleBusy")
    public ResponseEntity<?> setVehicleBusy(@RequestHeader("Username") String username, @PathVariable Long groupId, @PathVariable Long vehicleId){
        User requestingUser = userService.findUserByUsername(username);
        if (requestingUser == null) {
            return ResponseEntity.status(400).body(null);
        }                                                              

        groupService.setVehicleBusy(groupId, vehicleId);
        return ResponseEntity.ok().body("Fordonet satt till upptaget");
    }


    //*enpoint för att registrera grupp-promenader

    //*enpoint för att avregistrera promenader

    //*endpoint för att hämta grupp-promenader
    //Promenadens rutt hämtas från tjänsten som hanterar enskild transport API 1 (Tobias).
    /*@GetMapping("/{groupWalkId}/getGroupWalk")
    public ResponseEntity<?> getGroupWalk(@PathVariable Long groupWalkId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ComputedRoute> route = restTemplate.getForEntity("https://tohemu23.azurewebsites.net/api/v1/routes/Foot/from/to", ComputedRoute.class);
        route.getBody().getRoute().getWaypoints();
    }    */
}
