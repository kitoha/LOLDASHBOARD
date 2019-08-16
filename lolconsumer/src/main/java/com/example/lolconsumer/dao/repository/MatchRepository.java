package com.example.lolconsumer.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.lolconsumer.Entity.Match;

public interface MatchRepository extends JpaRepository<Match, Integer> {
}
