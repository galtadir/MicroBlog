package com.example.restservice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

class PostDBManager {
    private HashMap<Long,Post> allPost;
    private static PostDBManager postDBManager;
    private LocalDate lastRankUpdate;

    private PostDBManager(){
        allPost = new HashMap<>();
        lastRankUpdate = java.time.LocalDate.now();
    }

    static PostDBManager getPostManger(){
        if(postDBManager==null){
            postDBManager = new PostDBManager();
        }
        return postDBManager;
    }

    void AddPost(Post post){
        allPost.put(post.getId(),post);
    }

    Post updateText(Long id, String text){
        if(allPost.containsKey(id)){
            Post post = allPost.get(id);
            post.setText(text);
            return post;
        }
        return null;
    }

    boolean deletePost(long id){
        if(allPost.containsKey(id)){
            allPost.remove(id);
            return true;
        }
        return false;
    }

    Post getPost(long id){
        if(allPost.containsKey(id)){
            return allPost.get(id);
        }
        return null;
    }

    Post likePost(long id){
        if(allPost.containsKey(id)){
            Post post = allPost.get(id);
            post.addLike();
            return post;
        }
        return null;
    }

    List<Post> getMostRelevant(int n){
        if(DAYS.between(lastRankUpdate, java.time.LocalDate.now())!=0){
            for(Long id: allPost.keySet()){
                allPost.get(id).updateRank();
            }
            lastRankUpdate = java.time.LocalDate.now();
        }
        List<Post> mapValues = new ArrayList(allPost.values());
        Collections.sort(mapValues);
        return mapValues.subList(0,n);

    }







}
