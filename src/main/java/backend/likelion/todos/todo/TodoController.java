package backend.likelion.todos.todo;

import backend.likelion.todos.auth.Auth;
import java.net.URI;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/todos")
@RestController
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @RequestBody TodoCreateRequest request
    ) {
        final Long toDoId = todoService.save(request.goalId(), memberId, request.content(), request.date());
        return ResponseEntity.created(URI.create("/todos/" + toDoId)).build();
    }

    @PostMapping("/{id}/check")
    public void check(
            @Auth Long memberId,
            @PathVariable("id") Long todoId
    ) {
        todoService.check(todoId, memberId);
    }

    @PostMapping("/{id}/uncheck")
    public void uncheck(
            @Auth Long memberId,
            @PathVariable("id") Long todoId
    ) {
        todoService.uncheck(todoId, memberId);
    }

    @PutMapping("/{id}")
    public void update(
            @Auth Long memberId,
            @PathVariable("id") Long todoId,
            @RequestBody TodoUpdateRequest request
    ) {
        todoService.update(todoId, memberId, request.content(), request.date());
    }

    @DeleteMapping("/{id}")
    public void delete(
            @Auth Long memberId,
            @PathVariable("id") Long todoId
    ) {
        todoService.delete(todoId, memberId);
    }

    @GetMapping("/my")
    public ResponseEntity<List<TodoWithDayResponse>> findMine(
            @Auth Long memberId,
            @RequestParam(value = "year", required = true) int year,
            @RequestParam(value = "month", required = true) int month
    ) {
        final List<TodoWithDayResponse> todoWithDayResponses =
                todoService.findAllByMemberIdAndDate(memberId, YearMonth.of(year, month));
        return ResponseEntity.ok(todoWithDayResponses);
    }
}
