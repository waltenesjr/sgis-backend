package br.com.oi.sgis.util;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.TechnicalStaffDTO;
import br.com.oi.sgis.entity.Level;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Set;

public class Utils {

    private static String userDepartment = "RJ-OI-ARC";
    private static String userID = "RJ127925";

    private Utils(){}

    public static TechnicalStaffDTO getUser(){
        return TechnicalStaffDTO.builder().id(userID)
                .levels(Set.of(Level.builder().id("ADMIN0").description("ADMINISTRADOR").lvl(0).build()))
                .departmentCode(DepartmentDTO.builder().id(userDepartment)
                        .build()).build();

//        return TechnicalStaffDTO.builder().id(userID)
//                .levels(Set.of(Level.builder().id("ADMIN0").description("Adminis").lvl(0).build()))
//                .departmentCode(DepartmentDTO.builder().id(userDepartment)
//                        .build()).build();

    }

    public static void isPeriodInvalid(LocalDateTime initial, LocalDateTime finalP){
        if (initial == null || finalP ==null)
            throw new IllegalArgumentException(MessageUtils.PERIOD_NULL.getDescription());

        Assert.isTrue(finalP.isAfter(initial), MessageUtils.INVALID_PERIOD.getDescription());
    }

    public static LocalDateTime onlyDate(LocalDateTime localDateTime){
        if(localDateTime != null)
            return localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        return null;
    }

    public static BigDecimal getNumberFromString(String term) {
        try{
            return new BigDecimal(term);
        }catch (NumberFormatException e){
            return null;
        }
    }

    public static boolean isEven(BigInteger number)
    {
        return number.getLowestSetBit() != 0;
    }


}
