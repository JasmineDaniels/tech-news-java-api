package techdailynews.com.technewsjavaapi.repository;

//Any clas that implements an Interface MUST use and define those methods

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import techdailynews.com.technewsjavaapi.model.User;

// A Java Repository is any class that fulfills the role of a data access object
// A (DAO) = data retrieval, storage, and search functionality*
//
//To make the methods available via inheritance, the interface repo must extend the
//JPARepository, so it will inherit the methods used to access the db for CRUD operations
@Repository
public interface Userrepository extends JpaRepository<User, Integer> {
    //custom query method
    User findUserByEmail(String email) throws Exception;
}
