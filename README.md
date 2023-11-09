# DAI Lab 3: SMTP

---

## Description

[//]: # (TODO)

## Setup

To run the project, first set up the [MailDev](https://github.com/maildev/maildev) fake SMTP server:

```bash
docker compose up
```

Then build the project using the following command:

```bash
mvn -f smtpclient/pom.xml clean:package
```

Finally, you can run the project by executing the JAR package:

```bash
java -jar smtpclient/target/smtpclient-1.0-SNAPSHOT <arguments>
```

## Using smtpclient

[//]: # (TODO)

## Documentation

[//]: # (TODO)

## Showcase

[//]: # (TODO)
