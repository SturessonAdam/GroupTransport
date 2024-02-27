package com.example.grouptransport.service;

import com.example.grouptransport.repository.GroupWalkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupWalkService {

    @Autowired
    private GroupWalkRepository groupWalkRepository;

}
