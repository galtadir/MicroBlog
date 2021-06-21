package com.example.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class PostController {

    private PostDBManager dbManager = PostDBManager.getPostManger();
    private final AtomicLong counter = new AtomicLong(dbManager.getNextId());

    @PostMapping("/post")
    public Post createPost(@RequestBody CreateRequest params){
        Post newPost =  new Post(counter.incrementAndGet(),params.getText());
        dbManager.AddPost(newPost);
        return newPost;
    }

    @PatchMapping("/post/{id}")
    public Post updatePost(@PathVariable Long id,@RequestBody UpdateRequest params){
        Post post = dbManager.updateText(id,params.getText());
        if(post==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return post;
    }

    @DeleteMapping("/post/{id}")
    public void deletePost(@PathVariable Long id){
        boolean deleted = dbManager.deletePost(id);
        if(deleted){
            return;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
    }

    @GetMapping("/post/{id}")
    public Post getPost(@PathVariable Long id){
        Post post = dbManager.getPost(id);
        if(post!=null){
            return post;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
    }

    @PostMapping("/post/{id}/like")
    public Post likePost(@PathVariable Long id){
        Post post = dbManager.likePost(id);
        if(post!=null){
            return post;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
    }


    @GetMapping("/post/trending/{n}")
    public List<Post> getPost(@PathVariable int n){
        return dbManager.getMostRelevant(n);
    }


}
