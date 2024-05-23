package com.project.watermelon.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class CustomPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {
    private static final long serialVersionUID = 1L;

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        // 테이블 이름을 모두 대문자로 변경
        return new Identifier(name.getText().toUpperCase(), name.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        // 컬럼 이름을 카멜케이스에서 스네이크 케이스로 변경한 후 모두 대문자로 변경
        String convertedName = convertCamelCaseToSnakeCase(name.getText()).toUpperCase();
        return new Identifier(convertedName, name.isQuoted());
    }

    // 카멜케이스를 스네이크 케이스로 변환
    private String convertCamelCaseToSnakeCase(String input) {
        if (input == null) return null;
        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                result.append('_').append(c);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
