package com.example.restservice;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class PostController {

    private final AtomicLong counter = new AtomicLong();
    private PostDBManager dbManager = PostDBManager.getPostManger();

    @PostMapping("/post/create")
    public Post createPost(@RequestBody CreateRequest params){

        Post newPost =  new Post(counter.incrementAndGet(),params.getText());
        dbManager.AddPost(newPost);
        return newPost;
    }

    @PutMapping("/post/update")
    public Post updatePost(@RequestBody UpdateRequest params){
        return dbManager.updateText(params.getId(),params.getText());
    }

    @DeleteMapping("/post/delete/{id}")
    public boolean deletePost(@PathVariable Long id){
        return dbManager.deletePost(id);
    }

    @GetMapping("/post/read/{id}")
    public Post getPost(@PathVariable Long id){
        return dbManager.getPost(id);
    }

    @PutMapping("/post/like")
    public Post likePost(@RequestBody LikeRequest params){
        return dbManager.likePost(params.getId());
    }


    @GetMapping("/post/trending/{n}")
    public List<Post> getPost(@PathVariable int n){
        return dbManager.getMostRelevant(n);
    }


}
