package techdailynews.com.technewsjavaapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import techdailynews.com.technewsjavaapi.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    // this custom  method takes in 2 args, Post id & Vote id??
    // @Param is a method level annotation
    @Query("SELECT count(*) FROM Vote v where v.postId = :id")
    int countVotesByPostId(@Param("id") Integer id);
}
