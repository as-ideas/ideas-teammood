package de.axelspringer.ideas.team.mood.mail;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class MailTemplate {

    public static class ViewParameter {
        public String key;
        public String value;

        public ViewParameter(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static String fromClasspath(String file, ViewParameter... params) {
        try {
            String view = IOUtils.toString(MailTemplate.class.getClassLoader().getResourceAsStream(file), Charsets.UTF_8);
            for (ViewParameter param : params) {
                view = view.replaceAll("\\{" + param.key + "\\}", param.value);
            }
            return view;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
