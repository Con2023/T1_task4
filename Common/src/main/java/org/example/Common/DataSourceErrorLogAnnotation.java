package org.example.Common;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//Создаем аннотацию для аспекта(в аспекте будем действовать через annotation). Прописываем через таргет к чему будем применять аспект -> метод,
//RetentionPolicy.RUNTIME показывате что аннотация будет доступна во время выполнения программы и ее результат можно получить.

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface
DataSourceErrorLogAnnotation {
}
