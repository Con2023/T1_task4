# Переходим в папку с Kafka
Set-Location "E:\Kafka\kafka_2.13-3.9.1"

# Запуск Zookeeper в отдельном окне PowerShell (окно останется открытым)
Start-Process powershell -ArgumentList "-NoExit", "-Command", ".\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties"

# Ждем 10 секунд, чтобы Zookeeper успел запуститься
Start-Sleep -Seconds 10

# Запуск Kafka в отдельном окне PowerShell и получение процесса для проверки
Start-Process powershell -ArgumentList "-NoExit", "-Command", ".\bin\windows\kafka-server-start.bat .\config\server.properties"