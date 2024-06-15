package com.project.watermelon.dto.payment;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.vo.PaymentVo;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PaymentResponseDto extends CommonBackendResponseDto<PaymentVo> {
    public PaymentResponseDto(PaymentVo response){
        super.setData(response);
    }
}
