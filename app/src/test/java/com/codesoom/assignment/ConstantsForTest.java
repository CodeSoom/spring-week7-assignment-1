package com.codesoom.assignment;

import java.util.List;

public class ConstantsForTest {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String VALID_TOKEN
            = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ze4dJmmF4peSe1uo9-ug019VAwzhr0WO8H3iHroSOeM";
    public static final List<String> INVALID_TOKENS = List.of(
            TOKEN_PREFIX + ""
            , TOKEN_PREFIX + " "
            , TOKEN_PREFIX + "112345678904162398746928372829829"
            , TOKEN_PREFIX + "eyJhbGciOiJIUzI1NiJ9"
            , TOKEN_PREFIX + "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9"
            , TOKEN_PREFIX + "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ze4dJmmF4peSe1uo9-ug019VAwzhr0WO8H3iHroSOUi"
            , TOKEN_PREFIX + "한글로토큰이만들어지지않습니다"
            , TOKEN_PREFIX + "한글로.토큰이.만들어지지않습니다"
            , TOKEN_PREFIX + "eyJhbGciOiJIUzI1NiJ9eyJhbGciOiJIUzI1NiJ9eyJhbGciOiJIUzI1NiJdlksiei34828.398274198230dlkj" +
                    "seif21932432.9348723riosw;fkajdslkfi8w03249283lkkdsdflkjsielskd"
            , TOKEN_PREFIX + "1!@#$@#$%&^%$#$23398282#$@#%@%@##@$.3987432$@#$290233.@#$*&@#)(*$O)@(#"
    );

}
