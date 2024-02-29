package com.example.grouptransport.controller;

import com.example.grouptransport.model.*;
import com.example.grouptransport.service.GroupService;
import com.example.grouptransport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grouptransport")
public class GroupTransportController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;

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
    public ResponseEntity<?> setVehicleBusy(@RequestHeader("Username") String username, @PathVariable Long groupId, @PathVariable Long vehicleId, @RequestBody RouteRequestDTO routeRequest){
        User requestingUser = userService.findUserByUsername(username);
        if (requestingUser == null) {
            return ResponseEntity.status(400).body(null);
        }                                                              

        groupService.setVehicleBusy(groupId, vehicleId, routeRequest);
        return ResponseEntity.ok().body("Fordonet med id " + vehicleId + " i grupp " + groupId + " satt till upptaget");
    }


    //*enpoint för att registrera grupp-promenader
    @PostMapping("/{groupId}/registerGroupWalk")
    public ResponseEntity<GroupWalk> registerGroupWalk(@RequestHeader("Username") String username, @PathVariable Long groupId, @RequestBody RouteRequestDTO routeRequest){
        User requestingUser = userService.findUserByUsername(username);
        if (requestingUser == null) {
            return ResponseEntity.status(400).body(null);
        }

        GroupWalk newGroupWalk = groupService.registerGroupWalk(groupId, routeRequest);
        return ResponseEntity.status(201).body(newGroupWalk);
    }

    //*enpoint för att avregistrera promenader
    @DeleteMapping("/{groupId}/removeGroupWalk/{groupwalkId}")
    public ResponseEntity<?> removeGroupWalk(@RequestHeader("Username") String username, @PathVariable Long groupId, @PathVariable Long groupwalkId){
         User requestingUser = userService.findUserByUsername(username);
         if (requestingUser == null) {
             return ResponseEntity.status(400).body(null);
         }

        groupService.removeGroupWalk(groupId, groupwalkId);
        return ResponseEntity.ok().build();
    }

    //*endpoint för att hämta grupp-promenader

}
