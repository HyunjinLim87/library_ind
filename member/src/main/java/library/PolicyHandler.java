package library;

import library.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    MemberRepository memberRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_(@Payload Paid paid){
        //결제완료(결제완료->예약됨)
        if(paid.isMe()){
            System.out.println("##### listener 결제완료->예약 : " + paid.toJson());
            //예약 되었을 때에, member의 rentalId, rentalStatus 갱신
            Optional<Member> memberOptional = memberRepository.findById(paid.getMemberId());

            if ( memberOptional.isPresent() ) {
                Member member = memberOptional.get();
                //member.setMemberId(paid.getMemberId());
                member.setRentalId(paid.getId());
                member.setRentalStatus("reserved");
                //member Entity 저장
                memberRepository.save(member);

            } else {
                System.out.println("Can't find member ID !! : " + paid.getMemberId());
            }

        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRefunded_(@Payload Refunded refunded){
        //예약취소(환불)
        if(refunded.isMe()){
            System.out.println("##### listener 예약취소(환불) : " + refunded.toJson());
            //예약취소>환불 처리 되었을 때에 member의 rentalStatus 갱신
            Optional<Member> memberOptional = memberRepository.findById(refunded.getMemberId());

            if ( memberOptional.isPresent() ) {
                Member member = memberOptional.get();
                //member.setMemberId(paid.getMemberId());
                //member.setRentalId(refunded.getId());
                member.setRentalStatus("refunded");
                //member Entity 저장
                memberRepository.save(member);

            } else {
                System.out.println("Can't find member ID !! : " + refunded.getMemberId());
            }

        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRentaled_(@Payload Rentaled rentaled){
        //대여상태
        if(rentaled.isMe()){
            System.out.println("##### listener 대여 : " + rentaled.toJson());

            Optional<Member> memberOptional = memberRepository.findById(rentaled.getMemberId());

            if ( memberOptional.isPresent() ) {
                Member member = memberOptional.get();
                //member.setMemberId(paid.getMemberId());
                //member.setRentalId(refunded.getId());
                member.setRentalStatus("rentaled");
                //member Entity 저장
                memberRepository.save(member);

            } else {
                System.out.println("Can't find member ID !! : " + rentaled.getMemberId());
            }
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReturned_(@Payload Returned returned){
        //반납상태
        if(returned.isMe()){
            System.out.println("##### listener 반납 : " + returned.toJson());

            Optional<Member> memberOptional = memberRepository.findById(returned.getMemberId());

            if ( memberOptional.isPresent() ) {
                Member member = memberOptional.get();
                //member.setMemberId(paid.getMemberId());
                //member.setRentalId(refunded.getId());
                member.setRentalStatus("returned");
                //member Entity 저장
                memberRepository.save(member);

            } else {
                System.out.println("Can't find member ID !! : " + returned.getMemberId());
            }
        }
    }

}
