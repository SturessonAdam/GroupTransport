package com.example.grouptransport.service;

import com.example.grouptransport.model.API1.route.ComputedRoute;
import com.example.grouptransport.model.API1.route.Route;
import com.example.grouptransport.model.Group;
import com.example.grouptransport.model.GroupWalk;
import com.example.grouptransport.model.User;
import com.example.grouptransport.model.Vehicle;
import com.example.grouptransport.repository.GroupRepository;
import com.example.grouptransport.repository.GroupWalkRepository;
import com.example.grouptransport.repository.UserRepository;
import com.example.grouptransport.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private GroupWalkRepository groupWalkRepository;

    public Group createGroup(Group group, User user) {
        group.getUsers().add(user); //lägger till användaren i grupplistan

        return groupRepository.save(group);
    }

    public Group addUserToGroup(User newUser, Long groupId) {
        //kontrollera om det finns en användare i databasen med samma namn
        User user = userRepository.findByUsername(newUser.getUsername());

        if(user == null){
            user = new User(); //skapar en ny användare om det inte fanns någon
            user.setUsername(newUser.getUsername());
            userRepository.save(user);
        }
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        if (groupOptional.isEmpty()) { //kollar om gruppen finns
            throw new RuntimeException("gruppen inte hittad");
        }

        Group group = groupOptional.get();

        group.getUsers().add(user); // lägger till användaren i grupplistan
        user.setGroup(group); // sätter gruppen på användaren

        return groupRepository.save(group);
    }

    @Transactional
    public Group addVehicleToGroup(Vehicle newVehicle, Long groupId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("gruppen inte hittad"));

        newVehicle.setGroup(group);
        vehicleRepository.save(newVehicle); //skapar alltid nytt fordon och sätter den till gruppen

        return group;
    }

    public List<Vehicle> getVehiclesInGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("gruppen inte hittad"));

        return new ArrayList<>(group.getVehicles());
    }

    public void removeVehicleFromGroup(Long groupId, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("fordonet inte hittat"));

        if (!vehicle.getGroup().getId().equals(groupId)) {
            throw new RuntimeException("fordonet tillhör inte gruppen");
        }

        vehicle.setGroup(null); //tar bort kopplinge mellan fordon och grupp
        vehicleRepository.save(vehicle); //sparar fordonet utan att ha en grupp
    }

    @Transactional
    public void setVehicleBusy(Long vehicleId, Long groupId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new RuntimeException("fordonet inte hittat"));

        if (!vehicle.getGroup().getId().equals(groupId)) {
            throw new RuntimeException("fordonet tillhör inte denna grupp");
        }
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Route> route = restTemplate.getForEntity("https://tohemu23.azurewebsites.net/api/v1/routes/Car/Oskarshamn/Kalmar/raw", Route.class);
        int busyForSeconds = route.getBody().getTime().intValue(); //hämtar rutten från Tobias API

        vehicle.setAvailable(false); //sätter fordonet upptaget
        vehicle.setBusyForSeconds(busyForSeconds); //sätter tiden i sekunder
        vehicleRepository.save(vehicle);
    }

    public GroupWalk registerGroupWalk(Long groupId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Route> route = restTemplate.getForEntity("https://tohemu23.azurewebsites.net/api/v1/routes/Foot/Oskarshamn/Kalmar/raw", Route.class);

        String routeDetails = route.getBody().getWaypoints().toString(); //hämtar rutten från Tobias API
        GroupWalk groupWalk = new GroupWalk();
        groupWalk.setRoute(routeDetails); //sätter rutten från Tobias API

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("gruppen inte hittad"));
        groupWalk.setGroup(group); //sätter grupp-promenaden till gruppen

        return groupWalkRepository.save(groupWalk);
    }


}
