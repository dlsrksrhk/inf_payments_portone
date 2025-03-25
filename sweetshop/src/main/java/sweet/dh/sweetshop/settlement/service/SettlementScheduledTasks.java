package sweet.dh.sweetshop.settlement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sweet.dh.sweetshop.payment.entity.Payment;
import sweet.dh.sweetshop.payment.repository.PaymentRepository;
import sweet.dh.sweetshop.settlement.entity.Settlement;
import sweet.dh.sweetshop.settlement.repository.SettlementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class SettlementScheduledTasks {
    public static final String PAYMENT_COMPLETED = "paid";
    private final PaymentRepository paymentRepository;

    private final SettlementRepository settlementRepository;

    //1분마다
    @Scheduled(cron = "0 * * * * ?")
    @SchedulerLock(name = "ScheduledTask_run")
    public void dailySettlement() {
        // 어제의 날짜를 가져옴
        LocalDate yesterday = LocalDate.now().minusDays(1);
        // 어제의 시작 시각 설정 (2024-10-26 00:00:00)
        LocalDateTime startDate = yesterday.atStartOfDay();
        // 어제의 끝 시각 설정 (2024-10-26 23:59:59)
        LocalDateTime endDate = yesterday.atTime(LocalTime.of(23, 59, 59));

        // 해당 기간 동안의 결제 내역 조회 및 집계
        Map<Long, BigDecimal> settlementMap = getSettlementMap(startDate, endDate);

        processSettlements(settlementMap, yesterday);
    }

    private Map<Long, BigDecimal> getSettlementMap(LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> paymentList = paymentRepository.findByPaymentDateBetweenAndStatus(startDate, endDate, PAYMENT_COMPLETED);
        // partner_id를 기준으로 group by
        return paymentList.stream()
                .collect(Collectors.groupingBy(
                        Payment::getPartnerId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Payment::getPaymentAmount,
                                BigDecimal::add
                        )
                ));
    }

    private void processSettlements(Map<Long, BigDecimal> settlementMap, LocalDate paymentDate) {
        //공유풀을 사용하지 않고 별도의 스레드풀을 만들어 병렬스트림 수행
        //Runtime.getRuntime().availableProcessors() 대신에 직접 스레드 수 조절 가능
        ForkJoinPool customForkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        try {
            customForkJoinPool.submit(() ->
                    //결제내역 일별 그룹핑 후 집계
                    settlementMap.entrySet().parallelStream()
                            .map(entry -> Settlement.create(entry.getKey(), entry.getValue(), paymentDate))
                            .forEach(settlementRepository::save)
            ).get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customForkJoinPool.shutdown();
        }
    }
}