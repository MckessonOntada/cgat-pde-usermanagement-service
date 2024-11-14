package com.mckesson.cgat.pde.usermanagement.model.converters;

import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import jakarta.persistence.AttributeConverter;

public class AccountPartnerFunctionConverter implements AttributeConverter<AccountPartnerFunction, String> {

    @Override
    public String convertToDatabaseColumn(AccountPartnerFunction accountPartnerFunction) {
        return accountPartnerFunction.getValue();
    }

    @Override
    public AccountPartnerFunction convertToEntityAttribute(String s) {
        return AccountPartnerFunction.valueFor(s);
    }
}
