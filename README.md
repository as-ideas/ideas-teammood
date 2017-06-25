# ideas-teammood

## Quickstart

> java -jar -Dde.axelspringer.ideas.team.mood.mail.user={USER} -Dde.axelspringer.ideas.team.mood.mail.password={PASSWORD} -Dde.axelspringer.ideas.team.mood.api.keys={KEY}

## Properties

* de.axelspringer.ideas.team.mood.mail.user
* de.axelspringer.ideas.team.mood.mail.password
* de.axelspringer.ideas.team.mood.api.keys

## E-Mail Template 
https://github.com/leemunroe/responsive-html-email-template

# Let's Encrypt

keytool -keystore letsencrypt-truststore -alias isrgrootx -importcert -file letsencrypt.cer
keytool -keystore letsencrypt-truststore -alias identrust -importcert -file identrust.cer