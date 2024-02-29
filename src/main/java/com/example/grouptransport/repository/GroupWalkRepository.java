package com.example.grouptransport.repository;

import com.example.grouptransport.model.GroupWalk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupWalkRepository extends JpaRepository<GroupWalk, Long> {

    Optional<GroupWalk> findByIdAndGroupId(Long id, Long groupId);
}
