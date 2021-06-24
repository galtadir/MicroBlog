package com.example.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class PostController {

    //get DB Manager as single tone
    private PostDBManager dbManager = PostDBManager.getPostManger();
    // responsible to assign unique id
    private final AtomicLong counter = new AtomicLong(dbManager.getNextId());

    // create new post
    @PostMapping("/post")
    public Post createPost(@RequestBody CreateRequest params){
        Post newPost =  new Post(counter.incrementAndGet(),params.getText());
        if(dbManager.AddPost(newPost)){
            return newPost;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // change the text of specific post
    @PatchMapping("/post/{id}")
    public Post updatePost(@PathVariable Long id,@RequestBody UpdateRequest params){
        Post post = dbManager.updateText(id,params.getText());
        if(post==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return post;
    }

    // delete specific post by id
    @DeleteMapping("/post/{id}")
    public void deletePost(@PathVariable Long id){
        boolean deleted = dbManager.deletePost(id);
        if(deleted){
            return;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
    }

    //return specific post by id
    @GetMapping("/post/{id}")
    public Post getPost(@PathVariable Long id){
        Post post = dbManager.getPost(id);
        if(post!=null){
            return post;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
    }

    //add like to post
    @PostMapping("/post/{id}/like")
    public Post likePost(@PathVariable Long id){
        Post post = dbManager.likePost(id);
        if(post!=null){
            return post;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
    }

    //return top n trending posts
    @GetMapping("/post/trending/{n}")
    public List<Post> getPost(@PathVariable int n){
        return dbManager.getMostRelevant(n);
    }


}
