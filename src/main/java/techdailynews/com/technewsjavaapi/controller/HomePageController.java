package techdailynews.com.technewsjavaapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import techdailynews.com.technewsjavaapi.model.Comment;
import techdailynews.com.technewsjavaapi.model.Post;
import techdailynews.com.technewsjavaapi.model.User;
import techdailynews.com.technewsjavaapi.repository.CommentRepository;
import techdailynews.com.technewsjavaapi.repository.PostRepository;
import techdailynews.com.technewsjavaapi.repository.UserRepository;
import techdailynews.com.technewsjavaapi.repository.VoteRepository;

import java.util.List;


//Include the repositories we created earlier and map them to objects that will be instantiated
// in this class when necessary - @Autowired.
@Controller
public class HomePageController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    CommentRepository commentRepository;

    @GetMapping("/login") //get the login view
    //the Model object is inherited functionality from Spring ui, model is the mapped variable
    public String login(Model model, HttpServletRequest request) {

        if (request.getSession(false) != null) {
            return "redirect:/";
        }
        //addAttribute is a function off the Model object
        // sends a new User to the template as a string "user"
        model.addAttribute("user", new User());
        return "login";
    }

    //When the logout route is hit, we check whether the session exists.
    // If it does, we'll invalidate the session, subsequently logging out the user.
    // We then redirect the user back to the login route.
    @GetMapping("/users/logout")
    public String logout(HttpServletRequest request) {
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
        return "redirect:/login";
    }

    @GetMapping("/")
    public String homepageSetup(Model model, HttpServletRequest request) {
        User sessionUser = new User();

        // if user is loggedIn
        if (request.getSession(false) != null) {
            sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
        } else { // if not
            model.addAttribute("loggedIn", false);
        }


        List<Post> postList = postRepository.findAll();
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
            User user = userRepository.getById(p.getUserId());
            p.setUserName(user.getUsername());
        }

        model.addAttribute("postList", postList);
        model.addAttribute("loggedIn", sessionUser.isLoggedIn());

        // "point" and "points" attributes refer to upvotes.
        model.addAttribute("point", "point");
        model.addAttribute("points", "points");

        return "homepage";
    }


    @GetMapping("/dashboard")
    public String dashboardPageSetup(Model model, HttpServletRequest request) throws Exception {

        if (request.getSession(false) != null) {
            setupDashboardPage(model, request);
            return "dashboard";
        } else {
            model.addAttribute("user", new User());
            return "login";
        }
    }

    @GetMapping("/dashboardEmptyTitleAndLink") //form submit notice
    public String dashboardEmptyTitleAndLinkHandler(Model model, HttpServletRequest request) throws Exception {
        setupDashboardPage(model, request);
        model.addAttribute("notice", "To create a post the Title and Link must be populated!");
        return "dashboard";
    }


    @GetMapping("/singlePostEmptyComment/{id}") //form submit notice
    public String singlePostEmptyCommentHandler(@PathVariable int id, Model model, HttpServletRequest request) {
        setupSinglePostPage(id, model, request);
        model.addAttribute("notice", "To add a comment you must enter the comment in the comment text area!");
        return "single-post";
    }


    @GetMapping("/post/{id}")
    public String singlePostPageSetup(@PathVariable int id, Model model, HttpServletRequest request) {
        setupSinglePostPage(id, model, request);
        return "single-post";
    }


    @GetMapping("/editPostEmptyComment/{id}")
    public String editPostEmptyCommentHandler(@PathVariable int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            setupEditPostPage(id, model, request);
            model.addAttribute("notice", "To add a comment you must enter the comment in the comment text area!");
            return "edit-post";
        } else {
            model.addAttribute("user", new User());
            return "login";
        }
    }


    @GetMapping("/dashboard/edit/{id}")
    public String editPostPageSetup(@PathVariable int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            setupEditPostPage(id, model, request);
            return "edit-post";
        } else {
            model.addAttribute("user", new User());
            return "login";
        }
    }


    // Create the data for the Dashboard View
    public Model setupDashboardPage(Model model, HttpServletRequest request) throws Exception {
        //Assign the value of the current user, via SESSION_USER, to a variable called sessionUser
        User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

        Integer userId = sessionUser.getId();
        // find all posts by this user id
        List<Post> postList = postRepository.findAllPostsByUserId(userId);
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
            // for each post in postList set the username to this users username
            User user = userRepository.getById(p.getUserId());
            p.setUserName(user.getUsername());
        }
        //Pass information into the Thymeleaf pages when that template is called
        //addAttribute() calls on Spring's model object
        model.addAttribute("user", sessionUser);
        model.addAttribute("postList", postList);
        model.addAttribute("loggedIn", sessionUser.isLoggedIn());
        model.addAttribute("post", new Post());

        return model;
    }

    //Set up single post view. This view takes in the post id, a ui, and a request
    public Model setupSinglePostPage(int id, Model model, HttpServletRequest request) {
        //if there is a request, and an open session?..
        if (request.getSession(false) != null) {
            //get the users "session user" attribute off the request
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            //set loggedIn to true for the view, from the user session
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
        }
        //get post by id and set the voteCount
        Post post = postRepository.getById(id);
        post.setVoteCount(voteRepository.countVotesByPostId(post.getId()));
        //get the userId off the post repository
        User postUser = userRepository.getById(post.getUserId());
        //set the post username to users username
        post.setUserName(postUser.getUsername());

        // get all comments for this post from the comment repository
        List<Comment> commentList = commentRepository.findCommentsByPostId(post.getId());

        model.addAttribute("post", post);

        model.addAttribute("commentList", commentList);
        model.addAttribute("comment", new Comment());

        return model;
    }


    public Model setupEditPostPage(int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

            Post returnPost = postRepository.getById(id);
            User tempUser = userRepository.getById(returnPost.getUserId());
            returnPost.setUserName(tempUser.getUsername());
            returnPost.setVoteCount(voteRepository.countVotesByPostId(returnPost.getId()));

            List<Comment> commentList = commentRepository.findCommentsByPostId(returnPost.getId());

            model.addAttribute("post", returnPost);
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
            model.addAttribute("commentList", commentList);
            model.addAttribute("comment", new Comment());
        }

        return model;
    }
}
