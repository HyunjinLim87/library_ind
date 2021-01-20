package library;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Rental_table")
public class Rental {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;        // 예약번호
    private Long memberId;  // 사용자번호
    private Long bookId;    // 책번호
    private String reqState;//요청: "reserve", "cancel", "rental", "return"

    @PostPersist
    public void onPostPersist(){
        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        reserved.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
        library.external.Payment payment = new library.external.Payment();
        // mappings goes here
        payment.setId(this.id);
        payment.setMemberId(this.memberId);
        payment.setBookId(this.bookId);
        //payment.setId(this.getId());
        //payment.setMemberId(this.getMemberId());
        //payment.setBookId(this.getBookId());
        payment.setReqState("reserve");

        RentalApplication.applicationContext.getBean(library.external.PaymentService.class)
            .pay(payment);
    }

    @PostUpdate
    public void onPostUpdate(){
        //this.getReqState()로 가져와도 됨
        if (this.reqState.equals("cancel") ) {
            Cancelled cancelled = new Cancelled();
            BeanUtils.copyProperties(this, cancelled);
            cancelled.publishAfterCommit();

            // Req/Res 방식으로 수정
            library.external.Payment payment = new library.external.Payment();
            payment.setId(this.getId());
            payment.setMemberId(this.getMemberId());
            payment.setBookId(this.getBookId());
            payment.setReqState(this.getReqState()); // 수정해야할 지 판단필요함
            RentalApplication.applicationContext.getBean(library.external.PaymentService.class)
                    .pay(payment);

            System.out.println("########## canceled" + cancelled.toJson());

        }  else if (this.reqState.equals("rental") ) {
            Rentaled rentaled = new Rentaled();
            BeanUtils.copyProperties(this, rentaled);
            rentaled.publishAfterCommit();
            System.out.println("########## rentaled" + rentaled.toJson());
        }  else if (this.reqState.equals("return") ) {
            Returned returned = new Returned();
            BeanUtils.copyProperties(this, returned);
            returned.publishAfterCommit();
            System.out.println("########## returned" + returned.toJson());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public String getReqState() {
        return reqState;
    }

    public void setReqState(String reqState) {
        this.reqState = reqState;
    }

}
