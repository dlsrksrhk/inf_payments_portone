// 구매자 정보
const useremail = "sweet_test@gmail.com"
const username = "kdh"

// 결제창 함수 넣어주기
document.getElementById('payment-button').addEventListener("click", paymentProcess);

var IMP = window.IMP;
var isLogin = true;

function generateMerchantUid() {
    var today = new Date();
    var hours = today.getHours(); // 시
    var minutes = today.getMinutes();  // 분
    var seconds = today.getSeconds();  // 초
    var milliseconds = today.getMilliseconds(); // 밀리초

    // 시, 분, 초, 밀리초를 결합하여 고유한 문자열 생성
    var makeMerchantUid = `${hours}${minutes}${seconds}${milliseconds}`;

    return makeMerchantUid;
}


function paymentProcess() {
    if (confirm("구매 하시겠습니까?")) {
        if (isLogin) { // 회원만 결제 가능
            IMP.init("imp28156365"); // 가맹점 식별코드
            IMP.request_pay({
                channelKey: 'channel-key-63ce5626-047d-4824-a7eb-8cc9d665977c',
                ppay_method: "card",
                merchant_uid: `payment-${crypto.randomUUID()}`, // 주문 고유 번호
                name: "노르웨이 회전 의자",
                amount: 64900,
                buyer_email: "gildong@gmail.com",
                buyer_name: "홍길동",
                buyer_tel: "010-4242-4242",
                buyer_addr: "서울특별시 강남구 신사동",
                buyer_postcode: "01181",
            }, async (response) => {
                console.log(response)
                if (response.error_code != null) {
                    return alert(`결제에 실패하였습니다. 에러 내용: ${response.error_msg}`);
                }

                //결제 완료시 필요한 데이터 추가
                response.partnerId = 12345;  // partnerId 값 설정 (필요시 동적으로 가져올 수 있음)
                response.userId = 67890;     // userId 값 설정 (현재 로그인한 사용자 정보로 설정 가능)
                response.orderId = 112233;   // orderId 값 설정 (주문 관련 정보로 설정 가능)
                response.paymentDate = new Date().toISOString().split('T')[0];  // paymentDate를 현재 날짜로 설정 (yyyy-mm-dd 형식)
                const notified = await fetch(`/api/payment/portone`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    // imp_uid와 merchant_uid, 주문 정보를 서버에 전달합니다
                    body: JSON.stringify(response),
                });
            });
        } else { // 비회원 결제 불가
            alert('로그인이 필요합니다!')
        }
    } else { // 구매 확인 알림창 취소 클릭시 돌아가기
        return false;
    }
}