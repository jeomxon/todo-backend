package backend.likelion.todos.goal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.likelion.todos.common.ForbiddenException;
import backend.likelion.todos.common.NotFoundException;
import backend.likelion.todos.member.Member;
import backend.likelion.todos.member.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("목표 서비스 (GoalService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalService goalService;

    private Member member;
    private Member other;

    @BeforeEach
    void setUp() {
        goalRepository.clear();
        memberRepository.clear();
        member = memberRepository.save(new Member("1", "1", "1", "1"));
        other = memberRepository.save(new Member("2", "2", "2", "2"));
    }

    @Nested
    class 목표_생성_시 {

        @Test
        void 회원_정보가_없으면_예외() {
            // when & then
            assertThatThrownBy(() -> {
                goalService.save("목표", "#111111", member.getId() + 100L);
            }).isInstanceOf(NotFoundException.class)
                    .hasMessage("회원 정보가 없습니다.");
        }

        @Test
        void 목표를_생성한다() {
            // when
            Long goalId = goalService.save("목표", "#111111", member.getId());

            // then
            assertThat(goalId).isNotNull();
            assertThat(goalRepository.findById(goalId)).isPresent();
        }
    }

    @Nested
    class 목표_수정_시 {

        @Test
        void 회원_정보가_없으면_예외() {
            // given
            Long goalId = goalService.save("목표", "#111111", member.getId());

            // when & then
            assertThatThrownBy(() -> {
                goalService.update(goalId, "u", "c", member.getId() + 100L);
            }).isInstanceOf(NotFoundException.class)
                    .hasMessage("회원 정보가 없습니다.");
        }

        @Test
        void 목표가_없으면_예외() {
            // given
            Long goalId = goalService.save("목표", "#111111", member.getId());

            // when & then
            assertThatThrownBy(() -> {
                goalService.update(goalId + 100L, "u", "c", member.getId());
            }).isInstanceOf(NotFoundException.class)
                    .hasMessage("목표 정보가 없습니다.");
        }

        @Test
        void 목표를_생성한_회원이_아닌_다른_회원이_수정하려는_경우_예외() {
            // given
            Long goalId = goalService.save("목표", "#111111", member.getId());

            // when & then
            assertThatThrownBy(() -> {
                goalService.update(goalId, "u", "c", other.getId());
            }).isInstanceOf(ForbiddenException.class)
                    .hasMessage("해당 목표에 대한 권한이 없습니다.");
        }

        @Test
        void 수정_성공() {
            // given
            Long goalId = goalService.save("목표", "#111111", member.getId());

            // when
            goalService.update(goalId, "u", "c", member.getId());

            // then
            Goal goal = goalRepository.findById(goalId).get();
            assertThat(goal.getName()).isEqualTo("u");
            assertThat(goal.getColor()).isEqualTo("c");
        }
    }

    @Nested
    class 목표_삭제_시 {

        @Test
        void 회원_정보가_없으면_예외() {
            // given
            Long goalId = goalService.save("목표", "#111111", member.getId());

            // when & then
            assertThatThrownBy(() -> {
                goalService.delete(goalId, member.getId() + 100L);
            }).isInstanceOf(NotFoundException.class)
                    .hasMessage("회원 정보가 없습니다.");
        }

        @Test
        void 목표가_없으면_예외() {
            // given
            Long goalId = goalService.save("목표", "#111111", member.getId());

            // when & then
            assertThatThrownBy(() -> {
                goalService.delete(goalId + 100L, member.getId());
            }).isInstanceOf(NotFoundException.class)
                    .hasMessage("목표 정보가 없습니다.");
        }

        @Test
        void 목표를_생성한_회원이_아닌_다른_회원이_삭제하려는_경우_예외() {
            // given
            Long goalId = goalService.save("목표", "#111111", member.getId());

            // when & then
            assertThatThrownBy(() -> {
                goalService.delete(goalId, other.getId());
            }).isInstanceOf(ForbiddenException.class)
                    .hasMessage("해당 목표에 대한 권한이 없습니다.");
        }

        @Test
        void 삭제_성공() {
            // given
            Long goalId = goalService.save("목표", "#111111", member.getId());

            // when
            goalService.delete(goalId, member.getId());

            // then
            assertThat(goalRepository.findById(goalId)).isEmpty();
        }
    }

    @Nested
    class 특정_회원의_목표_전체_조회_시 {

        @Test
        void 성공() {
            // given
            Long goalId1 = goalService.save("목표1", "#111111", member.getId());
            Long goalId2 = goalService.save("목표2", "#111111", other.getId());
            Long goalId3 = goalService.save("목표3", "#111111", member.getId());

            // when
            List<GoalResponse> response = goalService.findAllByMemberId(member.getId());

            // then
            assertThat(response)
                    .hasSize(2)
                    .extracting(GoalResponse::getName)
                    .containsExactlyInAnyOrder("목표1", "목표3");
        }
    }
}
