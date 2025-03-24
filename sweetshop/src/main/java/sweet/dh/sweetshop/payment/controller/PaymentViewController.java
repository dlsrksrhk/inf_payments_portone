package sweet.dh.sweetshop.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentViewController {
    @GetMapping
    public String payment() {
        return "payment";
    }

}
