package techdailynews.com.technewsjavaapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import techdailynews.com.technewsjavaapi.model.Post;
import techdailynews.com.technewsjavaapi.model.User;
import techdailynews.com.technewsjavaapi.repository.UserRepository;
import techdailynews.com.technewsjavaapi.repository.VoteRepository;


import java.util.List;

@RestController //for JSON/XML responses
public class UserController {

    @Autowired // Only make an instance of this object as needed, improves efficiency
    UserRepository repository;

    @Autowired
    VoteRepository voteRepository;

    @GetMapping("/api/users") //Get all users end point
    //Remember: ALL methods without the VOID keyword must have a RETURN type
    public List<User> getAllUsers() {
        // Set the Return type to List, because we want a List of users
        //Assign the list of users to the userList variable
        List<User> userList = repository.findAll(); // findAll() is inherited off the CRUD Repository Object
        // for each u (user) in userList
        for (User u : userList) {
            //get their posts
            List<Post> postList = u.getPosts();
            //for each post in postList
            for (Post p : postList) {
                //set the post voteCount by
                //      tally up the number of votes by Post id from the VoteRepository
                //                       Get the postId by invoking the method off the Post model ''post.getId()''
                p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
                // Remember the voteCount is not persisted to the db, so you have to query the db to tally the votes
            }
        }
        //Return the userList data
        return userList;
    }

    @GetMapping("/api/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        User returnUser = repository.getById(id); //getById is inherited from the User JPA Repository
        List<Post> postList = returnUser.getPosts(); // getPosts is off the user model
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
        }

        return returnUser;
    }

    @PostMapping("/api/users") //create a user
    public User addUser(@RequestBody User user) {
        // Encrypt password
        //setPassword/getPassword is off the user model
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        repository.save(user); //save is off the JPA repository
        return user;
    }

    @PutMapping("/api/users/{id}")
    //@RequestBody annotation - maps the req body to a transfer object then a Java object
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        User tempUser = repository.getById(id);

        if (!tempUser.equals(null)) { // if there is a user with this id
            user.setId(tempUser.getId()); // user pertains to the req body for this User
            repository.save(user);
        }
        return user;
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) //nothing to return so send the status
    public void deleteUser(@PathVariable int id) { //void deleteUser method
        repository.deleteById(id);
    }
}
