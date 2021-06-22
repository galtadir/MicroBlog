package com.example.restservice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

//single tone class
class PostDBManager {
    private static PostDBManager postDBManager;
    private LocalDate lastRankUpdate;
    private Connection con;

    private PostDBManager(){
        lastRankUpdate = java.time.LocalDate.now();

        String url="jdbc:mysql://localhost:3306/posts-db";
        String user="root";
        String  password="1234";

//        String url=System.getenv("MYSQL_URL");
//        String user=System.getenv("MYSQL_USER");
//        String  password=System.getenv("MYSQL_PASSWORD");
//        System.out.println(url);
//        System.out.println(user);
//        System.out.println(password);

//        String url="jdbc:mysql://appN_db:3307/java_to_dev_app_db";
//        String user="java_to_dev";
//        String  password= "nE5kMc7JCGNqwDQM";


        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url,user,password);
            createPostsTable();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    boolean createPostsTable(){
        try {
            String query = "CREATE TABLE IF NOT EXISTS posts (`id` Long NOT NULL, `text` VARCHAR(255) NOT NULL, `likes` INTEGER NOT NULL, date DATE NOT NULL, `rank` Double NOT NULL)";
            PreparedStatement st = con.prepareStatement(query);
            st.executeUpdate();
            return true;
        }catch (Exception e){
            return false;
        }

    }

    static PostDBManager getPostManger(){
        if(postDBManager==null){
            postDBManager = new PostDBManager();
        }
        return postDBManager;
    }

    //insert post to the db
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

    //update text for specific post
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

    //remove post from the db
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

    //return post from the db
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

    //add like to post
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

    //return top n trending posts
    List<Post> getMostRelevant(int n){
        //if the rank of all posts not update today then update them all
        if(DAYS.between(lastRankUpdate, java.time.LocalDate.now())!=0){
            //the rank will be the amount of likes minus 10% foreach day
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
            //return the most trending post in corresponded order
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

    //check the last id updated in db and return him
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
