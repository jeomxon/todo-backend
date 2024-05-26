package backend.likelion.todos.goal;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findAllByMemberId(Long memberId);
}
