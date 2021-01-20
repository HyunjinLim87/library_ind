
package library.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

//@FeignClient(name="payment", url="http://payment:8080")
@FeignClient(name="payment", url="${api.payment.url}")
public interface PaymentService {

    @RequestMapping(method= RequestMethod.POST, path="/payments")
    public void pay(@RequestBody Payment payment);

    // 렌탈 취소되어야 환불되어야 함으로 추가
    //@RequestMapping(method= RequestMethod.PATCH, path="/refunds")
    //public void refund(@RequestBody Payment payment);
    //@RequestMapping(method= RequestMethod.PUT, path="/refunds")
    //public void refund(@RequestBody Payment payment);

}
