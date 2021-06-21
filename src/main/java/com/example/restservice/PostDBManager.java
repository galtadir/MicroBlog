package com.example.restservice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

class PostDBManager {
    private static PostDBManager postDBManager;
    private LocalDate lastRankUpdate;
    private Connection con;

    private PostDBManager(){
        lastRankUpdate = java.time.LocalDate.now();

        String url="jdbc:mysql://localhost:3306/posts-db";
        String user="root";
        String  password="1234";

        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url,user,password);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    static PostDBManager getPostManger(){
        if(postDBManager==null){
            postDBManager = new PostDBManager();
        }
        return postDBManager;
    }

    boolean AddPost(Post post){
        boolean insert= false;
        try {
            String query = "INSERT INTO posts values (?,?,?,?,?)";
            PreparedStatement st = con.prepareStatement(query);
            st.setLong(1, post.getId());
            st.setString(2, post.getText());
            st.setInt(3, post.getLikes());
            st.setDate(4, Date.valueOf(post.getDate()));
            st.setDouble(5,post.getRank());
            int rows = st.executeUpdate();
            if (rows > 0) {
                insert = true;
            }
        }catch (Exception e){
            return false;
        }
        return insert;
    }

    Post updateText(Long id, String text){
        String query="UPDATE posts SET text = (?) WHERE id=(?)";
        try {
            PreparedStatement st= con.prepareStatement(query);
            st.setString(1,text);
            st.setLong(2,id);
            st.executeUpdate();
        }catch (Exception e){
            return null;

        }
        return getPost(id);
    }

    boolean deletePost(long id){

        if(getPost(id)==null){
            return false;
        }
        try {
            String query = "DELETE FROM posts WHERE id =" + id;
            PreparedStatement st = con.prepareStatement(query);
            st.executeUpdate();
            return true;
        }
        catch (Exception e){
            return false;
        }

    }

    Post getPost(long id){
        Post post = null;
        try {
            String query = "SELECT * FROM  posts WHERE id=" + id;
            PreparedStatement st = con.prepareStatement(query);
            ResultSet rows = st.executeQuery();
            if (rows.next()) {
                post = new Post(rows.getInt("id"),rows.getString("text"),rows.getInt("likes"),rows.getDate("date"),rows.getDouble("rank"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return  post;

    }

    Post likePost(long id){
        Post post = getPost(id);
        if(post==null){
            return null;
        }
        post.addLike();
        String query="UPDATE posts SET likes = " + post.getLikes() + ", `rank` = " + post.getRank() + " WHERE id = " + id;
        try {
            PreparedStatement st= con.prepareStatement(query);

            st.executeUpdate();
        }catch (Exception e){
            return null;
        }
        return getPost(id);


    }

    List<Post> getMostRelevant(int n){

        if(DAYS.between(lastRankUpdate, java.time.LocalDate.now())!=0){
            String query="UPDATE posts SET `rank` = likes*pow(0.9,DATEDIFF(`date`,(?)))";
            try {
                PreparedStatement st= con.prepareStatement(query);
                st.setDate(1, Date.valueOf(java.time.LocalDate.now()));
                st.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            lastRankUpdate = java.time.LocalDate.now();
        }
        try {
            String query = "SELECT * FROM  posts ORDER BY `rank` DESC LIMIT " + n;
            PreparedStatement st = con.prepareStatement(query);
            ResultSet rows = st.executeQuery();
            List<Post> mapValues = new ArrayList();
            while (rows.next()) {
                Post post = new Post(rows.getInt("id"),rows.getString("text"),rows.getInt("likes"),rows.getDate("date"),rows.getDouble("rank"));
                mapValues.add(post);

            }
            return mapValues;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    int getNextId(){

        try {
            int max = 0;
            String query = "SELECT MAX(id) AS maxid  FROM  posts";
            PreparedStatement st = con.prepareStatement(query);
            ResultSet rows = st.executeQuery();
            while (rows.next()){
                max = rows.getInt("maxid");
            }
            return max;

        }
        catch (Exception e){
            return 0;
        }
    }

}
