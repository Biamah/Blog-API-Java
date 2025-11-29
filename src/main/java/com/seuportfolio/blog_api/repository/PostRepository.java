package com.seuportfolio.blog_api.repository;

import com.seuportfolio.blog_api.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, String> {
}
