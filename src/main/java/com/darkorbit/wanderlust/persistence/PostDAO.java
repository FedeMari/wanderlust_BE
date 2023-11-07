package com.darkorbit.wanderlust.persistence;

import com.darkorbit.wanderlust.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostDAO extends JpaRepository<Post, Long>{}
