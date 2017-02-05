package ua.com.newsblog.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@MappedSuperclass
public abstract class Model implements Serializable {

    private static final long serialVersionUID = 1L;

    protected static final char[] CODE_PATTERN = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    protected static final int CODE_LENGTH = 6;

    protected static final String DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";

    protected static final String TIME_ZONE = "GMT+2";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Model)) {
            return false;
        }
        Model other = (Model) obj;
        return (this.toEquals().equals(other.toEquals()));
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : toString().hashCode();
    }

    public String toEquals() {
        return toString();
    }

    public static String createRandomString() {
        return createRandomString(CODE_PATTERN, CODE_LENGTH);
    }

    public static String createRandomString(char[] pattern, int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int number = new Random().nextInt(pattern.length);
            char ch = pattern[number];
            builder.append(ch);
        }

        return builder.toString();
    }

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE);
        return dateToStringWithFormat(date, dateFormat, timeZone);
    }

    public static String dateToStringWithFormat(Date date, DateFormat dateFormat, TimeZone timeZone) {
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static <T extends Object> List<T> getUnmodifiableList(List<T> list) {
        return list == null || list.isEmpty() ? Collections.EMPTY_LIST : Collections.unmodifiableList(list);
    }
}
