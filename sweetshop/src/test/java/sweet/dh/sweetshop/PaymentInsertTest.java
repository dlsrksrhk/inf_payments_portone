package sweet.dh.sweetshop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@Transactional
@Rollback(false)
public class PaymentInsertTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String[] cardNames = {"삼성카드", "현대카드", "국민카드", "신한카드", "롯데카드", "하나카드", "우리카드", "KEB하나카드"};
    private final String[] pgProviders = {"kakaopay", "tosspay", "inicis", "nice"};
    private final String[] statuses = {"paid", "cancel"};
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void insertDummyPayments() {
        for (int i = 0; i < 2000; i++) {
            Long partnerId = ThreadLocalRandom.current().nextLong(10000, 10500);
            long userId = ThreadLocalRandom.current().nextLong(10000, 99999);
            long orderId = ThreadLocalRandom.current().nextLong(100000, 999999);
            double paymentAmount = Math.round(ThreadLocalRandom.current().nextDouble(1000, 300000) * 100.0) / 100.0;

            LocalDateTime paymentDate = getRandomMarchDateTime();
            LocalDateTime updatedAt = paymentDate.plusHours(ThreadLocalRandom.current().nextInt(0, 72));

            String sql = """
                INSERT INTO payments (
                    partner_id, user_id, order_id, payment_amount, payment_date, imp_uid,
                    payment_method, merchant_uid, pg_provider, pg_type, pg_tid, status,
                    card_name, card_number, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            jdbcTemplate.update(sql,
                    partnerId,
                    userId,
                    orderId,
                    paymentAmount,
                    formatter.format(paymentDate),
                    "imp_" + ThreadLocalRandom.current().nextLong(100000000000L, 999999999999L),
                    "card",
                    "payment-" + UUID.randomUUID(),
                    pgProviders[ThreadLocalRandom.current().nextInt(pgProviders.length)],
                    "payment",
                    UUID.randomUUID().toString(),
                    statuses[ThreadLocalRandom.current().nextInt(statuses.length)],
                    cardNames[ThreadLocalRandom.current().nextInt(cardNames.length)],
                    ThreadLocalRandom.current().nextInt(400000, 599999) + "******" + ThreadLocalRandom.current().nextInt(100, 999) + "*",
                    formatter.format(paymentDate),
                    formatter.format(updatedAt)
            );
        }
    }

    private LocalDateTime getRandomMarchDateTime() {
        int day = ThreadLocalRandom.current().nextInt(1, 32);
        int hour = ThreadLocalRandom.current().nextInt(0, 24);
        int minute = ThreadLocalRandom.current().nextInt(0, 60);
        int second = ThreadLocalRandom.current().nextInt(0, 60);
//        return LocalDateTime.of(2025, 3, day, hour, minute, second);
        return LocalDateTime.now()
                .minusDays(1)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second);
    }
}
