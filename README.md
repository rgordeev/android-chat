## Настройка и сборка приложения

Перед запуском необходимо сконфигурировать адес внешнего сервера, указав правильные значения в
переменных

```
ru.rgordeev.chat.activities.ChatActivity.WS_URL
```
и
```
ru.rgordeev.chat.activities.MainActivity.AUTH_URL
```

Кроме того следует указать правильные политики безопасности в файле
```
app/src/main/res/xml/network_security_config.xml
```
Выбрав правильные адреса, соответсвующие адресу сервера
```
<domain includeSubdomains="true">192.168.78.116</domain>
```

После этого следует собрать приложение штатными средствами Android Studio или Gradle
и запустить в эмуляторе или на доступном устройстве.