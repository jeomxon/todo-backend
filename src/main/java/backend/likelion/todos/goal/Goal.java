package backend.likelion.todos.goal;

import backend.likelion.todos.common.ForbiddenException;
import backend.likelion.todos.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Goal(String name, String color, Member member) {
        this.name = name;
        this.color = color;
        this.member = member;
    }

    public void validateMember(Member member) {
        if (!this.member.equals(member)) {
            throw new ForbiddenException("해당 목표에 대한 권한이 없습니다.");
        }
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
