package sweet.dh.sweetshop.settlement.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;


@Rollback(false)
@SpringBootTest
class SettlementScheduledTasksTest {

    @Autowired
    private SettlementScheduledTasks settlementScheduledTasks;

    @Test
    void taskTimeTest() {
        long startTime = System.currentTimeMillis();

        settlementScheduledTasks.dailySettlement();

        long endTime = System.currentTimeMillis();
        System.out.println("실행 시간 : " + (endTime - startTime) + "ms");
    }

}