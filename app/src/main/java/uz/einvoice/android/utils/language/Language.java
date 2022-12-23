package uz.einvoice.android.utils.language;
public enum Language {
    UZ("uz"),
    RU("ru"),
    EN("en") ;

    private String value;


    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}

